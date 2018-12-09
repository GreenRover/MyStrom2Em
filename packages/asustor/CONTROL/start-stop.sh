#!/bin/sh

. /etc/script/lib/command.sh

APKG_PKG_DIR=/usr/local/AppCentral/mystrom
PID_FILE=/var/run/mystrom.pid

JAVA_CMD=/usr/local/bin/java

case $1 in

	start)
		# start script here
		cd $APKG_PKG_DIR/
		$JAVA_CMD -ea -jar $APKG_PKG_DIR/mystrom2em-jar-with-dependencies.jar $APKG_PKG_DIR/config.xml > /dev/null &
		echo $! > $PID_FILE
		;;

	stop)
		# stop script here
		kill -9 `cat $PID_FILE` 2> /dev/null
		rm -rf $PID_FILE
		;;

	*)
		echo "usage: $0 {start|stop}"
		exit 1
		;;
		
esac

exit 0
