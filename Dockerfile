FROM maven:3.6.3-jdk-11

WORKDIR /

COPY src /src
COPY pom.xml /

RUN mvn package


ENV NEVERMINED_OPTS=''
RUN mv target/cli-*-shaded.jar /
RUN alias ncli='java $NEVERMINED_OPTS -jar cli-*-shaded.jar'

CMD ["/bin/sh"]
