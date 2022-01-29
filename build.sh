#!/bin/sh

gradle clean war
docker build . -t parksjin01/game_party_server:${1}
