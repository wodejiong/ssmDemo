app.controller('contentController',function ($scope,contentService) {

    $scope.categoryItems=[];
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.categoryItems[categoryId]=response;
        })
    }

    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.searchKeywords;
    }
})