FROM openjdk:8-jdk-alpine

LABEL maintainer="willian.azevedo (willian-mga@hotmail.com)"

ADD target/reactive-chat-auth-service.jar /opt/auth/reactive-chat-auth-service.jar

WORKDIR /opt/chat

EXPOSE 8080

ENV CHAT_MONGO_SERVER=localhost:27017
ENV CHAT_MONGO_USERNAME=evitcaer
ENV CHAT_MONGO_PASSWORD=johnjones
ENV CHAT_MONGO_CONNECTION_STRING=mongodb://%s:%s@%s/%s
ENV CHAT_MONGO_DATABASE=socialchat
ENV CHAT_MONGO_AUTH_DATABASE=admin

CMD java -jar reactive-chat-auth-service.jar