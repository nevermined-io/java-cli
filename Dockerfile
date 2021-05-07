FROM maven:3.6.3-jdk-11
LABEL maintainer="Keyko Team <root@keyko.io>"

RUN apt-get update && apt-get install -y gettext-base jq
RUN curl -L https://dl.min.io/client/mc/release/linux-amd64/mc -o /usr/bin/mc && chmod +x /usr/bin/mc

WORKDIR /
ENV HOME /root

COPY src /src
COPY pom.xml /
COPY docker-entrypoint.sh /

RUN mvn package
RUN chmod +x /docker-entrypoint.sh

ENV NEVERMINED_OPTS=''
RUN mkdir -p $HOME/.local/share/nevermined-cli/accounts
RUN mkdir -p $HOME/.local/share/nevermined-cli/data
RUN mkdir -p $HOME/.nevermined/nevermined-contracts/artifacts

RUN mv target/cli-*-shaded.jar /cli-shaded.jar
RUN alias ncli='java $NEVERMINED_OPTS -jar cli-shaded.jar'


ENTRYPOINT ["/docker-entrypoint.sh"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]
