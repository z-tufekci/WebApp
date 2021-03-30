#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/ROOT
sudo rm  /opt/cloudwatch-config.json
sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
sudo rm -rf /opt/tomcat/logs/catalina*

