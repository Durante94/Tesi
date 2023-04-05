import numpy as np
from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import socket
import os
from multiprocessing import Process
import json
import time
import uuid

id = str(uuid.uuid4())


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    # else:
    #     print("Message produced: %s" % (str(msg)))


def producer_task(conf):
    print(conf)
    producer = Producer(conf)

    function = os.getenv("MATH_FUN")
    amplitude = float(os.getenv('AMPLITUDE'))
    frequency = float(os.getenv('FREQUENCY'))
    math_func = getattr(np, function)
    t = 1

    while True:
        time.sleep(1)
        value = np.array2string(
            amplitude * math_func(t + np.pi / frequency))
        producer.produce("data",
                         key=os.getenv("PARTIAL_KEY") + id,
                         value=value.encode('utf-8'),
                         callback=acked)
        producer.poll(2)
        t = t + 1


def heartbeat_task(conf):
    producer = Producer(conf)
    while True:
        time.sleep(int(os.getenv("HB_RATE")))
        producer.produce("heartbeat",
                         key=os.getenv("PARTIAL_KEY") + id,
                         callback=acked)


conf = {'bootstrap.servers': os.getenv("KAFKA_HOST"),
        'client.id': socket.gethostname()}
consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                     'group.id': "simulatore",
                     'auto.offset.reset': 'earliest'})
producer = Producer(conf)
function = os.getenv("MATH_FUN")
amplitude = float(os.getenv('AMPLITUDE'))
frequency = float(os.getenv('FREQUENCY'))

data_task = Process(target=producer_task, args=(conf,))
heartbeat_process = Process(target=heartbeat_task, args=(conf,))
heartbeat_process.start()
consumer.subscribe(['config-request'])
print(id)
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
                try:
                    msgValue = json.loads(msg.value())
                except Exception as e:
                    print("Error during deseralization", e, sep=": ")
                    continue

                if msgValue.get('id') != id:
                    continue

                match msg.key():
                    case b'request':
                        producer.produce('config-response',
                                         key=os.getenv("PARTIAL_KEY") + id,
                                         value=json.dumps({
                                             'function': function,
                                             'amplitude': amplitude,
                                             'frequency': frequency
                                         }).encode(),
                                         callback=acked)
                    case b'toggle':
                        if bool(msgValue["payload"].lower().capitalize()):
                            data_task.start()
                        else:
                            data_task.terminate()
        except Exception as e:
            print(e)
finally:
    consumer.close()
    data_task.terminate()
    heartbeat_process.terminate()
