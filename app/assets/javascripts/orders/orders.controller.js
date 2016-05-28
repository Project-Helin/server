(function () {
    angular.module('OrdersApp').controller('OrdersController',
        ['$scope', '$http', 'ProductsService', 'OrdersService', function ($scope, $http, ProductsService, OrdersService) {

            console.log("called OrdersController");

            $scope.sendConfirmRequest = function (orderId) {
                console.log("Send confirm for id " + orderId);

                OrdersService
                    .confirmOrder(orderId)
                    .then(function (response) {
                        // reload current page
                        location.reload();
                        console.log("Succesfully confirmed");
                    });
            };

            $scope.sendFakeOrder = function (projectId) {

                var lon = "8.817394673824309";
                var lat = "47.22330617326986";

                ProductsService.findByProjects(projectId).then(function (productArray) {
                    console.log("Got a few products", productArray);

                    var noProductsFound = productArray.length == 0;
                    if (noProductsFound) {
                        alert("This project has no products");
                        return;
                    }

                    var firstProductId = productArray[0].id;
                    sendOrder(firstProductId, projectId);
                });

                function sendOrder(productId, projectId) {
                    var orderCargoDto = {
                        displayName: 'Batman',
                        email: 'batman@wayneenterprise.com',
                        projectId: projectId,
                        customerPosition: {
                            lon: lon,
                            lat: lat
                        },
                        orderProducts: [
                            {
                                id: productId,
                                amount: 10

                            }
                        ]
                    };

                    console.log("Sending orderCargo: ", orderCargoDto);
                    
                    OrdersService.create(orderCargoDto)
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