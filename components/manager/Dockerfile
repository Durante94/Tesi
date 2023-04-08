FROM python:3.10
COPY requirements.txt ./
RUN pip install --upgrade pip \
    && pip install -r requirements.txt \
    && virtualenv .venv \
    && source .venv/bin/activate
COPY manager/ .
ENV KAFKA_HOST=localhost:9092 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5 \
    HB_RATE_TOL=5
CMD ["gunicorn" "manager.app"]