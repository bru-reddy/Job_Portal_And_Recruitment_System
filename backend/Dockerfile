FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw -q -DskipTests package || true
EXPOSE 8080
CMD ["java","-jar","target/job-portal-api-0.0.1-SNAPSHOT.jar"]
