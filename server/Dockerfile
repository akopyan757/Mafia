FROM openjdk:17
EXPOSE 8082:8082
RUN mkdir /app
COPY build/libs/*.jar /app/fat.jar
ENTRYPOINT ["java","-jar", "/app/fat.jar"]