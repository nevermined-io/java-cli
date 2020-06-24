FROM maven:3.6.3-jdk-11

WORKDIR /
ENV HOME /root

COPY src /src
COPY pom.xml /

RUN mvn package


ENV NEVERMINED_OPTS=''
RUN mkdir -p $HOME/.local/share/nevermined-cli/accounts
RUN mkdir -p $HOME/.nevermined/nevermined-contracts/artifacts

RUN mv target/cli-*-shaded.jar /cli-shaded.jar
RUN alias ncli='java $NEVERMINED_OPTS -jar cli-shaded.jar'


#ENTRYPOINT ["java", "-jar", "cli-shaded.jar"]
ENTRYPOINT ["tail", "-f", "/dev/null"]
