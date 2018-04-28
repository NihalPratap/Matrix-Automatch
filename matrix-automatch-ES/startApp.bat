@ECHO OFF

set MAIN_CLASS=com.secant.match.main.Start
set MAIN_JAR=automatch-1.0.jar

set path=%PATH%;C:\Program Files\Java\jdk1.6.0_22\bin


set DUMP_HOME=C:\Users\Irfan\Automatch

set CP=%DUMP_HOME%
set CP=%CP%;%DUMP_HOME%\%MAIN_JAR%


set JAVA_OPTS=-Xms128M -Xmx512M -XX:PermSize=128M -XX:MaxPermSize=512M

java %JAVA_OPTS% -cp %CP% %MAIN_CLASS% 