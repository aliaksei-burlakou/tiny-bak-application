# Tiny Bank Web Application with Spring Boot and Vaadin Framework

Prerequisites:
* Java 17 or higher
* Git
* (Optionally): Intellij Community

## Building

Run the following command in this repo:

```bash
./gradlew clean build -Pvaadin.productionMode
```

That will build this app in production mode as a runnable jar archive; please find the jar file in `build/libs/tiny-bank-application-x.x.x.jar`.
You can run the JAR file with next command (where x.x.x is an actual version of the application):

```bash
cd build/libs/
java -jar tiny-bank-application-x.x.x.jar
```

Now you can open the [http://localhost:8080](http://localhost:8080) with your browser.

## Functionality
On the main page you will see two buttons: "Login" and "Register".
1. To create user account it is required to click on "Register" button, fill username and password fields and click "Register".
2. To login into application it is required to click "Login" button and enter your username and password.
3. After login, you will see the current balance, fields for input, as well as actions that can be performed with the entered values: deposit, withdrawal, transfer to another user.
4. The “Deactivate” button is highlighted separately. Clicking on it will deactivate the current user.
5. At the very bottom of the page there is a table with the history of the transactions.
