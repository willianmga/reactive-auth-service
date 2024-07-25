FROM maven:3.8.5-jdk-8 AS builder
COPY . /opt/socialchat/auth
WORKDIR /opt/socialchat/auth
RUN mvn clean package

FROM openjdk:18-jdk-alpine

LABEL maintainer="willian.azevedo (willian-mga@hotmail.com)"
COPY --from=builder /opt/socialchat/auth/target/social-chat-auth-service.jar /opt/socialchat/social-chat-auth-service.jar
WORKDIR /opt/socialchat

ENV PORT=8080
ENV CHAT_MONGO_SERVER=localhost:27017
ENV CHAT_MONGO_USERNAME=evitcaer
ENV CHAT_MONGO_PASSWORD=johnjones
ENV CHAT_MONGO_CONNECTION_STRING=mongodb://%s:%s@%s/%s
ENV CHAT_MONGO_DATABASE=socialchat
ENV CHAT_MONGO_AUTH_DATABASE=admin
ENV JWT_SECRET_KEY=asdf

EXPOSE 8080

CMD ["java", "-jar", "social-chat-auth-service.jar", "--spring.profiles.active=prod", "--server.port=8080"]