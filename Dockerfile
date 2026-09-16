FROM python:3.10-slim
WORKDIR /app
COPY Greatest.py .
EXPOSE 5000
CMD ["python", "Greatest.py"]
