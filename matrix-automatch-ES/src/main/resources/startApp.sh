MAIN_CLASS=com.secant.match.main.Start
MAIN_JAR=matrix-automatch-1.2.jar

APP_HOME=/home/matrix/matrix-1.2/matrix-automatch

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA="java"
fi

# Setup the classpath
CP="$APP_HOME"
CP="$CP:$APP_HOME/$MAIN_JAR"

JAVA_OPTS="-Xms128m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m"

exec $JAVA $JAVA_OPTS -cp $CP $MAIN_CLASS