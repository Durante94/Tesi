#!/usr/bin/env bash

read -p "Insert new Tag: " tag
docker build -t fabrizio294/manager:$tag .
docker login --username fabrizio294 --password "dckr_pat_aeWHKHK8FgAopinT4G3E4UoY0eY"
docker tag fabrizio294/manager:$tag fabrizio294/manager:$tag
docker push fabrizio294/manager:$tag