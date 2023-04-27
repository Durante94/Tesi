#!/usr/bin/env bash

read -p "Insert new Tag: " tag
docker build -t fabrizio294/configuration-scheduler:$tag .
docker login --username fabrizio294 --password "a45W[=nw3y3Fp6>!"
docker tag fabrizio294/configuration-scheduler:$tag fabrizio294/configuration-scheduler:$tag
docker push fabrizio294/configuration-scheduler:$tag