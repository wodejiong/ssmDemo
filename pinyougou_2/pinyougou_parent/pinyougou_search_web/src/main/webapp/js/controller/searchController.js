app.controller('searchController', function ($scope,$location, searchService) {


    $scope.searchMap = {
        "keywords": "",
        "category": "",
        "brand": "",
        "spec": {},
        "price": "",
        "pageNo": 1,
        "pageSize": 40,
        "sort": "",
        "sortField": ""
    };

    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        $scope.searchMap.pageSize = parseInt($scope.searchMap.pageSize);
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            $scope.searchByPage();
        })
    }

    $scope.searchByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPage) {
            return;
        }
        if (pageNo) {
            $scope.searchMap.pageNo = pageNo;
        }
        navigation();
    }

    function navigation() {
        $scope.navigations = [];
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPage;
        $scope.preSpot = true;
        $scope.postSpot = true;

        if ($scope.resultMap.totalPage > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                $scope.preSpot = false;
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPage - 2) {
                firstPage = $scope.resultMap.totalPage - 4;
                $scope.postSpot = false;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.preSpot = false;
            $scope.postSpot = false;
        }


        for (var i = firstPage; i <= lastPage; i++) {
            $scope.navigations.push(i);
        }
    }


    $scope.addSearchItem = function (key, value) {
        if (key == "brand" || key == "category" || key == "price") {
            $scope.searchMap[key] = value
        } else {
            $scope.searchMap.spec[key] = value
        }
        $scope.search();
    }


    $scope.removeSearchItem = function (key) {
        if (key == "brand" || key == "category" || key == "price") {
            $scope.searchMap[key] = ''
        } else {
            delete $scope.searchMap.spec[key]
        }
        $scope.search();
    }

    $scope.order = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    };

    $scope.containBrand = function () {

        var brandList = $scope.resultMap.brandList;
        for (var i = 0; i < brandList.length; i++) {
           if($scope.searchMap.keywords.indexOf(brandList[i].text)>=0) {
               return false;
           }
        }
        return true;
    };

    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords']
        $scope.search();
    }
})