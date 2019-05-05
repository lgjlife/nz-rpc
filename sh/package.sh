
echo  #################################
echo  #################################
echo  #################################
pwd
echo
echo  ################################


cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-consumer

pwd

mvn package -Dmaven.test.skip=true

cd -
############################################

echo  #################################
echo  #################################
echo  #################################
pwd
echo
echo  ################################
cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-provider
pwd
echo 
mvn package -Dmaven.test.skip=true

cd -

###################################

echo  #################################
echo  #################################
echo  #################################

echo
echo  ################################

cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-provider
pwd

mvn package -Dmaven.test.skip=true

cd -

############################################

echo  #################################
echo  #################################
echo  #################################
pwd
echo
echo  ################################
cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-test
pwd
echo
mvn package -Dmaven.test.skip=true

cd -


