#!/usr/bin/env bash

VERSION="0.0.3"

JAVA_HOME="/usr/local/jdk1.8.0_151"
JAVA="${JAVA_HOME}/bin/java"

BASE_URI="http://localhost:26666"
FILE="/home/innkp/proxyTest.tar.gz"
USERNAME="alluxio"
PASSWORD="alluxio"

TEST_HOME="$( cd "$( dirname "$0" )/.."; pwd )"
TEST_JAR="${TEST_HOME}/target/proxy-test-${VERSION}-jar-with-dependencies.jar"

MAIN_CLASS="com.htsc.alluxioproxy.FileTest"

COMMAND="${JAVA} -cp ${TEST_JAR} ${MAIN_CLASS} ${BASE_URI}"

${COMMAND}
