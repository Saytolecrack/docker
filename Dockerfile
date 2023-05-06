FROM debian:latest

RUN apt-get update && apt-get install -y default-jdk

COPY ./*.java /app/

RUN javac /app/*.java

RUN echo curl ifconfig.me

EXPOSE 9000 

CMD ["java", "-classpath", "/app", "ChatServer"]
