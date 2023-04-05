FROM python:3.10
COPY requirements.txt ./
RUN pip install --upgrade pip \
    && pip install -r requirements.txt
COPY Manager.py ./
ENV KAFKA_HOST=localhost:9092
CMD ["python", "./Manager.py"]