# Temp container to build using Gradle
FROM gradle:8.6.0-jdk21 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME
COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . $APP_HOME

USER root
RUN chown -R gradle:gradle $APP_HOME
USER gradle

# Build fat JAR (includes bucket + communication gRPC and email templates)
RUN gradle bootJar -x test --no-daemon

# Final container
FROM openjdk:21
ENV APP_HOME=/usr/app/
ENV TZ=Asia/Kathmandu
WORKDIR $APP_HOME

# Copy the JAR file from the build stage
COPY --from=TEMP_BUILD_IMAGE /usr/app/build/libs/* $APP_HOME/build/libs/
RUN ls -larth $APP_HOME/build/libs/
RUN cp $APP_HOME/build/libs/Bucket-Service-0.0.1-SNAPSHOT.jar App.jar
EXPOSE 8006 8906
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=PROFILE", "App.jar"]

