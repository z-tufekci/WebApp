#!/bin/bash

#sudo chmod 775 /opt/tomcat/webapps/ 
#sudo chmod 775 /opt/tomcat/work/ 
#sudo chmod 775 /opt/tomcat/temp/ 
#sudo chmod 775 /opt/tomcat/logs/

#sudo rm -rf /opt/tomcat/webapps/ROOT
#sudo rm -rf /opt/tomcat/webapps/ROOT.war
test -f /opt/cloudwatch-config.json && sudo rm -r /opt/cloudwatch-config.json & > /dev/null 2> /dev/null < /dev/null &
#sudo rm -rf /opt/tomcat/logs/*.log
#sudo rm -rf /opt/tomcat/logs/*.txt
#sudo rm -rf /opt/tomcat/logs/catalina*
