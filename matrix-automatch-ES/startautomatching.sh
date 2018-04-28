MAIN_CLASS=com.secant.match.main.Start
MAIN_JAR=migration-automatching-1.0.jar

#Change the $HOME to absolute path to sop-analysis-1.0 directory
AUTOMATCH_HOME=`cd /home/matrix/Automatch; pwd`

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA="java"
fi

# Setup the classpath
CP="$AUTOMATCH_HOME"
CP="$CP:$AUTOMATCH_HOME/$MAIN_JAR"

JAVA_OPTS="-Xms128m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m"

exec $JAVA $JAVA_OPTS -cp $CP $MAIN_CLASS $1
