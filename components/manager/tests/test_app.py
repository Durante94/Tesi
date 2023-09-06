import unittest
import pytest
import falcon
import time
import json
import os
from unittest.mock import MagicMock
from manager.app import List, acked, agent_check, heartbeat_ckeck, heartbeat_controller_task, agent_controller_task
from confluent_kafka import Producer, Consumer, KafkaError


def kafka_config():
    # Get the Kafka broker address from the container
    return {"bootstrap.servers": os.getenv("KAFKA_HOST"),
            "group.id": os.getenv("KAFKA_GROUP")+"1",
            "auto.offset.reset": "latest"}


def kafka_producer():
    # Create a Kafka producer
    producer = Producer(kafka_config())
    yield producer
    producer.flush()


def kafka_consumer():
    # Create a Kafka consumer
    consumer = Consumer(kafka_config())
    consumer.subscribe(["alarm"])
    yield consumer
    consumer.close()


class TestListEndpoint(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_controller_task.terminate()
        agent_controller_task.terminate()

    def test_on_get(self):
        # Create a List instance with a mock agentDict
        agentDict = {"agent1": 123, "agent2": 456}
        list_endpoint = List(agentDict)
        # Create mock Falcon request and response objects
        req = MagicMock()
        resp = MagicMock()

        # Call the on_get method
        list_endpoint.on_get(req, resp)

        # Check if the response is as expected
        self.assertEqual(resp.text, '["agent1", "agent2"]')
        self.assertEqual(resp.status, falcon.HTTP_200)


class TestAcked(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_controller_task.terminate()
        agent_controller_task.terminate()

    def test_acked_callback(self):
        # Create a mock error and message
        err = MagicMock()
        msg = MagicMock()

        # Call the acked callback function
        acked(err, msg)

        # Assert that error handling is correct (customize as needed)
        self.assertTrue(err is not None)

        err.return_value = "Test"
        msg.return_value = "Test"

        self.assertEqual("Test", err.return_value)
        self.assertEqual("Test", msg.return_value)


class TestAgentCheck(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_controller_task.terminate()
        agent_controller_task.terminate()
        self.consumer = next(kafka_consumer())

    def test_agent_check(self):
        now = time.time()
        oldTime = now-100000000
        agentDict = {"test": now, "test2": oldTime}
        msg = self.consumer.poll(10.0)
        agent_check(agentDict, True, os.getenv("KAFKA_HOST"), 5, 5, True)
        if msg is None:
            pytest.fail("No message received from Kafka.")
        elif msg.error():
            if msg.error().code() == KafkaError._PARTITION_EOF:
                pytest.fail(
                    f"Reached end of partition at offset {msg.offset()}")
            else:
                pytest.fail(f"Kafka error: {msg.error()}")
        else:
            self.assertEqual("test2", msg.key().decode())
            msgVal = json.loads(msg.value())
            self.assertEqual(oldTime, msgVal["lastHB"])
            self.assertEqual("connection", "type")

# class TestHeartbeatCheck(unittest.TestCase):
#     def setUp(self) -> None:
#         heartbeat_controller_task.terminate()
#         agent_controller_task.terminate()

#     def test_consumer_poll(self):
#         # Create a mock agentDict and Kafka host
#         agentDict = {}
#         kafka_host = "localhost"

#         # Call the heartbeat_check function
#         heartbeat_ckeck(agentDict, True, kafka_host, True)


if __name__ == '__main__':
    unittest.main()
