#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/demo-0.0.1-SNAPSHOT

sudo chown tomcat:tomcat /opt/tomcat/webapps/demo-0.0.1-SNAPSHOT.war

sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
sudo rm -rf /opt/tomcat/logs/catalina*
