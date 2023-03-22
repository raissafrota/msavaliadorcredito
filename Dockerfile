#FROM maven:3.8.5-openjdk-11 as build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests

#FROM openjdk:11
#WORKDIR /app
#COPY --from=build ./app/target/*.jar ./app.jar

#ARG RABBITMQ_SERVER=rabbitmq-host
#ARG EUREKA_SERVER=localhost

#ENTRYPOINT java -jar -Dspring.profiles.active=prod app.jar
#################################
FROM openjdk:11
WORKDIR /app
COPY ./target/msavaliadorcredito-0.0.1-SNAPSHOT.jar app.jar

ARG RABBITMQ_SERVER=rabbitmq-host
ARG EUREKA_SERVER=localhost

ENTRYPOINT java -jar -Dspring.profiles.active=prod app.jar