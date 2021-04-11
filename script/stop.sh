#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps
#test -f pid.file && kill $(cat pid.file)
test -f ROOT.jar && sudo rm -r ROOT.jar

test -d /proc/$(sudo lsof -t -i:8080) && { kill $(sudo lsof -t -i:8080) && wait $(sudo lsof -t -i:8080); } 2>/dev/null

