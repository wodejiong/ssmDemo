//服务层
app.service('cartService',function($http){
     this.findCartList=function () {
         return  $http.get("../cart/findCartList.do")
     }

    this.addGoodsToCartList = function (itemId, num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
    };


    this.countTotal=function (cartList) {
        var totalValue = {totalCount: 0, totalMoney: 0};
        for(var i=0;i<cartList.length;i++) {
            var cart=cartList[i]
            for(var j=0;j<cart.orderItemList.length;j++) {
                totalValue.totalCount+=cart.orderItemList[j].num;
                totalValue.totalMoney+=cart.orderItemList[j].totalFee;
            }
        }

        return totalValue;
    }

    //通过用户名查询地址列表,用户名由security提供
    this.findAddressList=function () {
        return $http.get("../address/findAddressByUsername.do")
    }
});