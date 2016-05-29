(function () {
    angular.module('RouteDebugger').controller('RouteDebuggerCtrl', ['$scope', 'ProjectsService',
        function ($scope, ProjectsService) {

            function initialize() {
                $scope.data = {
                    selectedProject: null,
                    routeData: {
                        dronePosition: null,
                        customerPosition: null
                    },
                    routeWayPoints: null
                };

                $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];

                ProjectsService.loadProjects().then(function (projects) {
                    $scope.data.projects = projects;
                    if($scope.projectId) {
                        var foundProject = $scope.data.projects.filter(function(project) {
                            return project.id === $scope.projectId;
                        });
                        if (foundProject.length) {
                            $scope.data.selectedProject = foundProject[0];
                        }
                    }
                });
            }
            
            $scope.calculateRoute = function () {
                if ($scope.data.routeData.dronePosition && $scope.data.routeData.customerPosition) {
                    console.log($scope.data.routeData.dronePosition);
                    ProjectsService.calculateRouteForProject(
                        $scope.data.selectedProject.id,
                        $scope.data.routeData.dronePosition, 
                        $scope.data.routeData.customerPosition)
                        .then(function (route) {
                           $scope.data.routeWayPoints = route.wayPoints;
                        }, function(error){
                            $scope.data.routeWayPoints = [];
                            console.log('Got error', error);
                            toastr.error(error.data, 'Error');
                        });
                } else {
                    toastr.error('Please click on the map to add a drone and a customer position', 'Error');
                }
            };
            
            $scope.reset = function() {
                $scope.$broadcast('resetMap');
                $scope.data.routeWayPoints = null;
            };


            initialize();


        }]);

}());