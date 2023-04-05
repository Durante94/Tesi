from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import socket
import os
from multiprocessing import Process
import json
import time

agentDict = {}


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    # else:
    #     print("Message produced: %s" % (str(msg)))


def agent_check(conf):
    while True:
        producer = Producer(conf)
        now = time.time()
        hbVal = int(os.getenv("HB_RATE"))
        global agentDict
        print(now, hbVal, agentDict)
        for id in agentDict.keys():
            print(agentDict[id], hbVal+int(os.getenv("HB_RATE_TOL")),
                  now-agentDict[id] > hbVal+int(os.getenv("HB_RATE_TOL")))
            if now-agentDict[id] > hbVal+int(os.getenv("HB_RATE_TOL")):
                print("dio")
                producer.produce("alarm",
                                 key=id,
                                 value=json.dumps({
                                     'type': "connection",
                                     'time': now,
                                     'lastHB': agentDict[id]
                                 }).encode(),
                                 callback=acked)
        time.sleep(hbVal)


conf = {'bootstrap.servers': os.getenv("KAFKA_HOST"),
        'client.id': socket.gethostname()}
consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                     'group.id': "manager",
                     'auto.offset.reset': 'smallest'})
producer = Producer(conf)

agent_controller_task = Process(target=agent_check, args=(conf,))
agent_controller_task.start()

consumer.subscribe(['heartbeat'])
try:
    while True:
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
                print(key, agentDict)
                agentDict[key] = time.time()
        except Exception as e:
            print(e)
finally:
    consumer.close()
    agent_controller_task.terminate()
