# Crazy Eight

Networked game that allows up to four players to play in real-time. This is achieved with the use of websockets.
This project uses Spring Boot with WebFlux for handling server requests and the client code is written in TypeScript.

# Directory Structure
- `acceptance`Acceptance tests written using Selenium
- `backend` Backend application written in Spring Boot
- `frontend` Frontend application written in React
- `bin` Directory that will contain the final 'fat' JAR containing bundled backend and frontend applications

# Run Acceptance Tests
To run `selenium` based acceptance test, execute the 'Build & Test' run configuration in IntelliJ.