#! /bin/bash
ssh -i ~/.ssh/uxguards.pem ubuntu@workshop.uxguards.com -t '
cd ~/src/whipping-boy &&
git fetch &&
git reset --hard origin/master &&
lein clean &&
DEPLOYMENT=true RING_ENV=production lein ring uberwar &&
sudo mv target/whipping-boy.war /var/lib/jetty/webapps/whipping-boy.war &&
sudo chown jetty /var/lib/jetty/webapps/whipping-boy.war &&
sudo service jetty restart'
