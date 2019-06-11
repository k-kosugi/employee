FROM openjdk:12.0.1-jdk-oraclelinux7
COPY ./target/employee-thorntail.jar /
ENTRYPOINT ["java", "-jar", "/employee-thorntail.jar"]
