(function () {
    angular.module('ProjectsApp').controller('ProjectsController', ['$scope', 'HelperService', function ($scope, HelperService) {

        function initialize() {
            $scope.selectedZone = null;
            $scope.zoneTypes = ['OrderZone', 'FlightZone', 'DeliveryZone', 'LoadingZone'];
        }

        var defaultZoneTemplate = {
            id: HelperService.generateUUID(),
            polygon: null,
            height: 10,
            type: 'OrderZone'
        };

        $scope.zones = [
            {
                id: 1,
                name: null,
                polygon: 'POLYGON((8.81647686634051 47.2235972073977,8.81666981124281 47.2234438666848,8.81619360680309 47.2231567001565,8.8160622400611 47.2230535428686,8.81595550458323 47.2230145103289,8.81565171899238 47.2232598572438,8.8154505636687 47.2234578067679,8.81533972298015 47.2237003636278,8.81538898550839 47.2238313996306,8.81543003761526 47.2238564915941,8.81599245147942 47.2233434979779,8.81607455569316 47.2233044656518,8.81609918695728 47.2233797422551,8.81609918695728 47.2233797422551,8.81647686634051 47.2235972073977))',
                height: 10,
                type: 'OrderZone'
            },
            {
                id: 2,
                name: null,
                polygon: 'POLYGON((8.81691612388404 47.2239986791635,8.81729380326726 47.2240209830613,8.8176673774398 47.2240098311136,8.81834063199251 47.2239206154471,8.81818873919708 47.2238760075576,8.81804916203371 47.2238453396118,8.8179260057131 47.2238286116339,8.81781105981385 47.2238063076551,8.81774127123217 47.2238118836507,8.81762222012224 47.2237616996694,8.81750316901231 47.2238815835459,8.81721580426421 47.2237728516693,8.81721580426421 47.2237728516693,8.81691612388404 47.2239986791635))',
                height: 50,
                type: 'FlightZone'
            },
            {
                id: 3,
                name: null,
                polygon: null,
                height: 20,
                type: 'DeliveryZone'
            },
            {
                id: 4,
                name: null,
                polygon: null,
                height: 10,
                type: 'LoadingZone'
            }
        ];

        $scope.selectZone = function (zone) {
            $scope.selectedZone = zone;
        };

        $scope.createZone = function () {
            var newZone = angular.copy(defaultZoneTemplate);
            $scope.zones.splice(0, 0, newZone);
            $scope.selectZone(newZone);
        };

        $scope.deleteZone = function (zoneToDelete) {
            $scope.zones = $scope.zones.filter(function (zone) {
                return zone.id !== zoneToDelete.id;
            });
        };

        initialize();


    }]);

}());