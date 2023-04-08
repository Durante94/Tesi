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


def agent_check(agentDict, execute):
    while execute:
        producer = Producer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                             'client.id': socket.gethostname()})
        now = time.time()
        hbVal = int(os.getenv("HB_RATE"))
        for id in agentDict.keys():
            if now-agentDict[id] > hbVal+int(os.getenv("HB_RATE_TOL")):
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


def heartbeat_ckeck(agentDict, execute):
    consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
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
                key = msg.key().decode("utf-8").removeprefix(os.getenv("PARTIAL_KEY"))
                agentDict[key] = time.time()
        except KafkaException as e:
            print(e)
    consumer.close()
    print("Heartbeat process closed")


concurrentManager = Manager()
agentDict = concurrentManager.dict()
closeFlag = concurrentManager.Value('i', True)
agent_controller_task = Process(
    target=agent_check, args=(agentDict, closeFlag))
heartbeat_controller_task = Process(
    target=heartbeat_ckeck, args=(agentDict, closeFlag))
agent_controller_task.start()
heartbeat_controller_task.start()

# RESTFul init
app = application = falcon.App()
agentsEndpoint = List(agentDict)
app.add_route('/', agentsEndpoint)

closeFlag.set(False)
# agent_controller_task.terminate()
# heartbeat_controller_task.terminate()
