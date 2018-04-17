#!/usr/bin/env bash

function printUsage {
  echo "Usage: proxy-stop.sh"
}

function killJvm {
  main_class=$1
  for pid in $( ps -Aww -o pid,command | grep -i "[j]ava" | grep ${main_class} | awk '{print $1}' ); do
    kill -15 ${pid} > /dev/null 2>&1
    local cnt=0
    while kill -0 ${pid} > /dev/null 2>&1; do
      if [[ ${cnt} -lt 20 ]]; then
        cnt=$(expr ${cnt} + 1)
        sleep 1
      else
        kill -9 ${pid} 2> /dev/null
      fi
    done
  done
}

function main {
  MAIN_CLASS="com.htsc.alluxioproxy.sql.ProxySqlServer"
  killJvm ${MAIN_CLASS}
}

main "$@"
