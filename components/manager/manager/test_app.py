from multiprocessing import Manager
from unittest import TestCase
from falcon import testing
import app


class TestList(testing.TestCase):
    def setUp(self):
        super(TestList, self).setUp()
        self.app = app


class TestGet(TestList):
    def test_on_get(self):
        result = self.simulate_get('/')
        self.assertEqual(result.json, [])
