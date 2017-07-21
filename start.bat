echo "::::::::::STARTING AUTHORIZATION SERVICE:::::::::::"
cd %~dp0
call gradlew build -x test
call java -jar build/libs/authorization-service-0.0.1-SNAPSHOT.jar
