#!/bin/bash


#test -f pid.file && sudo rm -r pid.file  & > /dev/null 2> /dev/null < /dev/null &

#source /etc/profile & > /dev/null 2> /dev/null < /dev/null &
#source /etc/profile.d/setenv.sh
#source /home/ubuntu/.bashrc
cd /opt/webapps
#. setenv.sh & > /dev/null 2> /dev/null < /dev/null &
export Z=$(cat setenv.file) 2> /dev/null
java -jar $Z /opt/webapps/ROOT.jar  & > /dev/null 2> /dev/null < /dev/null &







#sudo chmod 755 /opt/tomcat/webapps/ROOT
#sudo rm -r /opt/tomcat/webapps/ROOT
#sudo chmod +x /opt/tomcat/bin/startup.sh

#sudo systemctl restart tomcat > /dev/null 2> /dev/null < /dev/null &

#sudo bash /opt/tomcat/bin/startup.sh run > /dev/null 2> /dev/null < /dev/null &

