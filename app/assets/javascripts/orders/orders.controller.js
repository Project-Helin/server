(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http','$timeout',
            function ($scope, $http, $timeout) {
                console.log("called OrdersController");

                $scope.sendFakeRequest = function(){

                    var  orderCargoDto = {
                        displayName: 'Batman',
                        email: 'batman@wayneenterprise.com',
                        orderProducts: [
                            {
                                productId: '400022bd-2555-4443-9ecd-432b5e65d4b3',
                                amount: 10
                            }
                        ]
                    };

                    console.log("Sending orderCargo: ", orderCargoDto);
                    $http.post('/api/orders/', orderCargoDto)
                        .then(function (response) {
                            console.log("Got response back", response.data);
                            location.reload();
                            return response.data;
                        })
                        .catch(function (error) {
                            console.log("Failed to save ...", response);
                            return error;
                        });
                };
            }]);
}());