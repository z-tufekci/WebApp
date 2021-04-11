#!/bin/bash

cd /opt/webapps
test -f pid.file && sudo rm -r pid.file  & > /dev/null 2> /dev/null < /dev/null &
#sudo chmod +x /opt/tomcat/bin/startup.sh

source /etc/profile & > /dev/null 2> /dev/null < /dev/null &
java -jar $JAVA_OPTS ROOT.jar & sudo sh -c "echo $!  > pid.file" & > /dev/null 2> /dev/null < /dev/null &

#sudo chmod 755 /opt/tomcat/webapps/ROOT
#sudo rm -r /opt/tomcat/webapps/ROOT



#sudo systemctl restart tomcat > /dev/null 2> /dev/null < /dev/null &


#sudo bash /opt/tomcat/bin/startup.sh run > /dev/null 2> /dev/null < /dev/null &

