(function () {
    angular.module('ProjectsApp').controller('ProjectsController', ['$scope', 'HelperService', '$http', 'ProjectsService', '$timeout', function ($scope, HelperService, $http, ProjectsService, $timeout) {

        function initialize() {
            $scope.selectedZone = null;
            $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
            $scope.projectId = document.getElementById('projectId').value;


            ProjectsService.loadProject($scope.projectId).then(function (project) {
                $scope.project = project;
                $scope.zones = project.zones;
                $timeout(function () {
                    removeLeaveConfirmation();
                }, 500);
            });


        }

        function generateRandomZoneName() {
            return "Zone" + Math.floor((Math.random() * 1000) + 1);
        }

        function removeLeaveConfirmation() {
            window.onbeforeunload = null;
        }

        $scope.$watch('project', function (newVal, oldVal) {
            console.log("ChangesDetected");
            window.onbeforeunload = confirmOnPageExitIfUnsavedChanges;
        }, true);

        var defaultZoneTemplate = {
            polygon: null,
            height: 10,
            type: 'OrderZone'
        };

        $scope.project = {};
        $scope.zones = [];
        $scope.selectZone = function (zone) {
            $scope.selectedZone = zone;
        };

        $scope.createZone = function () {
            var newZone = angular.copy(defaultZoneTemplate);
            newZone.id = HelperService.generateUUID();
            newZone.name = generateRandomZoneName();
            $scope.zones.splice(0, 0, newZone);
            $scope.selectZone(newZone);
        };

        $scope.deleteZone = function (zoneToDelete) {
            $scope.zones = $scope.zones.filter(function (zone) {
                return zone.id !== zoneToDelete.id;
            });
        };

        $scope.save = function () {
            $scope.project.zones = $scope.zones;

            ProjectsService.saveProject($scope.project).then(function () {
                removeLeaveConfirmation();
                toastr.success('Success', 'Saved successfully.');
            });

        };

        function confirmOnPageExitIfUnsavedChanges() {
            return "There are unsaved changes";
        }

        initialize();


    }]);

}());