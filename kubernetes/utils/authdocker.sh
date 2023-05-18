#!/bin/bash

set +e

export DOCKER_REGISTRY_SERVER=https://index.docker.io/v1/ ; 
export DOCKER_USER=fabrizio294 ;
export DOCKER_EMAIL=fabrizio.durante294@gmail.com ;
export DOCKER_PASSWORD='dckr_pat_aeWHKHK8FgAopinT4G3E4UoY0eY' ; 

k0s kubectl create secret docker-registry cfcr --docker-server=$DOCKER_REGISTRY_SERVER --docker-username=$DOCKER_USER --docker-password=$DOCKER_PASSWORD --docker-email=$DOCKER_EMAIL -n backend; 
k0s kubectl create secret docker-registry cfcr --docker-server=$DOCKER_REGISTRY_SERVER --docker-username=$DOCKER_USER --docker-password=$DOCKER_PASSWORD --docker-email=$DOCKER_EMAIL -n frontend; 

k0s kubectl patch serviceaccount default -p "{\"imagePullSecrets\": [{\"name\": \"cfcr\"}]}" -n backend;
k0s kubectl patch serviceaccount default -p "{\"imagePullSecrets\": [{\"name\": \"cfcr\"}]}" -n frontend;
