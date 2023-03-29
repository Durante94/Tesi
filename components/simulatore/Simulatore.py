import numpy as np
from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import socket
import os
from multiprocessing import Process
import json
import time


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    # else:
    #     print("Message produced: %s" % (str(msg)))


def consume_loop(conf):
    consumer = Consumer({'bootstrap.servers': os.getenv("kafka_host"),
                         'group.id': "foo",
                         'auto.offset.reset': 'smallest'})
    producer = Producer(conf)
    function = os.getenv("math_fun")
    amplitude = float(os.getenv('amplitude'))
    frequency = float(os.getenv('frequency'))

    consumer.subscribe(['request-config'])
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
                    producer.produce('request-response',
                                    key="simulatore-conf",
                                    value=json.dumps({
                                        'function': function,
                                        'amplitude': amplitude,
                                        'frequency': frequency
                                    }),
                                    callback=acked)
            except:
                continue
    finally:
        consumer.close()


conf = {'bootstrap.servers': os.getenv("kafka_host"),
        'client.id': socket.gethostname()}
producer = Producer(conf)

function = os.getenv("math_fun")
amplitude = float(os.getenv('amplitude'))
frequency = float(os.getenv('frequency'))
math_func = getattr(np, function)
t = 1

# consume_loop(conf)
task = Process(target=consume_loop, args=(conf,))
task.start()

try:
    while True:
        time.sleep(1)
        value = np.array2string(
            amplitude * math_func(t + np.pi / frequency))
        producer.produce(os.getenv('topic'),
                         key="simulatore",
                         value=value.encode('utf-8'),
                         callback=acked)
        producer.poll(2)
        t = t + 1
finally:
    task.kill()