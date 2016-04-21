(function () {
    angular.module('RouteDebugger').controller('RouteDebuggerCtrl', ['$scope', '$http', 'RouteDebuggerService', 'ProjectsService', '$timeout',
        function ($scope, HelperService, $http, ProjectsService) {

            function initialize() {
                $scope.selectedZone = null;
                $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
                $scope.project = {};

                ProjectsService.loadProjects().then(function (projects) {
                    $scope.projects = projects;
                    console.log(projects);
                });
                
            }

            initialize();


        }]);

}());