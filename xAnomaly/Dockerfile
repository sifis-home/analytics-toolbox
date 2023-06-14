# syntax=docker/dockerfile:1

FROM python:3.8-bullseye

# WORKDIR /home/sm/ml
ENV PYTHONUNBUFFERED=1

COPY serving/requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY serving/ .

#CMD [ "python3", "-u", "-m" , "flask", "run", "--host=0.0.0.0", "--port=5001"]
CMD [ "python3", "-u", "-m" , "serving.py"]

EXPOSE 5003

# # Keeps Python from generating .pyc files in the container
# ENV PYTHONDONTWRITEBYTECODE=1

# # Turns off buffering for easier container logging
# ENV PYTHONUNBUFFERED=1

# # Install pip requirements
# COPY requirements.txt .
# RUN python -m pip install -r requirements.txt

# WORKDIR /reportern
# COPY . /app

# # Creates a non-root user with an explicit UID and adds permission to access the /app folder
# # For more info, please refer to https://aka.ms/vscode-docker-python-configure-containers
# RUN adduser -u 5678 --disabled-password --gecos "" appuser && chown -R appuser /app
# USER appuser

# # During debugging, this entry point will be overridden. For more information, please refer to https://aka.ms/vscode-docker-python-debug
# CMD ["gunicorn", "--bind", "0.0.0.0:5000", "app:app"]
