(function () {
    angular.module('OrderViewer').controller('OrderViewerCtrl', ['$scope', 'HelperService', 'OrdersService',
        function ($scope, HelperService, OrdersService) {

            function initialize() {
                $scope.data = {};

                OrdersService.loadOrder($scope.orderId).then(function(order) {
                    $scope.data.order = order;
                });

            }
            
            initialize();
        }
    ])
})();