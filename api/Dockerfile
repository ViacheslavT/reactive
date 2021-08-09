FROM openjdk:11

COPY ./target/api-1.0-SNAPSHOT.jar /app/app.jar
RUN chmod +x /app/app.jar
WORKDIR app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
