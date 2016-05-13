(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http','$timeout',
            function ($scope, $http, $timeout) {
                console.log("called OrdersController");

                $scope.sendConfirmRequest = function(orderId){
                    console.log("Send confirm for id " + orderId);

                    $http.post('/api/orders/' + orderId + "/confirm", {})
                         .then(function(response){
                             // reload current page
                             location.reload();
                             console.log("Succesfully confirmed");
                         });
                };

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
                            customerPosition: {
                                lat: "8.817697763442991",
                                lon: "47.223881796664756"
                            },
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