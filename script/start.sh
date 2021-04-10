#!/bin/bash


sudo rm -r pid.file

java -jar ROOT.jar & echo $! > ./pid.file &

#sudo chmod 777 /opt/tomcat/webapps/ROOT
#sudo rm -r /opt/tomcat/webapps/ROOT


#sudo chmod +x /opt/tomcat/bin/startup.sh
#sudo systemctl restart tomcat > /dev/null 2> /dev/null < /dev/null &


#sudo bash /opt/tomcat/bin/startup.sh run > /dev/null 2> /dev/null < /dev/null &

