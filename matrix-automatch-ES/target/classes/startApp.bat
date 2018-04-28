@ECHO OFF

set MAIN_CLASS=com.secant.match.main.Start
set MAIN_JAR=matrix-automatch-1.2.jar

set APP_HOME=G:\Application_Execution\matrix-1.2\matrix-automatch

set CP=%APP_HOME%
set CP=%CP%;%APP_HOME%\%MAIN_JAR%

set JAVA_OPTS=-Xms512M -Xmx1024M -XX:PermSize=128M -XX:MaxPermSize=512M

java %JAVA_OPTS% -cp %CP% %MAIN_CLASS% > run.log