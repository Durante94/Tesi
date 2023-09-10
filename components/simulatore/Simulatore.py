import numpy as np
from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import os
from multiprocessing import Process, Manager
import json
import time
import uuid
import logging
import random

id = str(uuid.uuid4())

# Set logging level: DEBUG, INFO, WARNING, ERROR, CRITICAL
logging.basicConfig(level=logging.INFO)
logging.info(id)


def acked(err, msg):
    if err is not None:
        logging.error("Failed to deliver message: %s: %s" %
                      (str(msg), str(err)))
    else:
        logging.debug("Message produced: %s" % (str(msg)))


def producer_task(conf, flag, transmit, function, amplitude, frequency, currentId, test=False):
    producer = Producer(conf)
    math_func = getattr(np, function)
    t = 1
    logging.debug("Data task started")
    while flag and transmit:
        time.sleep(1)
        value = np.array2string(
            amplitude * math_func(t + np.pi / frequency))
        logging.debug(value)
        yield value
        producer.produce("data",
                         key=currentId,
                         value=json.dumps(
                             {"value": value, "agent": id}) .encode('utf-8'),
                         callback=acked)
        producer.poll(2)
        t = t + 1
        if test:
            break


def heartbeat_task(conf, flag, sleepTime, currentId):
    time.sleep(random.randint(1, 10))
    producer = Producer(conf)
    logging.debug("Heartbeat task started")
    while flag:
        time.sleep(sleepTime)
        producer.produce("heartbeat",
                         key=currentId,
                         callback=acked)
        logging.debug("Heartbeat produced %s" % currentId)


def msg_elaboration(msg, producer, current_data_task, conf, exit_flag, transmit_flag, function, amplitude, frequency, id, test=False):
    if msg is None:
        return None

    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            logging.warning('%% %s [%d] reached end at offset %d\n' %
                            (msg.topic(), msg.partition(), msg.offset()))
            return None
        elif msg.error():
            raise KafkaException(msg.error())
    else:
        try:
            msgValue = json.loads(msg.value())
        except Exception as e:
            logging.error("Error during deserialization: %s" % e)
            return None

        if msgValue.get('id') != id:
            return None

        match msg.key():
            case b'request':
                producedDict = {
                    'function': function,
                    'amplitude': amplitude,
                    'frequency': frequency
                }
                if test:
                    yield producedDict
                producer.produce('config-response',
                                 key=id,
                                 value=json.dumps(producedDict).encode(),
                                 callback=acked)
            case b'toggle':
                if eval(str(msgValue["payload"]).capitalize()):
                    transmit_flag.set(True)
                    data_task = Process(target=producer_task, args=(
                        conf, exit_flag, transmit_flag, function, amplitude, frequency, id))
                    yield data_task
                    data_task.start()
                else:
                    transmit_flag.set(False)
                    current_data_task.terminate()


manager = Manager()
exit_flag = manager.Value('i', True)
transmit_flag = manager.Value('i', True)
conf = {
    'bootstrap.servers': os.getenv("KAFKA_HOST"),
    'client.id': "simulatore-"+id
}
logging.debug("Kafka configuration: %s" % conf)
logging.debug("KAFKA_HOST: %s" % os.getenv("KAFKA_HOST"))

consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                    'group.id': os.getenv("KAFKA_GROUP"),
                     'auto.offset.reset': 'latest'})

time.sleep(random.randint(1, 10))

producer = Producer(conf)
function = os.getenv("MATH_FUN")
amplitude = float(os.getenv("AMPLITUDE"))
frequency = float(os.getenv('FREQUENCY'))
hb_rate = int(os.getenv("HB_RATE"))

heartbeat_process = Process(target=heartbeat_task,
                            args=(conf.copy(), exit_flag, hb_rate, id))
data_task = None
heartbeat_process.start()
consumer.subscribe(['config-request'])

try:
    logging.info("Started")
    while True:
        try:
            msg = consumer.poll(timeout=10.0)
            logging.debug("Message consumed: %s" % str(msg))
            data_task = msg_elaboration(msg, producer, data_task, conf.copy(
            ), exit_flag, transmit_flag, function, amplitude, frequency, id)
        except KafkaException as e:
            logging.error("KafkaException: %s" % e)
finally:
    consumer.close()
    exit_flag.set(False)
    transmit_flag.set(False)
    heartbeat_process.terminate()
    if data_task and data_task.is_alive():
        data_task.terminate()
