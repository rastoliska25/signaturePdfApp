FROM openjdk:18
EXPOSE 8080
ADD target/signaturePDF-0.0.1-SNAPSHOT.jar signaturepdf.jar
ENTRYPOINT ["java", "-jar", "signaturepdf.jar"]