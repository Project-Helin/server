(function () {
    angular.module('ProjectsApp').controller('ProjectsController',
        ['$scope', 'HelperService', '$http', 'ProjectsService', '$timeout',
            function ($scope, HelperService, $http, ProjectsService, $timeout) {

                function initialize() {
                    $scope.data = {
                        selectedZone: null
                    };
                    $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
                    $scope.projectId = document.getElementById('projectId').value;
                    $scope.project = {};
                    $scope.zones = [];

                    if ($scope.projectId) {
                        ProjectsService.loadProject($scope.projectId).then(function (project) {
                            $scope.project = project;
                            $scope.zones = project.zones;
                            $timeout(function () {
                                removeLeaveConfirmation();
                            }, 500);
                        });
                    }
                }

                $scope.$watch('project', function (newVal, oldVal) {
                    window.onbeforeunload = confirmOnPageExitIfUnsavedChanges;
                }, true);

                var defaultZoneTemplate = {
                    polygon: null,
                    height: 10,
                    type: 'OrderZone'
                };

                $scope.selectZone = function (zone) {
                    $scope.data.selectedZone = zone;
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
                    if (!$scope.project.name) {
                        toastr.error('Error', 'Please ad a Project Title before saving.');
                        return;
                    }

                    if ($scope.projectId) {
                        $scope.project.id = $scope.projectId;
                    } else {
                        var newId = HelperService.generateUUID();
                        $scope.project.id = newId ;
                        $scope.projectId = newId;
                    }

                    $scope.project.zones = $scope.zones;

                    ProjectsService.saveProject($scope.project).then(function () {
                        removeLeaveConfirmation();
                        toastr.success('Success', 'Saved successfully.');
                    });

                };

                function confirmOnPageExitIfUnsavedChanges() {
                    return "There are unsaved changes";
                }

                function generateRandomZoneName() {
                    return "Zone" + Math.floor((Math.random() * 1000) + 1);
                }

                function removeLeaveConfirmation() {
                    window.onbeforeunload = null;
                }


                initialize();


            }]);

}());