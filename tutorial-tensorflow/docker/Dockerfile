FROM python:3

RUN pip install pandas
RUN pip install tensorflow
RUN pip install hug
RUN pip install uwsgi

ENV app=template

ADD $app app

EXPOSE 8000

CMD ["uwsgi", "--http","0.0.0.0:8000", "--wsgi-file", "/app/handlers/TensorFlowWideNDeepHug.py", "--callable", "__hug_wsgi__", "--worker-reload-mercy", "300", "--http-timeout","600", "--enable-threads"]
