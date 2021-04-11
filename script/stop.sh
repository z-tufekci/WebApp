#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps
#test -f pid.file && kill $(cat pid.file)
test -f ROOT.jar && sudo rm -r ROOT.jar

#sudo lsof -i:8080 -t | xargs -r sudo kill

if pids=$(sudo lsof -i:8080 -t); then
    sudo kill -9 $pids
fi

#sudo kill -9 `sudo lsof -t -i:8080`

