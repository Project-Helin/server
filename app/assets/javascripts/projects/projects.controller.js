(function () {
    angular.module('ProjectsApp').controller('ProjectsController', ['$scope', function ($scope) {

        function initialize() {
            $scope.selectedZone = null;

        }

        $scope.zones = [
            {
                area: null,
                height: 10,
                type: 'OrderZone'
            },
            {
                area: null,
                height: 50,
                type: 'FlightZone'
            },
            {
                area: null,
                height: 20,
                type: 'DeliveryZone'
            },
            {
                area: null,
                height: 10,
                type: 'LoadingZone'
            }
        ];

        $scope.selectZone = function (zone) {
            $scope.selectedZone = zone;
        };

        initialize();


    }]);

}());