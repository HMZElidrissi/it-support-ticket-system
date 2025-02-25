FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]