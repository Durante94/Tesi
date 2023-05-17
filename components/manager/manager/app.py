from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
from multiprocessing import Process, Manager
import socket
import os
import json
import time
import falcon


class List:
    def __init__(self, agentDict):
        self.agentDict = agentDict

    def on_get(self, req, resp):
        resp.text = json.dumps(self.agentDict.keys())
        resp.status = falcon.HTTP_200


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    # else:
    #     print("Message produced: %s" % (str(msg)))


def agent_check(agentDict, execute, kafka, hbVal, hbTol):
    producer = Producer({'bootstrap.servers': kafka,
                         'client.id': socket.gethostname()})
    while execute:
        now = time.time()
        for id in agentDict.keys():
            if now-agentDict[id] > hbVal + hbTol:
                producer.produce("alarm",
                                 key=id,
                                 value=json.dumps({
                                     'type': "connection",
                                     'time': now,
                                     'lastHB': agentDict[id]
                                 }).encode(),
                                 callback=acked)
        time.sleep(hbVal)
    print("Agent check process closed")


def heartbeat_ckeck(agentDict, execute, kafka):
    consumer = Consumer({'bootstrap.servers': kafka,
                        'group.id': "manager",
                         'auto.offset.reset': 'smallest'})
    consumer.subscribe(['heartbeat'])
    while execute:
        try:
            msg = consumer.poll(timeout=60.0)
            if msg is None:
                continue

            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    print('%% %s [%d] reached end at offset %d\n' %
                          (msg.topic(), msg.partition(), msg.offset()))
                elif msg.error():
                    raise KafkaException(msg.error())
            else:
                key = msg.key().decode("utf-8")
                agentDict[key] = time.time()
        except KafkaException as e:
            print(e)
    consumer.close()
    print("Heartbeat process closed")


concurrentManager = Manager()
agentDict = concurrentManager.dict()
closeFlag = concurrentManager.Value('i', True)

kafkaHost = os.getenv("KAFKA_HOST")
hbVal = int(os.getenv("HB_RATE"))
hbTol = int(os.getenv("HB_RATE_TOL"))

agent_controller_task = Process(
    target=agent_check, args=(agentDict, closeFlag, kafkaHost, hbVal, hbTol))
heartbeat_controller_task = Process(
    target=heartbeat_ckeck, args=(agentDict, closeFlag, kafkaHost))
agent_controller_task.start()
heartbeat_controller_task.start()

# RESTFul init
app = application = falcon.App()
agentsEndpoint = List(agentDict)
app.add_route('/', agentsEndpoint)

closeFlag.set(False)
# agent_controller_task.terminate()
# heartbeat_controller_task.terminate()
