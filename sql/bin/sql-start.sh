#!/usr/bin/env bash

function printUsage {
  echo "Usage: proxy-start.sh"
}

function main {
  if [[ $# != 0 ]]; then
    printUsage
    exit 1
  fi
  
  BIN="$( cd "$( dirname "$0" )"; pwd )"

  #  . "${BIN}/../conf/proxy-env.sh"

  VERSION=0.0.3

  JAVA_HOME=/usr/bin/java
  PROXY_HOME=/root/software/alluxio-proxy-test

  PROXY_HOSTNAME=0.0.0.0

  export PROXY_HOME=${PROXY_HOME:-"$( cd "$( dirname "$0" )/.." && pwd )"}

  #JAVA_HOME="${JAVA_HOME:-"$(dirname $(which java))/.."}"

#  JAVA="${JAVA:-"${JAVA_HOME}/bin/java"}"
    JAVA="${JAVA:-"${JAVA_HOME}"}"

  PROXY_HOSTNAME="${PROXY_HOSTNAME:-"alluixo-01"}"
  
  PROXY_JAR="${PROXY_HOME}/sql/target/sql-${VERSION}-jar-with-dependencies.jar"
  PROXY_CONF_DIR="${PROXY_HOME}/conf"
  PROXY_LOGS_DIR="${PROXY_HOME}/sql/logs"
  PROXY_LOGS_CONF_DIR="${PROXY_HOME}/sql/conf"

  mkdir -p ${PROXY_LOGS_DIR}
  PROXY_TASK_LOG="${PROXY_LOGS_DIR}/task.log"

  CLASSPATH="${PROXY_JAR}"

  MAIN_CLASS="com.htsc.alluxioproxy.sql.ProxySqlServer"
  
  PROXY_JAVA_OPTS+=" -Dproxy.conf.dir=${PROXY_CONF_DIR}"
  PROXY_JAVA_OPTS+=" -Dproxy.logs.dir=${PROXY_LOGS_DIR}"
  PROXY_JAVA_OPTS+=" -Dproxy.logs.conf.dir=${PROXY_LOGS_CONF_DIR}"
  PROXY_JAVA_OPTS+=" -Dlog4j.configuration=file:${PROXY_CONF_DIR}/log4j.properties"
  PROXY_JAVA_OPTS+=" -Dproxy.hostname=${PROXY_HOSTNAME}"
  PROXY_JAVA_OPTS+=" -Dalluxio.site.conf.dir=${PROXY_CONF_DIR}"
  PROXY_JAVA_OPTS+=" -Dproxy.keystore.file.path=${PROXY_CONF_DIR}/keystore.pkcs12"
  PROXY_JAVA_OPTS+=" -Djava.com.htsc.alluxioproxy.sql.util.logging.config.file=${PROXY_CONF_DIR}/logging.properties"

  nohup ${JAVA} -Xmx2g -cp ${CLASSPATH} ${PROXY_JAVA_OPTS} ${MAIN_CLASS} > \
  ${PROXY_LOGS_DIR}/sql.out 2>&1 &
}

main "$@"
