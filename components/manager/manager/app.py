from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
from multiprocessing import Process, Manager
import socket
import os
import json
import time
import falcon
import logging

# Set logging level: DEBUG, INFO, WARNING, ERROR, CRITICAL
logging.basicConfig(level=logging.INFO)


class List:
    def __init__(self, agentDict):
        self.agentDict = agentDict

    def on_get(self, req, resp):
        resp.text = json.dumps(list(self.agentDict.keys()))
        resp.status = falcon.HTTP_200


def acked(err, msg):
    if err is not None:
        logging.error("Failed to deliver message: %s: %s" %
                      (str(msg), str(err)))
    # else:
    #     logging.debug("Message produced: %s" % (str(msg)))


def agent_check(agentDict, execute, kafka, hbVal, hbTol, test=False):
    time.sleep(10)
    producer = Producer({"bootstrap.servers": kafka,
                        "client.id": socket.gethostname()})
    alarmSended = {}
    while execute:
        keys_to_remove = []
        now = time.time()
        try:
            iterated_dict = agentDict.copy()
        except BrokenPipeError as e:
            logging.error(e)
            iterated_dict = {}

        for id in iterated_dict.keys():
            if now - iterated_dict[id] > hbVal + hbTol:
                if alarmSended.get(id) == None:
                    producer.produce(
                        "alarm",
                        key=id,
                        value=json.dumps(
                            {"type": "connection", "time": now,
                                "lastHB": iterated_dict[id]}
                        ).encode(),
                        callback=acked,
                    )
                    alarmSended[id] = False
                elif alarmSended.get(id):
                    keys_to_remove.append(id)
                else:
                    alarmSended[id] = True
        for id in keys_to_remove:
            del agentDict[id]
        producer.flush()
        time.sleep(hbVal)
        if test:
            break
    logging.debug("Agent check process closed")


def msg_elaboration(msg, agentDict):
    if msg is None:
        logging.debug("Messagge null")
        return

    if msg.error():
        logging.debug("Messagge broken")
        if msg.error().code() == KafkaError._PARTITION_EOF:
            logging.info(
                "%% %s [%d] reached end at offset %d\n"
                % (msg.topic(), msg.partition(), msg.offset())
            )
        elif msg.error():
            raise KafkaException(msg.error())
    else:
        logging.debug("Messagge consumed")
        key = msg.key().decode("utf-8")
        agentDict[key] = time.time()


def heartbeat_ckeck(agentDict, execute, kafka, test=False):
    consumer = Consumer(
        {
            "bootstrap.servers": kafka,
            "group.id": os.getenv("KAFKA_GROUP"),
            "auto.offset.reset": os.getenv("COSUMER_CONF"),
        }
    )
    logging.debug("Consumer connected")
    consumer.subscribe(["heartbeat"])
    logging.debug("Consumer subscribed")
    while execute:
        try:
            msg = consumer.poll(timeout=10.0)
            logging.debug("Messagge consumed")
            msg_elaboration(msg, agentDict)
        except KafkaException as e:
            logging.error(e)
        except Exception as general:
            logging.error(general)
        if test:
            break
    consumer.close()
    logging.debug("Heartbeat process closed")


concurrentManager = Manager()
agentDict = concurrentManager.dict()
closeFlag = concurrentManager.Value("i", True)

kafkaHost = os.getenv("KAFKA_HOST")
hbVal = int(os.getenv("HB_RATE"))
hbTol = int(os.getenv("HB_RATE_TOL"))

agent_controller_task = Process(
    target=agent_check, args=(agentDict, closeFlag, kafkaHost, hbVal, hbTol)
)
heartbeat_controller_task = Process(
    target=heartbeat_ckeck, args=(agentDict, closeFlag, kafkaHost)
)
agent_controller_task.start()
heartbeat_controller_task.start()

# RESTFul init
app = application = falcon.App()
agentsEndpoint = List(agentDict)
app.add_route("/", agentsEndpoint)

closeFlag.set(False)
# agent_controller_task.terminate()
# heartbeat_controller_task.terminate()
