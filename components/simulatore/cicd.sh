read -p "Insert new Tag: " tag
docker build -t fabrizio294/simulatore:$tag .
docker login --username fabrizio294 --password "a45W[=nw3y3Fp6>!"
docker push fabrizio294/simulatore:$tag