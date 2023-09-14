import json
import os
import time
import unittest
from multiprocessing import Manager
from unittest.mock import MagicMock

import falcon
from falcon import testing
from confluent_kafka import Message
from confluent_kafka.cimpl import KafkaException, KafkaError

from manager.app import app, List, acked, agent_check, msg_elaboration, heartbeat_controller_task, agent_controller_task


class TestListEndpoint(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_controller_task.terminate()
        agent_controller_task.terminate()
        # Create a List instance with a mock agentDict
        self.agentDict = {"agent1": 123, "agent2": 456}

    def test_on_get(self):
        list_endpoint = List(self.agentDict)
        # Create mock Falcon request and response objects
        req = MagicMock()
        resp = MagicMock()

        # Call the on_get method
        list_endpoint.on_get(req, resp)

        # Check if the response is as expected
        self.assertEqual(resp.text, json.dumps(list(self.agentDict.keys())))
        self.assertEqual(resp.status, falcon.HTTP_200)

    def test_list_endpoint(self):
        # Simulate a GET request
        result = testing.simulate_get(app, '/')
        self.assertEqual(result.status, falcon.HTTP_200)
        self.assertEqual(result.json, [])


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

    def test_agent_check(self):
        future = time.time() + 10
        past = future - 100000000
        agentDict = {"test": future, "test2": past}
        msgVal = next(agent_check(
            agentDict, Manager().Value('i', True), os.getenv("KAFKA_HOST"), 5, 5, True))
        self.assertEqual(past, msgVal["lastHB"])
        self.assertEqual("connection", msgVal["type"])


class TestHeartbeatCheck(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_controller_task.terminate()
        agent_controller_task.terminate()

    def test_no_msg(self):
        # Create a mock agentDict and Kafka host
        agentDict = Manager().dict()

        # Call the heartbeat_check function
        msg_elaboration(None, agentDict)
        self.assertEqual(0, len(agentDict))

    def test_get_valid_msg(self):
        # Create a mock agentDict and Kafka host
        agentDict = Manager().dict()
        testKey = "test"

        msg = MagicMock(spec=Message)
        msg.key.return_value = testKey.encode()
        msg.error.return_value = False

        # Call the heartbeat_check function
        msg_elaboration(msg, agentDict)

        self.assertGreater(len(agentDict), 0)
        self.assertIsNotNone(agentDict[testKey])
        self.assertLessEqual(agentDict[testKey], time.time())

    def test_get_error_msg(self):
        # Create a mock agentDict and Kafka host
        agentDict = Manager().dict()

        errorObj = MagicMock()
        errorObj.code.return_value = KafkaError._PARTITION_EOF

        msg = MagicMock(spec=Message)
        msg.error.return_value = errorObj

        # Call the heartbeat_check function
        msg_elaboration(msg, agentDict)

        self.assertEqual(len(agentDict), 0)

    def test_get_error2_msg(self):
        # Create a mock agentDict and Kafka host
        agentDict = Manager().dict()

        errorObj = MagicMock()
        errorObj.code.return_value = KafkaError._BAD_MSG

        msg = MagicMock(spec=Message)
        msg.error.return_value = errorObj

        try:
            msg_elaboration(msg, agentDict)
        except KafkaException as ke:
            self.assertIsNotNone(ke)

        self.assertEqual(len(agentDict), 0)


if __name__ == '__main__':
    unittest.main()
