(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http','$timeout',
            function ($scope, $http, $timeout) {
                console.log("called OrdersController");

                $scope.sendFakeRequest = function(){
                    alert("hello");
                };
            }]);
}());