app.service("payService",function ($http) {
    this.createNative=function () {
        return $http.get("./pay/createNative.do");
    }

    this.queryOrder=function (out_trade_no) {
        return $http.get("./pay/queryOrder.do?out_trade_no=" + out_trade_no);
    }
})