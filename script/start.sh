#!/bin/bash
#sudo chmod 755 /opt/tomcat/webapps/ROOT
#sudo rm -r /opt/tomcat/webapps/ROOT
sudo chmod +x /opt/tomcat/bin/startup.sh
sudo systemctl start tomcat > /dev/null 2> /dev/null < /dev/null &
sudo chmod 755 /opt/tomcat/webapps/ROOT
sudo rm -r /opt/tomcat/webapps/ROOT

#sudo bash /opt/tomcat/bin/startup.sh run > /dev/null 2> /dev/null < /dev/null &

