FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY build/libs/*.jar /app/fat.jar
ENTRYPOINT ["java","-jar", "/app/fat.jar"]