#去除打包
cd rpc-support
mvn clean install
cd .. 

cd rpc-client
mvn clean install
cd .. 


cd rpc-server
mvn clean install
cd .. 

#打包到本地
cd rpc-suport
mvn  install
cd .. 

cd rpc-client
mvn install
cd .. 


cd rpc-server
mvn  install
cd .. 
