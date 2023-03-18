FROM python:3.9
COPY requirements.txt ./
RUN pip install --upgrade pip \
    pip install -r requirements.txt
COPY Simulatore.py ./
ENV kafka_host=localhost:9092 \
    math_fun=sin \
    amplitude=6 \
    frequency=4.2 \
    topic=test
CMD ["python", "./Simulatore.py"]