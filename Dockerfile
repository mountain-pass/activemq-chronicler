FROM webcenter/activemq:5.11.1
MAINTAINER tom@windyroad.com.au
EXPOSE 80
EXPOSE 61616
EXPOSE 5672
EXPOSE 61613
EXPOSE 1883
EXPOSE 61614

ADD src/main/resources/activemq.xml /opt/activemq/conf/activemq.xml

ADD build/libs/activemq-chronicler-1.jar /opt/activemq/activemq-chronicler-1.jar
ADD ~/.gradle/caches/modules-2/files-2.1/net.openhft/chronicle/3.4.3/b5952d63bb27751a50fb52598d7a7ee811bf3aee/chronicle-3.4.3.jar /opt/activemq/chronicle-3.4.3.jar
ADD ~/.gradle/caches/modules-2/files-2.1/net.openhft/affinity/2.2/8abd6d1d76202aa823c2d0d834f2059b1ea0192f/affinity-2.2.jar /opt/activemq/affinity-2.2.jar
ADD ~/.gradle/caches/modules-2/files-2.1/net.openhft/lang/6.6.2/5f28c524cabb357b3859558b5fa882141164929c/lang-6.6.2.jar /opt/activemq/affinity-2.2.jar
CMD ["/opt/activemq/bin/activemq", "start"]