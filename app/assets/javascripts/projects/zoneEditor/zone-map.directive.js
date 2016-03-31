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

                var modify = new ol.interaction.Modify({
                    features: scope.polygons,
                    // the SHIFT key must be pressed to delete vertices, so
                    // that new vertices can be drawn at the same position
                    // of existing vertices
                    deleteCondition: function(event) {
                        return ol.events.condition.shiftKeyOnly(event) &&
                            ol.events.condition.singleClick(event);
                    }
                });

                scope.map.addInteraction(modify);

                function addInteraction() {
                    var draw = new ol.interaction.Draw({
                        features: scope.polygons,
                        type: 'Polygon'
                    });

                    scope.map.addInteraction(draw);
                }

                function getPolygonsFromZones(zones) {
                    var polygons = new ol.Collection();

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

                addInteraction();

            },
            controller: 'ZoneMapCtrl'
        };


    }]);


}());