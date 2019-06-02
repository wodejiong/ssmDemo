app.controller('userController',function ($scope,userService) {

    $scope.reg=function () {
        if(!$scope.entity.password==$scope.password) {
            alert("两次密码不一致");
            return;
        }
        userService.add($scope.entity,$scope.smsCode).success(function (response) {
                alert(response.message);
        })
    };

    $scope.sendCode=function () {
        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message)
        });
    }

});