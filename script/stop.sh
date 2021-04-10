#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps

kill $(cat ./pid.file)

sudo rm -r /opt/tomcat/webapps/ROOT.jar

