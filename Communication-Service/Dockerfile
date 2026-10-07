# Temp container to build using Gradle
FROM gradle:8.14.1-jdk21 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME
COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . $APP_HOME

USER root
RUN chown -R gradle:gradle $APP_HOME
USER gradle

# Run SonarQube scan before building the JAR
RUN gradle clean build sonarqube -Dsonar.host.url=http://192.168.1.115:9500 -Dsonar.login=sqa_84dfe3d7c32b9058d397f84015e353f8126cdd16
# Build the application
RUN gradle clean bootJar

# Final container
FROM openjdk:21
ENV APP_HOME=/usr/app/
ENV TZ=Asia/Kathmandu
WORKDIR $APP_HOME

# Copy the JAR file from the build stage
COPY --from=TEMP_BUILD_IMAGE /usr/app/build/libs/* $APP_HOME/build/libs/

RUN ls -larth $APP_HOME/build/libs/
RUN cp $APP_HOME/build/libs/Communication-Service-0.0.1-SNAPSHOT.jar App.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=PROFILE", "App.jar"]
