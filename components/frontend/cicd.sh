#!/usr/bin/env bash

read -p "Insert new Tag: " tag
docker build -t fabrizio294/ui:$tag .
docker login --username fabrizio294 --password "a45W[=nw3y3Fp6>!"
docker tag fabrizio294/ui:$tag fabrizio294/ui:$tag
docker push fabrizio294/ui:$tag