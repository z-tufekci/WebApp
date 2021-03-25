#!/bin/bash

sudo systemctl stop tomcat

#sudo rm -rf /opt/tomcat/webapps/demo-0.0.1-SNAPSHOT

sudo chown tomcat:tomcat /opt/tomcat/webapps/demo-0.0.1-SNAPSHOT.war

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/cloudwatch-config.json -s
