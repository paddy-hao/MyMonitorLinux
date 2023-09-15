# Use OpenJDK 11 as the base image
FROM openjdk:11-jdk
# Accept the JAR file location as an argument, the default is under
# target/ directory and named MyMonitor2-1.0-SNAPSHOT.jar
ARG JAR_FILE=target/MyMonitor2-1.0-SNAPSHOT.jar
# Run package update and install utilities needed for the application
# fdisk, dmidecode, pciutils, and usbutils are system utilities
RUN apt-get update && apt-get install -y fdisk dmidecode pciutils usbutils
# Copy the JAR file into the image at location /app.jar
COPY ${JAR_FILE} app.jar
# Specify the entry point of the application, which will be executed when the container starts
ENTRYPOINT ["java","-jar","/app.jar"]







