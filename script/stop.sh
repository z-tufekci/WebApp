#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps
#test -f pid.file && { kill $(cat pid.file) && wait $(cat pid.file) } 2>/dev/null
{test -f ROOT.jar && sudo rm -r ROOT.jar} 2>/dev/null

test -d /proc/$(sudo lsof -t -i:8080) && { kill $(sudo lsof -t -i:8080) && wait $(sudo lsof -t -i:8080); } & > /dev/null 2> /dev/null < /dev/null &

