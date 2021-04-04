#!/bin/bash

#sudo systemctl start tomcat
sudo chmod +x /opt/tomcat/bin/startup.sh
sudo bash /opt/tomcat/bin/startup.sh run > /dev/null 2> /dev/null < /dev/null &
