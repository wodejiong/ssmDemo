app.controller('itemController',function ($scope,$http) {

   $scope.selectedSpec={};
   $scope.selectSpec=function(key,value){
	   $scope.selectedSpec[key]=value;
	   selectSku();
   }
   
   $scope.isSelected=function(key,value){
	   if($scope.selectedSpec[key]==value){
		   return true;
	   }else{
		   return false;
	   }
   }
   
   $scope.num=1;
   $scope.calNum=function(x){
	   $scope.num+=x;
	   if($scope.num<1){
		   $scope.num=1;
	   }
   }
   
  
   $scope.loadSku=function(){
	   $scope.sku=skuList[0];
	   $scope.selectedSpec=JSON.parse(JSON.stringify($scope.sku.spec));
   }
   
   objectMatch=function(map1,map2){
	   for(var k in map1){
		   if(map1[k]!=map2[k]){
			   return false;
		   }
	   }
	   
	   for(var k in map2){
		   if(map2[k]!=map1[k]){
			   return false;
		   }
	   }
	   
	   return true;
   }
   
   
   selectSku=function(){
	   for(var i=0;i<skuList.length;i++){
		   if(objectMatch(skuList[i].spec,$scope.selectedSpec)){
			   $scope.sku=skuList[i];
			   return;
		   }
	   }
	   
	   $scope.sku={'title':'-----','price':0,'id':0}
	   
   }

   $scope.addGoodsToCartList=function () {
	   $http.get("http://localhost:9109/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(function (response) {
		   if(response.success) {
			   location.href="http://localhost:9109/cart.html"
           }else {
               alert(response.message);
           }
       })
   }
   
})