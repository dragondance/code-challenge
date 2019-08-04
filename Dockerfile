FROM openjdk:8-jre
VOLUME /tmp
ENV path=/
ENV managementpath=actuator
ENV JAVA_OPTS="-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode"
ENV JAVA_MEM="-Xms75m -Xmx512m -Xss512k -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
ADD target/*.jar app.jar
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.contextPath=$path  -Dmanagement.endpoints.web.base_path=/$managementpath -jar /app.jar
EXPOSE 8080
