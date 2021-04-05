#!/bin/bash

sudo systemctl stop tomcat

sudo chmod 755 /opt/tomcat/webapps/ /opt/tomcat/work/ /opt/tomcat/temp/ /opt/tomcat/logs/

sudo rm -rf /opt/tomcat/webapps/ROOT
sudo rm -rf /opt/tomcat/webapps/ROOT.war
sudo rm  /opt/cloudwatch-config.json
sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
sudo rm -rf /opt/tomcat/logs/catalina*

