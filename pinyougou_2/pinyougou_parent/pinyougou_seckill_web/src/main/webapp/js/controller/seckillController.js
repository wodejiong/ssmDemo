app.controller('seckillController',function ($scope,seckillService,$location,$interval) {
    $scope.findList=function () {
        seckillService.findList().success(function (response) {
            $scope.seckillGoodsList=response;
        })
    }

    $scope.findSeckillGoods=function () {
        var seckillGoodId=$location.search()['seckillGoodId']
        seckillService.findSeckillGoods(seckillGoodId).success(function (response) {
            $scope.entity = response;
        })
    }

    var timer=$interval(function () {
        var second = (new Date($scope.entity.endTime).getTime() - new Date())/1000;
        second--;
        $scope.timeString=convertSecondToString(second);
        if(second<=0) {
            $interval.cancel(timer);
        }
    },1000)

    var convertSecondToString=function (second) {
        var days=Math.floor(second/(60*60*24));
        var hours=Math.floor((second-days*(60*60*24))/3600)
        var minutes=Math.floor(((second-days*(60*60*24)-hours*3600)/60))
        var sec = (second - days * (60 * 60 * 24) - hours * 3600 - minutes * 60).toFixed(0);
        var daysString = "";
        if(days>0) {
            daysString=days+"天"
        }
        return daysString + hours + ":" + minutes + ":" + sec;
    }

    /**
     * 抢购商品,若成功,调用后端方法,将订单生成到redis中
     */
    $scope.saveScekillOrderToRedis=function () {
        seckillService.saveScekillOrderToRedis($scope.entity.id).success(function (response) {
            if(response.success) {//如果抢购成功
                alert("抢购成功,请在5分钟之内付款");
                location.href = "pay.html";
            }else {
                alert(response.message)
            }
        })
    }
})