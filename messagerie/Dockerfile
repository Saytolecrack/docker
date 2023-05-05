FROM debian:latest

RUN apt-get update && apt-get install -y default-jdk

COPY ./*.java /app/

RUN javac /app/*.java

CMD ["java", "-classpath", "/app", "ChatServer"]

EXPOSE 9000 