FROM gradle:8.1.1-jdk17

WORKDIR /usr/src
COPY . /usr/src
VOLUME /tmp

RUN chmod +x run.sh && gradle updateLib

EXPOSE 8080

ENTRYPOINT [ "sh" , "run.sh" ]