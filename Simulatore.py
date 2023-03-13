import numpy as np
from confluent_kafka import Producer
import socket
import os


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    else:
        print("Message produced: %s" % (str(msg)))


conf = {'bootstrap.servers': os.getenv("kafka_host"),
        'client.id': socket.gethostname()}
producer = Producer(conf)

function = os.getenv("math_fun")
amplitude = float(os.getenv('amplitude'))
frequency = float(os.getenv('frequency'))
math_func=getattr(np, function)
t = 1

while True:
    value = np.array2string(
        amplitude * math_func(t + np.pi / frequency))
    producer.produce(os.getenv('topic'),
                     key="simulatore",
                     value=value.encode('utf-8'),
                     callback=acked)
    producer.poll(2)
    t = t + 1
