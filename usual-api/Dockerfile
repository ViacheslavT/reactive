### ARGS

ARG BUILD_IMAGE=maven:3.8.1-openjdk-11
ARG RUNTIME_IMAGE=openjdk:11-jre-slim

### STEP 1 build executable binary
FROM ${BUILD_IMAGE} as builder

RUN export && pwd

# PREPARE SRC TO BUILD
COPY pom.xml /tmp/
#COPY settings.xml ${MAVEN_CONFIG}/
COPY src /tmp/src/
WORKDIR /tmp/

# BUILD
RUN mvn package

## STEP 2 build final image (small image)
FROM ${RUNTIME_IMAGE}
COPY --from=builder /tmp/target/*.jar /app/app.jar
# Arguments
ARG API_BASE_URL
ARG DB_URL
ARG DB_USER
ARG DB_PASSWORD
ENV API_BASE_URL=${API_BASE_URL}
ENV DB_URL=${DB_URL}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
