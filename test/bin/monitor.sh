#!/usr/bin/env bash

ALLUXIO_HOME="/home/salty/alluxio-1.4.0"

TEST_HOME="$( cd "$( dirname "$0" )/.."; pwd )"

PATH_FILE="${TEST_HOME}/path.out"
ERROR_FILE="${TEST_HOME}/err.out"

SUCCESS=FALSE

for i in {1..5}; do
  if ( ${TEST_HOME}/bin/file-test.sh > ${PATH_FILE} 2>>${ERROR_FILE} ) ; then
    SUCCESS=TRUE
  else
    SUCCESS=FALSE
  fi
  OUT_PATH="$( cat ${PATH_FILE} )"
  if [[ ${OUT_PATH} == "" ]] ; then
    SUCCESS=FALSE
  else
    ${ALLUXIO_HOME}/bin/alluxio fs rm ${OUT_PATH}
    echo "" > ${PATH_FILE}
  fi

  if [[ ${SUCCESS} == "FALSE" ]] ; then
    echo "error"
    exit 1
  fi
done
