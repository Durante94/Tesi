FROM python:3.10 as tester
COPY requirements.txt ./
COPY Simulatore.py ./
COPY tests/ ./tests
COPY pytest.ini ./

RUN pip install --upgrade pip \
    && pip install -r /requirements.txt pytest
ENV KAFKA_HOST=localhost:9092 \
    MATH_FUN=sin \
    AMPLITUDE=6 \
    FREQUENCY=4.2 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5 \
    KAFKA_GROUP=tesi \
    TEST=True
RUN pytest --log-level=DEBUG ./tests

FROM python:3.10 as builder
COPY requirements.txt ./
RUN pip install --upgrade pip \
    && pip install -r requirements.txt
COPY Simulatore.py ./
ENV KAFKA_HOST=localhost:9092 \
    MATH_FUN=sin \
    AMPLITUDE=6 \
    FREQUENCY=4.2 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5 \
    KAFKA_GROUP=tesi \
    TEST=False
ENTRYPOINT ["python", "./Simulatore.py"]