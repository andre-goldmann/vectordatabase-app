FROM maven:3.8.2-eclipse-temurin-17

WORKDIR /usr/src/app

COPY . /usr/src/app
RUN mvn package

ENV PORT 4081
EXPOSE $PORT
CMD [ "sh", "-c", "mvn -Dserver.port=${PORT} spring-boot:run" ]
