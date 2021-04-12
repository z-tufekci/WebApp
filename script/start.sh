#!/bin/bash


sudo chmod +x /opt/tomcat/bin/startup.sh

sudo systemctl restart tomcat > /dev/null 2> /dev/null < /dev/null &
