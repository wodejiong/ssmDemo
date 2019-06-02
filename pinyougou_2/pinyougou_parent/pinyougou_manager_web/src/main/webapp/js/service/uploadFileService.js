app.service('uploadFileService',function ($http) {
    this.uploadFile=function () {
        var formdata = new FormData();
        formdata.append('file', file.files[0]);
        return $http({
            method:'post',
            url:'../uploadFile.do',
            data:formdata,
            headers:{'Content-Type':undefined},
            transformRequest: angular.identity
        })
    }
})