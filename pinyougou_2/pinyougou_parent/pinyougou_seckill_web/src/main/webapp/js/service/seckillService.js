app.service('seckillService',function ($http) {
    this.findList=function () {
        return $http.get("./seckillGoods/findList.do")
    }


    this.findSeckillGoods=function (seckillGoodId) {
        return $http.get("./seckillGoods/findSeckillGoods.do?seckillGoodId="+seckillGoodId)
    }


    this.saveScekillOrderToRedis=function (seckillGoodId) {
        return $http.get("./seckillOrder/saveScekillOrderToRedis.do?seckillGoodId="+seckillGoodId);
    }
})