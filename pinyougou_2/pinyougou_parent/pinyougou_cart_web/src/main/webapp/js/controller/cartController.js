app.controller('cartController',function ($scope,cartService,orderService) {

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            $scope.totalValue=cartService.countTotal($scope.cartList);
        });
    };

    //改变明细列表的数量
    $scope.changeNumberOfItem = function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if(response.success) {
                $scope.findCartList();
            }else {
                alert(response.message)
            }
        })
    };
    //通过用户名查询地址列表
    $scope.findAddressList=function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList=response;
            for(var i=0;i<$scope.addressList.length;i++) {
                if($scope.addressList[i].isDefault=="1") {
                    $scope.choseAddress($scope.addressList[i])
                }
            }
        })
    }

    $scope.choseAddress=function (address) {
        $scope.address=address;
    }
    $scope.isChosenAddress=function (address) {
        if($scope.address==address) {
            return true;
        }
        return false;
    }

    $scope.order = {paymentType: '1'};
    $scope.chosePaymentType=function (type) {
        $scope.order.paymentType=type;
    }

    $scope.save=function () {
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        orderService.add($scope.order).success(function (response) {
            if(response.message) {
                if($scope.order.paymentType=="1") {
                    location.href="pay.html"
                }else{
                    location.href="paysuccess.html"
                }

            }else{
                alert(response.message);
            }
        })
    }
});