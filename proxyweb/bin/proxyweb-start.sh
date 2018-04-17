#!/usr/bin/env bash

function printUsage {
  echo "Usage: proxyweb-start.sh"
}

function main {
  if [[ $# != 0 ]]; then
    printUsage
    exit 1
  fi

  export WEB_HOME=${WEB_HOME:-"$( cd "$( dirname "$0" )/.." && pwd )"}

  JAVA_HOME=/usr/local/jdk1.8.0_151

  VERSION=0.0.3

  JAVA="${JAVA:-"${JAVA_HOME}/bin/java"}"

  WEB_HOSTNAME="${WEB_HOSTNAME:-"localhost"}"

  WEB_JAR="${WEB_HOME}/target/proxy-web-${VERSION}-jar-with-dependencies.jar"
  WEB_CONF_DIR="${WEB_HOME}/conf"
  WEB_LOGS_DIR="${WEB_HOME}/logs"

  mkdir -p ${WEB_LOGS_DIR}
  GATEWAY_TASK_LOG="${WEB_LOGS_DIR}/task.log"

  CLASSPATH="${WEB_JAR}"

  MAIN_CLASS="com.htsc.alluxioproxy.webserver.web.WebServer"

  WEB_JAVA_OPTS+=" -Dweb.conf.dir=${WEB_CONF_DIR}"
  WEB_JAVA_OPTS+=" -Dweb.logs.dir=${WEB_LOGS_DIR}"
  WEB_JAVA_OPTS+=" -Dlog4j.configuration=file:${WEB_CONF_DIR}/log4j.properties"
  WEB_JAVA_OPTS+=" -Dproxyweb.hostname=${WEB_HOSTNAME}"
  WEB_JAVA_OPTS+=" -Djava.com.htsc.alluxioproxy.sql.util.logging.config.file=${WEB_CONF_DIR}/logging.properties"

  nohup ${JAVA} -Xmx2g -cp ${CLASSPATH} ${WEB_JAVA_OPTS} ${MAIN_CLASS} > \
  ${WEB_LOGS_DIR}/proxyweb.out 2>&1 &
}

main "$@"