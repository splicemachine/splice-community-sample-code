FROM centos:7.3.1611

ARG sm_version=2.6.1.1736
ARG dbaas_build=1.0.924
ARG spark_version=2.1.1
ARG hadoop_version=2.6
ARG cdh_version=5.8.3

ENV SPARK_VERSION=$spark_version
ENV HADOOP_VERSION=$hadoop_version
ENV SPARK_HOME=/usr/local/spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION
ENV JAVA_HOME=/usr/local/jdk1.8.0_121
ENV PATH=$JAVA_HOME/bin:$SPARK_HOME/bin:$PATH
ENV MESOS_NATIVE_JAVA_LIBRARY=/usr/local/libmesos-bundle/lib/libmesos.so
ENV LD_LIBRARY_PATH=/usr/local/libmesos-bundle/lib:/native:/usr/local/lib:$LD_LIBRARY_PATH

RUN yum install -y bind-utils net-tools which nc jq unzip

RUN curl -kLs "https://s3.amazonaws.com/splicemachine/$sm_version-$dbaas_build/artifacts/hadoop-2.6.0-cdh$cdh_version.tar.gz" | tar -xz
RUN ln -s /hadoop-2.6.0-cdh$cdh_version /hadoop
RUN curl -kLs "https://s3.amazonaws.com/splicemachine/artifacts/cdh${cdh_version}-native.tgz" | tar -xz
RUN chown -R root:root /native
RUN curl -kLs "https://s3.amazonaws.com/splicemachine/artifacts/spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz" | tar -xz -C /usr/local
RUN curl -kLso $SPARK_HOME/jars/shiro-core-1.2.3.jar https://s3.amazonaws.com/splicemachine/artifacts/shiro-core-1.2.3.jar
RUN curl -kLso $SPARK_HOME/jars/shiro-web-1.2.3.jar https://s3.amazonaws.com/splicemachine/artifacts/shiro-web-1.2.3.jar
RUN curl -kLso $SPARK_HOME/jars/splice-shiro-$sm_version.jar https://s3.amazonaws.com/splicemachine/artifacts/splice-shiro-$sm_version.jar
RUN curl -kLs "https://s3.amazonaws.com/splicemachine/$sm_version-$dbaas_build/artifacts/splice-zeppelin-0.7.1-bin-all.tgz" | tar -xz -C /var/tmp zeppelin-0.7.1-bin-all/interpreter/spark/dep
RUN curl -kLs "https://s3.amazonaws.com/splicemachine/$sm_version-$dbaas_build/artifacts/jdk-8u121-linux-x64.tar.gz" | tar -xz -C /usr/local
RUN curl -kLs "https://s3.amazonaws.com/splicemachine/$sm_version-$dbaas_build/artifacts/libmesos-bundle-1.9-argus-1.1.x-2.tar.gz" | tar -xz -C /usr/local
RUN cp /var/tmp/zeppelin-0.7.1-bin-all/interpreter/spark/dep/hbase_*.jar $SPARK_HOME/jars/
RUN rm $SPARK_HOME/jars/*-SNAPSHOT*.jar && \
    rm /var/tmp/zeppelin-0.7.1-bin-all/interpreter/spark/dep/*

COPY ./target/splicemachine-jars/* $SPARK_HOME/jars/
COPY ./target/splice-tutorial-file-spark-$sm_version.jar /
COPY ./src/main/resources/scripts/* /
COPY ./src/main/resources/spark/conf/ $SPARK_HOME/conf/

COPY ./Docker/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]


CMD ["/start-spark-streaming.sh"]