FROM centos

RUN yum -y install wget
RUN mkdir -p /opt/jdk
RUN cd /opt
RUN wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jdk-8u45-linux-x64.tar.gz"
RUN tar -zxf jdk-8u45-linux-x64.tar.gz -C /opt/jdk
RUN rm jdk-8u45-linux-x64.tar.gz
RUN update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.8.0_45/bin/java 100
RUN update-alternatives --install /usr/bin/javac javac /opt/jdk/jdk1.8.0_45/bin/javac 100

EXPOSE 8888

ADD ontology-1.0-SNAPSHOT.jar /

ENTRYPOINT ["java", "-jar", "/ontology-1.0-SNAPSHOT.jar"]

