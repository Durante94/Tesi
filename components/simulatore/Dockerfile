FROM python:3.10
COPY requirements.txt ./
RUN pip install --upgrade pip \
    && pip install -r requirements.txt
COPY Simulatore.py ./
ENV KAFKA_HOST=localhost:9092 \
    MATH_FUN=sin \
    AMPLITUDE=6 \
    FREQUENCY=4.2 \
    PARTIAL_KEY=simulatore \
    HB_RATE=5
CMD ["python", "./Simulatore.py"]