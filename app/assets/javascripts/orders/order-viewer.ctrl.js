(function () {
    angular.module('OrderViewer').controller('OrderViewerCtrl', ['$scope', 'HelperService', 'OrdersService', 'ProjectsService',
        function ($scope, HelperService, OrdersService, ProjectsService) {

            function initialize() {
                $scope.data = {};

                OrdersService.loadOrder($scope.orderId).then(function(order) {
                    $scope.data.order = order;
                    
                    ProjectsService.loadProject($scope.data.order.projectId).then(function(project) {
                        $scope.data.project = project;
                    })
                });

                
            }
            
            initialize();
        }
    ])
})();