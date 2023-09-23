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

idEmulator = str(uuid.uuid4())

# Set logging level: DEBUG, INFO, WARNING, ERROR, CRITICAL
logging.basicConfig(level=logging.INFO)
logging.info(idEmulator)


def acked(err, msg):
    if err is not None:
        logging.error("Failed to deliver message: %s: %s" %
                      (str(msg), str(err)))
    else:
        logging.debug("Message produced: %s" % (str(msg)))


def generate_solar_irradiance(time, amplitude, **args):
    period = 24*60*60
    mean_irradiance = 1000
    irradiance = mean_irradiance + amplitude * \
        np.sin(2 * np.pi / period * time)
    return {"value": irradiance, "measure_unit": "W/m^2"}


def generate_wind_speed(prev, amplitude, **args):
    mean_wind_speed = 5

    wind_speed = mean_wind_speed + \
        np.maximum(prev + np.random.normal(0, amplitude), 0)
    return {"value": wind_speed, "measure_unit": "m/s"}


def generate_rainfall(amplitude, **args):
    mean_rainfall = 2

    rainfall = np.maximum(np.random.normal(mean_rainfall, amplitude), 0)
    return {"value": rainfall, "measure_unit": "mm"}


def producer_task(conf, flag, transmit, function, amplitude, currentId, test=False):
    producer = Producer(conf)
    match function:
        case "solar":
            func = generate_solar_irradiance
        case "wind":
            func = generate_wind_speed
        case "rain":
            func = generate_rainfall
        case _:
            raise Exception()
    value = {"value": 0}
    t = 1
    logging.debug("Data task started")
    while flag.get() and transmit.get():
        time.sleep(1)
        value = func(time=t, prev=value["value"], amplitude=amplitude)
        logging.debug(value)
        value["agent"] = currentId
        if test:
            return value
        producer.produce("data",
                         key=currentId,
                         value=json.dumps(value) .encode('utf-8'),
                         callback=acked)
        producer.poll(2)
        t = t + 1


def heartbeat_task(conf, flag, sleepTime, currentId, test=False):
    time.sleep(random.randint(1, 10))
    producer = Producer(conf)
    logging.debug("Heartbeat task started")
    while flag.get():
        time.sleep(sleepTime)
        if test:
            return currentId
        producer.produce("heartbeat",
                         key=currentId,
                         callback=acked)
        logging.debug("Heartbeat produced %s" % currentId)


def msg_elaboration(msg, producer, current_data_task, conf, exit_flag, transmit_flag, function, amplitude, id, test=False):
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
                    'function': function
                }
                if test:
                    return producedDict
                producer.produce('config-response',
                                 key=id,
                                 value=json.dumps(producedDict).encode(),
                                 callback=acked)
            case b'toggle':
                if eval(str(msgValue["payload"]).capitalize()):
                    logging.debug("Data task start")
                    transmit_flag.set(True)
                    data_task = Process(target=producer_task, args=(
                        conf, exit_flag, transmit_flag, function, amplitude, id))
                    data_task.start()
                    return data_task
                else:
                    logging.debug("Data task stop")
                    transmit_flag.set(False)
                    current_data_task.terminate()


manager = Manager()
exit_flag = manager.Value('i', True)
transmit_flag = manager.Value('i', True)
conf = {
    'bootstrap.servers': os.getenv("KAFKA_HOST"),
    'client.id': "simulatore-" + idEmulator
}
logging.debug("Kafka configuration: %s" % conf)
logging.debug("KAFKA_HOST: %s" % os.getenv("KAFKA_HOST"))

consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                    'group.id': os.getenv("KAFKA_GROUP"),
                     'auto.offset.reset': 'latest'})

time.sleep(random.randint(1, 10))

producer = Producer(conf)
type_emulator = os.getenv("TYPE")
amplitude = float(os.getenv("AMPLITUDE"))
hb_rate = int(os.getenv("HB_RATE"))

heartbeat_process = Process(target=heartbeat_task,
                            args=(conf.copy(), exit_flag, hb_rate, idEmulator))
data_task = None
heartbeat_process.start()
consumer.subscribe(['config-request'])

try:
    logging.info("Started")
    while not eval(os.getenv("TEST")):
        try:
            msg = consumer.poll(timeout=10.0)
            logging.debug("Message consumed: %s" % str(msg))
            data_task = msg_elaboration(msg, producer, data_task, conf.copy(
            ), exit_flag, transmit_flag, type_emulator, amplitude, idEmulator)
        except KafkaException as e:
            logging.error("KafkaException: %s" % e)
finally:
    consumer.close()
    exit_flag.set(False)
    transmit_flag.set(False)
    heartbeat_process.terminate()
    if data_task and data_task.is_alive():
        data_task.terminate()
