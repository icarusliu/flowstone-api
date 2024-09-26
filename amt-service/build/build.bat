set serviceName=amt-service
cd ../
call mvn clean package -DskipTests
call cd ./build
call copy ..\base-service\target\base-service-0.0.1-SNAPSHOT.jar .\

call rd /s /q .\dist\*.*
call xcopy ..\..\amt-web\dist\ .\dist\ /e /y

call docker build -t %serviceName%:%1 .