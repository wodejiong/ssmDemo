app.controller("payController",function ($scope,payService,$location) {
    $scope.createNative=function () {
        payService.createNative().success(function (response) {
            $scope.money=response.totalFee;
            $scope.out_trade_no=response.out_trade_no;
            var qr = new QRious({
                element: document.getElementById('qrious'),
                size: 250,
                value: response.code_url
            })
            queryOrder();//生成二维码之后查询订单支付状态
        })
    }
    //查询支付状态
    var queryOrder=function () {
        payService.queryOrder($scope.out_trade_no).success(function (response) {
            if(response.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            }else{
                //如果返回信息为超时,则重新生成二维码
                if(response.message=="timeout") {
                    alert("二维码超时");
                    //$scope.createNative();
                }else{
                    location.href="payfail.html"
                }

            }
        })
    }


    $scope.searchMoney=function () {
        $scope.totalFee= $location.search()['money']
    }
})