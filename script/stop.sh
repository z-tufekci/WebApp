#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/ROOT > /dev/null 2> /dev/null
sudo rm -rf /opt/tomcat/webapps/ROOT.war > /dev/null 2> /dev/null

sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
sudo rm -rf /opt/tomcat/logs/catalina*

