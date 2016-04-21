(function () {
    angular.module('common').directive('routeMap', ['gisHelper', function (gisHelper) {
        return {
            restrict: 'E',
            scope: {
                zones: '='
            },
            replace: true,
            template: '<div id="map" class="map"></div>',
            link: function (scope) {

                function activate() {
                    createMap();
                }


                function createMap() {
                    var raster = new ol.layer.Tile({
                        source: new ol.source.OSM()
                    });

                    scope.format = new ol.format.WKT();

                    createVectorLayer();

                    scope.map = new ol.Map({
                        target: 'map',
                        layers: [raster, scope.vectorLayer],
                        view: new ol.View({
                            center: [981481.3, 5978619.7],
                            zoom: 18
                        })
                    });
                }


                function createVectorLayer() {
                    scope.readOnlyFeatures = gisHelper.getFeaturesFromZones(scope.zones, scope.format);

                    scope.vectorLayer = new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: scope.readOnlyFeatures
                        }),
                        style: styleFunction()
                    });
                }

                function styleFunction() {
                    var circlesAtEdges = new ol.style.Circle({
                        radius: 5,
                        fill: new ol.style.Fill({
                            color: 'rgba(0, 0, 255, 0.8)'
                        }),
                        stroke: null
                    });

                    return [
                        new ol.style.Style({
                            image: circlesAtEdges,
                            geometry: function (feature) {
                                if (scope.selectedZone && feature.getId() === scope.selectedZone.id) {
                                    var coordinates = feature.getGeometry().getCoordinates()[0];
                                    return new ol.geom.MultiPoint(coordinates);
                                }
                            }
                        }),
                        new ol.style.Style({
                            stroke: null,
                            fill: new ol.style.Fill({
                                color: 'rgba(0, 0, 255, 0.5)'
                            })
                        })
                    ];
                }

                activate();
            }
        };


    }]);

}());