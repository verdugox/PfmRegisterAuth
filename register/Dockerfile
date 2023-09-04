FROM openjdk:11
VOLUME /tmp
EXPOSE 9080
ADD ./target/register-0.0.1-SNAPSHOT.jar ms-register.jar
ENTRYPOINT ["java", "-jar","/ms-register.jar"]

