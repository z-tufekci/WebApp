#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/demo-0.0.1-SNAPSHOT

sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
sudo rm -rf /opt/tomcat/logs/catalina*

