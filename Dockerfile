FROM openjdk:17-jdk-slim
COPY build/libs/photique-0.0.1-SNAPSHOT.jar photique.jar
ENTRYPOINT ["java", "-jar", "photique.jar"]
EXPOSE 8080