#!/bin/sh

docker_container = $(docker container ls -al | grep "game_party_server")

if [ -n docker_container ]; then
  docker container rm "game_party_server"
fi

echo "docker run --name game_party_server parksjin01/game_party_server:${1}"
docker run --name game_party_server -e JAVA_OPTIONS="-server -Xms128m -Xmx256m -Dphase=${2} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" -p 80:8080 -p 5005:5005 -d parksjin01/game_party_server:${1} --module=http,jmx,stats,logging,deploy