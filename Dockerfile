FROM webcenter/activemq:5.11.1
MAINTAINER tom@windyroad.com.au
EXPOSE 80
EXPOSE 61616
EXPOSE 5672
EXPOSE 61613
EXPOSE 1883
EXPOSE 61614

ADD src/main/resources/activemq.xml /opt/activemq/conf/activemq.xml

ADD build/libs/*.jar /opt/activemq/
CMD ["/opt/activemq/bin/activemq", "start"]