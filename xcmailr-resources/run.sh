CURR_DIR=`pwd`
JVM_OPTS=-Dsun.lang.ClassLoader.allowArraySyntax=true 
XCMAILR_OPTS="-Dxcmailr.xcmstarter.home=$CURR_DIR -Dxcmailr.xcmstarter.host=localhost -Dxcmailr.xcmstarter.port=8080 -Dninja.external.configuration=conf/application.conf -Dninja.mode=prod "
#LOG4J_OPTS=-Dslf4j=false -Dlog4j.configuration=file:.\\conf\\log4j.properties 
JETTY_OPTS=-Dorg.eclipse.jetty.util.log.stderr.DEBUG=true
XCMAILR_JAR=./xcmailr-jetty-starter-1.0.jar
echo $JVM_OPTS $XCMAILR_OPTS $JETTY_OPTS -jar $XCMAILR_JAR
java $JVM_OPTS $XCMAILR_OPTS $JETTY_OPTS -jar $XCMAILR_JAR

