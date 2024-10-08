<h1 style="font-size: 48px;">Cities-web-app</h1>

# Summary
cities-web-app is a WEB application is an application that allows the user to play the popular game ‘Cities’ - where the server first sends the first city to the user and the user has to name another place that starts with the same letter as the previous one, you can use 1 name only once. This game covers the cities of Ukraine.

# Endpoints
- GET:/game/begin - the system offers a city to start the game.
- GET:/game/next - the player enters the word, and the system responds with the city to the last letter of the player's word. (You cannot leave the field empty, enter only in Cyrillic, in case of violations, an error will be signaled).
- POST:/game/end - finish game.

# Project structure
- src/main/java: contains all the source code for the application.
- src/main/resources: contains configuration files and resources.
- src/main/resources/static/: The main folder for static resources.
1. index.html: the main HTML page for web application.
2. styles.css: the styles file for the HTML page. 
3. js: a subfolder for JavaScript files.
4. script.js: the main JavaScript file that handles the logic on the client side.
- checkstyle/checkstyle.xml - is a configuration file for the checkstyle tool, which is used to check the code style. It contains settings for various checkstyle modules that perform various code checks for compliance with style standards.
- pom.xml - used to configure and create a Maven project, add the necessary dependencies.

# Technologies used
- JDK 17
- SpringBoot 3.3.2
- H2 database
- Swagger 2.2.1
- Maven 4.0.0
- Mapstruct 1.5.5

# How to run the application
In order to launch this project, you need to take the following steps:
1. Clone this project from GitHub to your local machine.
2. Install the following software:
- IntelliJ IDEA (IDE) to run the application.
- Install Postman for sending requests, or you can use Swagger UI and you need follow this link to test the application - http://localhost:8080/swagger-ui.html
- If you don't want to just test the application and just start playing, then follow this link - http://localhost:8080 and enjoy the game)
3. Open the project in IntelliJ IDEA.
4. Build the project using Maven: mvn clean package.
5. Once the configuration is complete, click the "Run" button in IntelliJ IDEA to start the application. You can choose either normal mode or debug mode.
6. If all the steps have been followed correctly, the server will start successfully.
7. Use Postman or a web browser to interact with the endpoints and test the application.
   Please follow these instructions carefully to launch the project.
