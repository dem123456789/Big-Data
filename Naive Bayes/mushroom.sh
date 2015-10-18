#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Downloads the 20newsgroups dataset, trains and tests a classifier.
#
# To run:  change into the mahout directory and type:
# examples/bin/classify-20newsgroups.sh

if [ "$1" = "--help" ] || [ "$1" = "--?" ]; then
  echo "This script runs SGD and Bayes classifiers over the classic 20 News Groups."
  exit
fi

SCRIPT_PATH=${0%/*}
if [ "$0" != "$SCRIPT_PATH" ] && [ "$SCRIPT_PATH" != "" ]; then
  cd $SCRIPT_PATH
fi
START_PATH=`pwd`

# Set commands for dfs
source ${START_PATH}/set-dfs-commands.sh

WORK_DIR=/tmp/mahout-work-${USER}
algorithm=( cnaivebayes-MapReduce naivebayes-MapReduce clean)
if [ -n "$1" ]; then
  choice=$1
else
  echo "Please select a number to choose the corresponding task to run"
  echo "1. ${algorithm[0]}"
  echo "2. ${algorithm[1]}"
  echo "3. ${algorithm[2]}-- cleans up the work area in $WORK_DIR"
  read -p "Enter your choice : " choice
fi

echo "ok. You chose $choice and we'll use ${algorithm[$choice-1]}"
alg=${algorithm[$choice-1]}



#echo $START_PATH
cd $MAHOUT_HOME

set -e

if  ( [ "x$alg" == "xnaivebayes-MapReduce" ] ||  [ "x$alg" == "xcnaivebayes-MapReduce" ]); then
  c=""
  if [ "x$alg" == "xcnaivebayes-MapReduce"]; then
    c=" -c"
  fi

  set -x
  echo "Preparing mushroom data"

  echo "Converting sequence files to vectors"
  ./bin/mahout seq2sparse \
    -i ${WORK_DIR}/mushroom-seq/training/ \
    -o ${WORK_DIR}/mushroom-vectors/training -lnorm -nv  -wt tfidf

  ./bin/mahout seq2sparse \
    -i ${WORK_DIR}/mushroom-seq/test/ \
    -o ${WORK_DIR}/mushroom-vectors/test -lnorm -nv  -wt tfidf

    if [ "x$alg" == "xnaivebayes-MapReduce"  -o  "x$alg" == "xcnaivebayes-MapReduce" ]; then

      echo "Training Naive Bayes model"
      ./bin/mahout trainnb \
        -i ${WORK_DIR}/mushroom-vectors/training/tfidf-vectors \
        -o ${WORK_DIR}/model \
        -li ${WORK_DIR}/labelindex \
        -ow $c

      echo "Self testing on training set"

      ./bin/mahout testnb \
        -i ${WORK_DIR}/mushroom-vectors/training/tfidf-vectors \
        -m ${WORK_DIR}/model \
        -l ${WORK_DIR}/labelindex \
        -ow -o ${WORK_DIR}/mushroom-testing $c

      echo "Testing on holdout set"

      ./bin/mahout testnb \
        -i ${WORK_DIR}/mushroom-vectors/test/tfidf-vectors \
        -m ${WORK_DIR}/model \
        -l ${WORK_DIR}/labelindex \
        -ow -o ${WORK_DIR}/mushroom-testing $c
        
    fi
elif [ "x$alg" == "xclean" ]; then
  rm -rf $WORK_DIR
  rm -rf /tmp/news-group.model
  $DFSRM $WORK_DIR
fi
# Remove the work directory
#

