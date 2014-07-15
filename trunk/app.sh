#!/bin/sh
# fileserver project shell script
# Filename : app.sh
# Author: chenyuan
# Date : 2014-07-15
# -------- Usage ------
# for example :
# svn update in product env , jar files and run server :
# /bin/sh app.sh up_product && /bin/sh app.sh start

JAVA_CMD=/usr/bin/java
GRADLE_CMD=/home/work/tools/gradle/bin/gradle
SVN_CMD=/usr/bin/svn

echo ${JAVA_CMD}
runJar="./build/libs/fileserver-1.0.jar"

# Source function library.
. /etc/rc.d/init.d/functions

mkdir -p /home/work/fileServer/tmp
mkdir -p ./logs

RETVAL=0

case "$1" in
  start)
	# This will add jar to classpath
	for j in ./lib/*.jar; do
        	CLASSPATH=$j:$CLASSPATH;
	done

	#java parameters
	DEFAULT_OPTS=" -server -Xms1G -Xmx1G -Xss160k "
	nohup ${JAVA_CMD} -cp $CLASSPATH $DEFAULT_OPTS  -cp "$runJar:$CLASSPATH" com.vernon.file.Main $@ &
	echo $runcmd
	echo "fileserver starting..."
	sleep 2
	/bin/sh $0 status
	echo 
	pid=`/usr/sbin/lsof -i :8851 | tail -1 | awk '{print $2}'`
	echo $pid>./logs/fileserver.pid
	;;
  stop)
	pid=`/usr/sbin/lsof -i :8851 | tail -1 | awk '{print $2}'`
	[ -n "$pid" ] && kill $pid
	echo "fileserver stop..."	
	sleep 2	
	/bin/sh $0 status
	;;
  status)
	netstat -lntp | egrep "8851|8861"
	;;
  restart)
	/bin/sh $0 stop
	/bin/sh $0 start	
	;;
  up_local)
	${SVN_CMD} up
	${GRADLE_CMD} jar -Denv=local
        ;;
  up_dev)
	${SVN_CMD} up
	${GRADLE_CMD} jar -Denv=dev
        ;;
  up_alpha)
	${SVN_CMD} up
	${GRADLE_CMD} jar -Denv=alpha
        ;;
  up_product)
	${SVN_CMD} up
	${GRADLE_CMD} jar -Denv=product
        ;;

  *)
	echo $"Usage: $0 {start|stop|status|restart|up_local|up_dev|up_alpha|up_product}"
	RETVAL=2
	;;
esac

exit $RETVAL
