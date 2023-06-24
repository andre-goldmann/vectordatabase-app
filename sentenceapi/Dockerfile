FROM python:3.9

WORKDIR /code

COPY ./requirements.txt /code/requirements.txt

#RUN apt install ffmpeg
RUN apt-get install python3-bs4
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt

#
COPY ./main.py /code/

#
CMD ["uvicorn", "main:app", "--proxy-headers", "--host", "0.0.0.0", "--port", "6081"]