
echo  #################################
echo  #################################
echo  #################################
pwd
echo
echo  ################################


cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-consumer

pwd

mvn package

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
mvn package

cd -

###################################

echo  #################################
echo  #################################
echo  #################################

echo
echo  ################################

cd ~/aProject/aRealPrj/nz-rpc/app-demo/app-provider
pwd

mvn package

cd -

