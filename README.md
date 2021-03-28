# Social Chat Auth Service

Authentication Service of SocialChat

## Links
* Chat: www.socialchat.live
* Live instance: https://www.socialchat.live/api
* Docker image registry: https://hub.docker.com/r/willianmga/social-chat-auth-service

## Related Repos
* Front End: https://github.com/willianmga/social-chat-front
* Chat Server: https://github.com/willianmga/social-chat-service

## Features
* Authentication
* Logoff
* JWT Token Validation

## Technologies
* Spring WebFlux and Spring Boot
* Jetty WebSocket Server
* JWT Tokens
* MongoDB Database
* Docker and Docker compose
* Deployment to AWS and Heroku
* Java 8
* Maven

## Endpoints
* ```POST /v1/auth``` : Authenticates a user and creates a session
* ```POST /v1/auth/logoff``` : Invalidates a previously created session
* ```POST /v1/auth/token/valid``` : Validates a JWT token confirming whether a session is valid or not.

## Environment Variables Accepted
* CHAT_MONGO_SERVER
* CHAT_MONGO_USERNAME
* CHAT_MONGO_PASSWORD
* CHAT_MONGO_AUTH_DATABASE
* CHAT_MONGO_DATABASE
* CHAT_MONGO_CONNECTION_STRING
* JWT_SECRET_KEY
