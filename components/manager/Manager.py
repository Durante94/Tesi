from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
from multiprocessing import Process, Manager
import socket
import os
import json
import time
import falcon


class List(object):
    def __init__(self, agentDict):
        self.agentDict = agentDict

    def on_get(self, req, resp):
        resp.body = json.dumps(self.agentDict.keys())


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    # else:
    #     print("Message produced: %s" % (str(msg)))


def agent_check(conf, agentDict):
    while True:
        producer = Producer(conf)
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


agentDict = Manager().dict()
app = falcon.App()
agentsEndpoint = List(agentDict)
app.add_route('/', agentsEndpoint)

# conf = {'bootstrap.servers': os.getenv("KAFKA_HOST"),
#         'client.id': socket.gethostname()}
# consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
#                      'group.id': "manager",
#                      'auto.offset.reset': 'smallest'})

# agent_controller_task = Process(target=agent_check, args=(conf, agentDict))
# agent_controller_task.start()

# consumer.subscribe(['heartbeat'])
# try:
#     while True:
#         try:
#             msg = consumer.poll(timeout=60.0)
#             if msg is None:
#                 continue

#             if msg.error():
#                 if msg.error().code() == KafkaError._PARTITION_EOF:
#                     print('%% %s [%d] reached end at offset %d\n' %
#                           (msg.topic(), msg.partition(), msg.offset()))
#                 elif msg.error():
#                     raise KafkaException(msg.error())
#             else:
#                 key = msg.key().decode("utf-8").removeprefix(os.getenv("PARTIAL_KEY"))
#                 agentDict[key] = time.time()
#         except KafkaException as e:
#             print(e)
# finally:
#     consumer.close()
#     agent_controller_task.terminate()
