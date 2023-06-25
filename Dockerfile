#
# Build stage
#
FROM maven:3.8.6-jdk-8-slim AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:8-jdk-alpine
COPY --from=build /target/*.jar app.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]