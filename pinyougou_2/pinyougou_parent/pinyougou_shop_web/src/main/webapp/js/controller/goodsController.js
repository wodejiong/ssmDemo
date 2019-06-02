//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadFileService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                editor.html('$scope.entity.goodsDesc.introduction')

                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);

                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        );
    }

    //保存
    $scope.save = function () {
        var obj;
        if ($scope.entity.goods.id) {
            //修改
            obj = goodsService.update($scope.entity)
        } else {
            //增加
            $scope.entity.goodsDesc.introduction = editor.html();
            obj = goodsService.add($scope.entity)
        }

        obj.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功")
                    location.href = "goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    $scope.resultEntity = {};
    //上传文件
    $scope.uploadFile = function () {
        uploadFileService.uploadFile().success(function (response) {
            if (response.success) {
                alert(response.message);
                $scope.resultEntity.url = response.message;
            } else {
                alert(response.message);
            }
        })
    }

    //将resultEntity中的数据保存到要提交的entity实例中
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: []};
    $scope.saveIntoImageList = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.resultEntity);
    }

    $scope.delFromItemImages = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //查询一级分类
    $scope.findCategory1 = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.category1List = response;
        })
    }

    //查询二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category2List = response;
        })
    })
    //查询三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category3List = response;
        })
    })
    //查询模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    })

    //查询品牌
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.templateBrandList = response;
            $scope.templateBrandList.brandIds = JSON.parse($scope.templateBrandList.brandIds);
            if (!$location.search()['id'])
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.templateBrandList.customAttributeItems);
        })

        typeTemplateService.findResult(newValue).success(function (response) {
            $scope.templateSpecIds = response;
        })
    })


    //选中规格选项后，将其保存到$scope.entity.goodsDesc.specificationItems中
    $scope.addSpecItems = function (specName, specOption) {
        var list = $scope.entity.goodsDesc.specificationItems;
        var object = $scope.findObjectByKey(list, "attributeName", specName);
        if (object) {
            //specName若存在
            var flag = false;
            for (var i = 0; i < object.attributeValue.length; i++) {
                if (object.attributeValue[i] == specOption) {
                    flag = true;
                }
            }
            if (flag) {
                object.attributeValue.splice(object.attributeValue.indexOf(specOption), 1)
                if (object.attributeValue.length == 0) {
                    list.splice(list.indexOf(object), 1);
                }
            } else {
                object.attributeValue.push(specOption);
            }
        } else {
            //specName不存在
            list.push({"attributeValue": [specOption], "attributeName": specName})
        }
    }

    $scope.addItems = function () {
        var list = $scope.entity.goodsDesc.specificationItems;
        $scope.entity.itemList = [{price: 0, num: 9999, status: 0, isDefault: 0, spec: {}}];

        for (var i = 0; i < list.length; i++) {
            $scope.entity.itemList = addItemValue($scope.entity.itemList, list[i].attributeName, list[i].attributeValue);
        }
    }

    addItemValue = function (list, specName, specOptions) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < specOptions.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[specName] = specOptions[j];
                newList.push(newRow);
            }

        }
        return newList;
    }


    $scope.statusList = ['未审核', '已审核', '已驳回', '已关闭'];
    $scope.categoryList = [];
    $scope.findCategoryList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.categoryList[response[i]['id']] = response[i]['name'];
            }

        })
    }

    $scope.findIsChecked = function (specName, specOption) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.findObjectByKey(items, "attributeName", specName);
        if (obj) {
            for (var i = 0; i < obj.attributeValue.length; i++) {
                if (obj.attributeValue.indexOf(specOption) >= 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
});

