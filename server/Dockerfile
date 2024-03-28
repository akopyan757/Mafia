FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY build/libs/*.jar /app
ENTRYPOINT ["java","-jar", "/app/fat.jar"]