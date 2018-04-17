#!/usr/bin/env bash

function printUsage {
  echo "Usage: gateway-start.sh"
}

function main {
  if [[ $# != 0 ]]; then
    printUsage
    exit 1
  fi
  
  BIN="$( cd "$( dirname "$0" )"; pwd )"

  . "${BIN}/../conf/gateway-env.sh"

  export GATEWAY_HOME=${GATEWAY_HOME:-"$( cd "$( dirname "$0" )/.." && pwd )"}

  JAVA_HOME="${JAVA_HOME:-"$(dirname $(which java))/.."}"
  JAVA="${JAVA:-"${JAVA_HOME}/bin/java"}"
  
  GATEWAY_HOSTNAME="${GATEWAY_HOSTNAME:-"localhost"}"
  
  GATEWAY_JAR="${GATEWAY_HOME}/target/proxy-gateway-${VERSION}-jar-with-dependencies.jar"
  GATEWAY_CONF_DIR="${GATEWAY_HOME}/conf"
  GATEWAY_LOGS_DIR="${GATEWAY_HOME}/logs"

  mkdir -p ${GATEWAY_LOGS_DIR}
  GATEWAY_TASK_LOG="${GATEWAY_LOGS_DIR}/task.log"

  CLASSPATH="${GATEWAY_JAR}"

  MAIN_CLASS="com.htsc.alluxioproxy.gateway.GatewayServer"
  
  GATEWAY_JAVA_OPTS+=" -Dgateway.conf.dir=${GATEWAY_CONF_DIR}"
  GATEWAY_JAVA_OPTS+=" -Dgateway.logs.dir=${GATEWAY_LOGS_DIR}"
  GATEWAY_JAVA_OPTS+=" -Dlog4j.configuration=file:${GATEWAY_CONF_DIR}/log4j.properties"
  GATEWAY_JAVA_OPTS+=" -Dgateway.hostname=${GATEWAY_HOSTNAME}"
  GATEWAY_JAVA_OPTS+=" -Djava.com.htsc.alluxioproxy.sql.util.logging.config.file=${GATEWAY_CONF_DIR}/logging.properties"

  nohup ${JAVA} -Xmx2g -cp ${CLASSPATH} ${GATEWAY_JAVA_OPTS} ${MAIN_CLASS} > \
  ${GATEWAY_LOGS_DIR}/gateway.out 2>&1 &
}

main "$@"
