import numpy as np
from confluent_kafka import Producer, Consumer
from confluent_kafka.cimpl import KafkaException, KafkaError
import socket
import os
from multiprocessing import Process, Manager
import json
import time
import uuid

id = str(uuid.uuid4())
print(id)

try:
    def acked(err, msg):
        if err is not None:
            print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
        # else:
        #     print("Message produced: %s" % (str(msg)))


    def producer_task(conf, flag, transmit):
        producer = Producer(conf)

        function = os.getenv("MATH_FUN")
        amplitude = float(os.getenv('AMPLITUDE'))
        frequency = float(os.getenv('FREQUENCY'))
        math_func = getattr(np, function)
        t = 1
        # print(conf, function, amplitude, frequency, math_func, sep=", ")

        while flag and transmit:
            time.sleep(1)
            value = np.array2string(
                amplitude * math_func(t + np.pi / frequency))
            producer.produce("data",
                            key=os.getenv("PARTIAL_KEY") + id,
                            value=value.encode('utf-8'),
                            callback=acked)
            producer.poll(2)
            t = t + 1


    def heartbeat_task(conf, flag):
        print("Heartbeat", conf, sep=", ")
        producer = Producer(conf)
        while flag:
            time.sleep(int(os.getenv("HB_RATE")))
            producer.produce("heartbeat",
                            key=os.getenv("PARTIAL_KEY") + id,
                            callback=acked)

    manager = Manager()
    exit_flag = manager.Value('i', True)
    transmit_flag = manager.Value('i', True)
    conf = {
        'bootstrap.servers': os.getenv("KAFKA_HOST"),
        'client.id': socket.gethostname()
    }
    consumer = Consumer({'bootstrap.servers': os.getenv("KAFKA_HOST"),
                        'group.id': "simulatore",
                        'auto.offset.reset': 'earliest'})
    producer = Producer(conf)
    function = os.getenv("MATH_FUN")
    amplitude = float(os.getenv("AMPLITUDE"))
    frequency = float(os.getenv('FREQUENCY'))

    data_task = Process(target=producer_task, args=(
        conf.copy(), exit_flag, transmit_flag))
    heartbeat_process = Process(target=heartbeat_task,
                                args=(conf.copy(), exit_flag))
    heartbeat_process.start()
    consumer.subscribe(['config-request'])
    try:
        print("Started")
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
                    # print(msg)
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
                                transmit_flag.set(True)
                                data_task.start()
                            else:
                                transmit_flag.set(False)
                                # data_task.terminate()
            except KafkaException as e:
                print(e)
    finally:
        consumer.close()
        exit_flag.set(False)
        transmit_flag.set(False)
        heartbeat_process.join()
        if data_task.is_alive():
            data_task.join()
except e:
    print(e)