(function () {
    angular.module('ProjectsApp').controller('ProjectsController', ['$scope', 'HelperService', '$http', function ($scope, HelperService, $http) {

        function initialize() {
            $scope.selectedZone = null;
            $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
            $scope.projectId = document.getElementById('projectId').value;


            $http({
                method: 'GET',
                url: 'http://localhost:9000/projects/' + $scope.projectId
            }).then(function successCallback(response) {
                $scope.project = response.data;
                $scope.zones = $scope.project.zones;

                console.log("Got project", $scope.project);
            }, function errorCallback(response) {
                console.log("Failed to get response", e);
            });
        }

        function generateRandomZoneName() {
            return "Zone" + Math.floor((Math.random() * 1000) + 1);
        }

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
            console.log("Save project");
            console.log($scope.project);

            $scope.project.zones = $scope.zones;
            var projectToSave = $scope.project;

            $http({
                method: 'POST',
                url: 'http://localhost:9000/projects/' + $scope.projectId + '/update',
                data: projectToSave
            }).then(function successCallback(response) {
                console.log("Saved successfully");

                alert("Saved successfully.")
            }, function errorCallback(response) {
                console.log("Failed to save ...", response);
            });

        };

        initialize();


    }]);

}());