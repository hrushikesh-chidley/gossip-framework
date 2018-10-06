export JAVA_CP=/Users/Rishi/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/org/reflections/reflections/0.9.11/reflections-0.9.11.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/com/google/guava/guava/20.0/guava-20.0.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/org/javassist/javassist/3.21.0-GA/javassist-3.21.0-GA.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/com/framework/gossip/gossip-core/1.0.0-SNAPSHOT/gossip-core-1.0.0-SNAPSHOT.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/com/framework/gossip/monitoring-extension/1.0.0-SNAPSHOT/monitoring-extension-1.0.0-SNAPSHOT.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/com/framework/gossip/monitoring-extension/1.0.0-SNAPSHOT/monitoring-extension-1.0.0-SNAPSHOT-tests.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar
export JAVA_CP=$JAVA_CP:/Users/Rishi/.m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar

java -cp .:$JAVA_CP com.monitoring.extension.tests.SampleMicroservice2
