(function () {
    angular.module('RouteDebugger').controller('RouteDebuggerCtrl', ['$scope', '$http', 'RouteDebuggerService', 'ProjectsService', '$timeout',
        function ($scope, HelperService, $http, ProjectsService) {

            function initialize() {
                $scope.data = {
                    selectedProject: null,
                    routeData: {
                        dronePosition: null,
                        customerPosition: null
                    }
                };

                $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];

                ProjectsService.loadProjects().then(function (projects) {
                    $scope.data.projects = projects;
                });
            }
            
            $scope.calculateRoute = function () {
                if ($scope.data.routeData.dronePosition && $scope.data.routeData.customerPosition) {
                    console.log($scope.data.routeData.dronePosition);
                    console.log($scope.data.routeData.customerPosition);
                } else {
                    toastr.error('Please click on the map to add a drone and a customer position', 'Error');
                }
            };


            initialize();


        }]);

}());