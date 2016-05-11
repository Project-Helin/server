(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http','$timeout',
            function ($scope, $http, $timeout) {
                console.log("called OrdersController");

                $scope.sendFakeRequest = function(){

                    $http.get('/api/products/').then(function(response){

                        var productArray = response.data;
                        console.log("Got a few products", productArray);

                        var firstProductId = productArray[0].id;
                        sendOrder(firstProductId);
                    });

                    function sendOrder(productId) {
                        var orderCargoDto = {
                            displayName: 'Batman',
                            email: 'batman@wayneenterprise.com',
                            orderProducts: [
                                {
                                    productId: productId,
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
                    }
                };
            }]);
}());