FROM openjdk:12-oracle
COPY ./target/employee-thorntail.jar /
COPY ./project-defaults.yaml /
ENTRYPOINT ["java", "-jar", "employee-thorntail.jar"]
