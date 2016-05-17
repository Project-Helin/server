(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http', function ($scope, $http) {

            console.log("called OrdersController");

            $scope.sendConfirmRequest = function (orderId) {
                console.log("Send confirm for id " + orderId);

                $http.post('/api/orders/' + orderId + "/confirm", {})
                    .then(function (response) {
                        // reload current page
                        location.reload();
                        console.log("Succesfully confirmed");
                    });
            };

            $scope.sendFakeRequest = function () {

                $http.get('/api/products/').then(function (response) {

                    var productArray = response.data;
                    console.log("Got a few products", productArray);

                    var firstProductId = productArray[0].id;
                    var projectId = productArray[0].project;
                    sendOrder(firstProductId, projectId);
                });

                function sendOrder(productId, projectId) {
                    var orderCargoDto = {
                        displayName: 'Batman',
                        email: 'batman@wayneenterprise.com',
                        projectId: projectId,
                        customerPosition: {
                            lon: "8.817394673824309",
                            lat: "47.22330617326986"
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