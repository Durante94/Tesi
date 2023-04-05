#!/usr/bin/env bash

read -p "Insert new Tag: " tag
docker build -t fabrizio294/manager:$tag .
docker login --username fabrizio294 --password "a45W[=nw3y3Fp6>!"
docker tag fabrizio294/manager:$tag localhost:5000/fabrizio294/manager:$tag
docker push localhost:5000/fabrizio294/manager:$tag