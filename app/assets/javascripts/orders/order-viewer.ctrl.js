(function () {
    angular.module('OrderViewer').controller('OrderViewerCtrl', ['$scope', 'HelperService', 'OrdersService', 'ProjectsService',
        function ($scope, HelperService, OrdersService, ProjectsService) {


            function initialize() {
                $scope.data = {};
                $scope.data.focusDrone = false;

                OrdersService.loadOrder($scope.orderId).then(function(order) {
                    $scope.data.order = order;
                    initializeWebSocketConnection(order.missions);

                    ProjectsService.loadProject($scope.data.order.projectId).then(function(project) {
                        $scope.data.project = project;
                    })
                });
            }

            function initializeWebSocketConnection(missions) {
                missions.forEach(function (mission) {
                    var ws = new WebSocket("ws://localhost:9000/api/missions/" + mission.id + "/ws");

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