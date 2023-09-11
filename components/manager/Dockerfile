FROM python:3.10 as tester
COPY requirements.txt ./
COPY manager/ ./manager
COPY tests/ ./tests
COPY pytest.ini ./

RUN pip install --upgrade pip \
    && pip install -r /requirements.txt pytest \
    && virtualenv .venv
ENV KAFKA_HOST=localhost:9092 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5 \
    HB_RATE_TOL=5 \
    KAFKA_GROUP=tesi \
    COSUMER_CONF=latest

RUN pytest --log-level=DEBUG ./tests

FROM python:3.10 as builder
COPY requirements.txt ./
COPY manager/ ./manager
RUN pip install --upgrade pip \
    && pip install -r /requirements.txt \
    && virtualenv .venv
ENV KAFKA_HOST=localhost:9092 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5 \
    HB_RATE_TOL=5 \
    KAFKA_GROUP=tesi \
    COSUMER_CONF=latest
EXPOSE 8000
CMD ["gunicorn", "--bind", "0.0.0.0:8000", "manager.app"]