#!/bin/bash

sudo chown tomcat:tomcat /opt/tomcat/webapps/ROOT.war
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/cloudwatch-config.json -s
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -o default -s

sudo chmod -R +x /opt/tomcat/
sudo ./opt/tomcat/bin/catalina.sh run
