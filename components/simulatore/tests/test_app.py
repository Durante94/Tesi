import unittest
from unittest.mock import MagicMock
from confluent_kafka import KafkaError, Message
from confluent_kafka.cimpl import KafkaException, KafkaError
from Simulatore import acked, id, msg_elaboration, heartbeat_process


class TestAcked(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_process.terminate()

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

        # Call the acked callback function
        acked(err, msg)

        self.assertEqual("Test", err.return_value)
        self.assertEqual("Test", msg.return_value)


class TestMsgElaboration(unittest.TestCase):
    def setUp(self) -> None:
        heartbeat_process.terminate()
        self.mockProducer = MagicMock()
        self.mockProducer.produce.return_value = None

    def test_no_msg(self):
        # Call the heartbeat_check function
        res = next(msg_elaboration(None, self.mockProducer, None, None,
                                   True, False, '', '', '', '', True))

        self.assertIsNone(res)

    def test_get_error_msg(self):
        errorObj = MagicMock()
        errorObj.code.return_value = KafkaError._PARTITION_EOF

        msg = MagicMock(spec=Message)
        msg.error.return_value = errorObj

        # Call the heartbeat_check function
        res = next(msg_elaboration(msg, self.mockProducer, None, None,
                                   True, False, '', '', '', '', True))

        self.assertIsNone(res)

    def test_get_error2_msg(self):
        errorObj = MagicMock()
        errorObj.code.return_value = KafkaError._BAD_MSG

        msg = MagicMock(spec=Message)
        msg.error.return_value = errorObj

        try:
            msg_elaboration(msg, self.mockProducer, None, None,
                            True, False, '', '', '', '', True)
            self.fail("Exception expected")
        except KafkaException as ke:
            self.assertIsNotNone(ke)

    def test_get_valid_msg_id_not_valid(self):
        msg = MagicMock(spec=Message)
        msg.value.return_value = b'{}'
        msg.error.return_value = False

        # Call the heartbeat_check function
        res = next(msg_elaboration(msg, self.mockProducer, None, None,
                                   True, False, '', '', '', id, True))

        self.assertIsNone(res)

        msg.value.return_value = b'{"id": -1}'
        msg.error.return_value = False

        # Call the heartbeat_check function
        res = next(msg_elaboration(msg, self.mockProducer, None, None,
                                   True, False, '', '', '', id, True))

        self.assertIsNone(res)

    def test_get_config_request(self):
        msg = MagicMock(spec=Message)
        msg.value.return_value = b'{"id": '+id.encode()+b'}'
        msg.error.return_value = False
        msg.key.return_value = b'request'

        expected_dict = {
            'function': 'Test',
            'amplitude': 'Test',
            'frequency': 'Test'
        }

        # Call the heartbeat_check function
        res = next(msg_elaboration(msg, self.mockProducer, None, None, True, False,
                   expected_dict['function'], expected_dict['amplitude'], expected_dict['frequency'], id, True))

        self.assertEqual(expected_dict, res)


if __name__ == '__main__':
    unittest.main()
