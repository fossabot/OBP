FROM frolvlad/alpine-oraclejdk8:slim
ADD target/history-1.0-SNAPSHOT.jar app.jar
#COPY addhostfile.sh /addhostsfile.sh
#RUN sh -c 'chmod +x /addhostsfile.sh'
#RUN sh -c '/addhostsfile.sh'
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8089
