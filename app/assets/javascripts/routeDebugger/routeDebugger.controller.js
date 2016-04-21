(function () {
    angular.module('RouteDebugger').controller('RouteDebuggerCtrl', ['$scope', '$http', 'RouteDebuggerService', 'ProjectsService','$timeout',
        function ($scope, HelperService, $http, ProjectsService, $timeout) {

            function initialize() {
                $scope.selectedZone = null;
                $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
                $scope.projectId = document.getElementById('projectId').value;
                $scope.project = {};

                if ($scope.projectId) {
                    ProjectsService.loadProject($scope.projectId).then(function (project) {
                        $scope.project = project;
                    });
                }
            }

            initialize();


        }]);

}());