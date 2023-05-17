import numpy as np
from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import socket
import os
from multiprocessing import Process, Manager
import json
import time
import uuid
import logging

id = str(uuid.uuid4())
print(id)

LOG_LEVEL = logging.INFO  # Set logging level: DEBUG, INFO, WARNING, ERROR, CRITICAL

logging.basicConfig(level=LOG_LEVEL)


def acked(err, msg):
    if err is not None:
        logging.error("Failed to deliver message: %s: %s" %
                      (str(msg), str(err)))
    # else:
    #     logging.info("Message produced: %s" % (str(msg)))


def producer_task(conf, flag, transmit, function, amplitude, frequency, currentId):
    producer = Producer(conf)
    math_func = getattr(np, function)
    t = 1

    while flag and transmit:
        time.sleep(1)
        value = np.array2string(
            amplitude * math_func(t + np.pi / frequency))
        producer.produce("data",
                         key=currentId,
                         value=json.dumps(
                             {"value": value}) .encode('utf-8'),
                         callback=acked)
        producer.poll(2)
        t = t + 1


def heartbeat_task(conf, flag, sleepTime, currentId):
    producer = Producer(conf)
    while flag:
        time.sleep(sleepTime)
        producer.produce("heartbeat",
                         key=currentId,
                         callback=acked)


manager = Manager()
exit_flag = manager.Value('i', True)
transmit_flag = manager.Value('i', True)
conf = {
    'bootstrap.servers': os.getenv("KAFKA_HOST"),
    'client.id': socket.gethostname()
}
logging.debug("Kafka configuration: %s" % conf)
logging.info("KAFKA_HOST: %s" % os.getenv("KAFKA_HOST"))

consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                    'group.id': "simulatore",
                     'auto.offset.reset': 'earliest'})
producer = Producer(conf)
function = os.getenv("MATH_FUN")
amplitude = float(os.getenv("AMPLITUDE"))
frequency = float(os.getenv('FREQUENCY'))
hb_rate = int(os.getenv("HB_RATE"))

data_task = Process(target=producer_task, args=(
    conf.copy(), exit_flag, transmit_flag, function, amplitude, frequency, id))
heartbeat_process = Process(target=heartbeat_task,
                            args=(conf.copy(), exit_flag, hb_rate, id))
heartbeat_process.start()
consumer.subscribe(['config-request'])

try:
    logging.info("Started")
    while True:
        try:
            msg = consumer.poll(timeout=60.0)
            logging.info("Message consumed: %s" % str(msg))
            if msg is None:
                continue

            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    logging.warning('%% %s [%d] reached end at offset %d\n' %
                                    (msg.topic(), msg.partition(), msg.offset()))
                elif msg.error():
                    raise KafkaException(msg.error())
            else:
                try:
                    msgValue = json.loads(msg.value())
                except Exception as e:
                    logging.error("Error during deserialization: %s" % e)
                    continue

                if msgValue.get('id') != id:
                    continue

                match msg.key():
                    case b'request':
                        producer.produce('config-response',
                                         key=id,
                                         value=json.dumps({
                                             'function': function,
                                             'amplitude': amplitude,
                                             'frequency': frequency
                                         }).encode(),
                                         callback=acked)
                    case b'toggle':
                        if bool(str(msgValue["payload"]).capitalize()):
                            transmit_flag.set(True)
                            data_task.start()
                        else:
                            transmit_flag.set(False)
                            # data_task.terminate()
        except KafkaException as e:
            logging.error("KafkaException: %s" % e)
finally:
    consumer.close()
    exit_flag.set(False)
    transmit_flag.set(False)
    heartbeat_process.join()
    if data_task.is_alive():
        data_task.join()
