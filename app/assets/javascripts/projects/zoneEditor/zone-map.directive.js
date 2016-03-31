(function () {
    angular.module('ProjectsApp').directive('zoneMap', [function () {
        return {
            restrict: 'E',
            scope: {
                zones: '=',
                selectedZone: '=',
                center: '='
            },
            replace: true,
            template: '<div id="map" class="map"></div>',
            link: function (scope) {

                var raster = new ol.layer.Tile({
                    source: new ol.source.OSM()
                });

                var format = new ol.format.WKT();

                scope.polygons = getPolygonsFromZones(scope.zones);

                var vector = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: scope.polygons
                    })
                });

                scope.map = new ol.Map({
                    target: 'map',
                    layers: [raster, vector],
                    view: new ol.View({
                        center: [981481.3,5978619.7],
                        zoom: 18
                    })
                });

                function getPolygonsFromZones(zones) {
                    var polygons = [];

                    zones.forEach(function (zone) {
                        if (zone.polygon !== null) {
                            polygons.push(
                                format.readFeature(zone.polygon, {
                                    dataProjection: 'EPSG:4326',
                                    featureProjection: 'EPSG:3857'
                                })
                            );
                        }
                    });

                    return polygons;
                }

            },
            controller: 'ZoneMapCtrl'
        };


    }]);


}());