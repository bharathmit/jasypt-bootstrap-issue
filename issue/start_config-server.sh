#!/bin/bash

CONFIG_REPO_URI=https://bitbucket.org/fahim-experiment/config-repo
CONFIG_REPO_PATH=config-repo
CONFIG_REPO_USERNAME=
# The passowrod should be 'ENC(<encrypted password by jsypt>)'
CONFIG_REPO_PASSWORD=
CONFIG_SERVER_PORT=11001
DEBUG="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=6005,suspend=n"

if [[ $CONFIG_REPO_USERNAME -eq "" || $CONFIG_REPO_PASSWORD -eq "" ]]
then
	echo "Please set the CONFIG_REPO_USERNAME and CONFIG_REPO_PASSWORD variables first."
	exit -1
fi

function wait_till_started {
	until [ "`curl --silent --show-error --connect-timeout 1 http://localhost:$1/health | grep 'UP'`" != "" ];
	do
	  echo "sleeping for 10 seconds..."
	  sleep 10
	done
}

printf "\n\nPackaging...\n\n"
mvn clean package

printf "\n\nStarting the config-server...\n\n"
java -Dport=$CONFIG_SERVER_PORT -Dconfig-repo.username=$CONFIG_REPO_USERNAME -Dconfig-repo.password=$CONFIG_REPO_PASSWORD -Dconfig.repo.uri=$CONFIG_REPO_URI -Dconfig.repo.path=$CONFIG_REPO_PATH -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar
