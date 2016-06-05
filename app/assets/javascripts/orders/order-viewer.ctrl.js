(function () {
    angular.module('OrderViewer').controller('OrderViewerCtrl', ['$scope', 'HelperService', 'OrdersService', 'ProjectsService', 'gisHelper',
        function ($scope, HelperService, OrdersService, ProjectsService, gisHelper) {


            function initialize() {
                $scope.data = {};
                $scope.data.focusDrone = false;
                
                $scope.zoneColors = angular.copy(gisHelper.zoneColors);
                $scope.zoneColors["Calculated Route"] = gisHelper.calculatedRouteColor;
                $scope.zoneColors["Flown Route"] = gisHelper.flownRouteColor;

                OrdersService.loadOrder($scope.orderId).then(function(order) {
                    $scope.data.order = order;
                    initializeWebSocketConnection(order.missions);
                    
                    order.missions.forEach(function(mission) {
                        mission.active = true;
                    });

                    ProjectsService.loadProject($scope.data.order.projectId).then(function(project) {
                        $scope.data.project = project;
                    })
                });
            }

            function initializeWebSocketConnection(missions) {
                missions.forEach(function (mission) {
                    var ws = new WebSocket("wss://localhost:9000/api/missions/" + mission.id + "/ws");

                    ws.onmessage = function (event) {
                        var droneInfoMessage = JSON.parse(event.data);
                        var droneInfo = droneInfoMessage.droneInfo;
                        console.log(droneInfo);
                        $scope.$broadcast('DroneInfoReceived', {droneInfo: droneInfo, missionId: mission.id});
                    };
                });
            }

            initialize();
        }
    ])
})();