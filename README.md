# Mainframe ingestion in the cloud

This repository is for a demo of doing ingestion of mainframe files defined by COBOL copybooks into a relational database using Spring Cloud Data Flow and it's related ecosystem on CloudFoundry.


java -jar spring-cloud-dataflow-server-local-1.7.0.M1.jar \
    --spring.datasource.url=jdbc:mysql://localhost:3306/store \
    --spring.datasource.username=root \
    --spring.datasource.password=p@ssw0rd \
    --spring.datasource.driver-class-name=org.mariadb.jdbc.Driver \
    --spring.cloud.dataflow.task.maximum-concurrent-tasks=3

app register --name sftp --type source --uri file:///Users/mminella/Documents/IntelliJWorkspace/mainframeingestion/bin/cloudfoundry/apps_static/sftp-source-rabbit-2.0.3.BUILD-SNAPSHOT.jar
app register --name launcher --type sink --uri file:///Users/mminella/Documents/IntelliJWorkspace/mainframeingestion/bin/cloudfoundry/apps_static/task-launcher-dataflow-sink-rabbit-1.0.0.BUILD-SNAPSHOT.jar
app register --name store-ingest --type task --uri file:///Users/mminella/Documents/IntelliJWorkspace/mainframeingestion/bin/local/store-ingestion-0.0.1-SNAPSHOT.jar

dataflow:> task create store-ingest --definition "store-ingest"
dataflow:> stream create file-ingest --definition "sftp --sftp.factory.password=scdf123  --sftp.factory.username=scdf --sftp.factory.host=0.0.0.0 --sftp.factory.allow-unknown-keys=true --sftp.remote-dir=/Users/scdf/input --sftp.local-dir=/Users/mminella/temp/input --task.launch.request.application-name=store-ingest --task.launch.request.format=DATAFLOW  --logging.level.com.jcraft.jsch=ERROR | launcher --spring.cloud.dataflow.client.server-uri=http://localhost:9393/"
dataflow:> stream deploy file-ingest


cd /Users/scdf/input
sudo cp ~/Documents/IntelliJWorkspace/mainframeingestion/file-generator/target/data/*.bin .















DELETE FROM STORE;
DELETE FROM BATCH_JOB_EXECUTION_CONTEXT;
DELETE FROM BATCH_STEP_EXECUTION_CONTEXT;
DELETE FROM BATCH_STEP_EXECUTION;
DELETE FROM BATCH_JOB_EXECUTION_PARAMS;
DELETE FROM BATCH_JOB_EXECUTION;
DELETE FROM BATCH_JOB_INSTANCE;
DELETE FROM TASK_TASK_BATCH;
DELETE FROM TASK_EXECUTION_PARAMS;
DELETE FROM TASK_EXECUTION;
