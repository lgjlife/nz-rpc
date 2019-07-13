#!/bin/bash

JAR_PATH="/home/lgj/aProject/aRealPrj/nz-rpc/app-demo/app-consumer/target/app-consumer-1.0.0.jar"
JAVA_OPT="${JAVA_OPT} -server -Xms1024m -Xmx1024m -Xmn800m -XX:MetaspaceSize=800m -XX:MaxMetaspaceSize=1000m"


java -jar ${JAVA_OPT} ${JAR_PATH}

