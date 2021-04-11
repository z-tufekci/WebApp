#!/bin/bash

#sudo systemctl stop tomcat
cd /opt/webapps

FILE=./pid.file
if test -f "$FILE"; then
    kill $(cat ./pid.file)
fi

FILE2=/opt/webapps/ROOT.jar
if test -f "$FILE2"; then
    sudo rm -r /opt/webapps/ROOT.jar
fi


