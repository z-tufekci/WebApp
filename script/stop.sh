#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps
test -f pid.file && kill $(cat pid.file)
test -f ROOT.jar && sudo rm -r ROOT.jar



