# social-chat-auth-service

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
