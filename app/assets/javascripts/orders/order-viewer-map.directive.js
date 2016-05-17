(function () {
    angular.module('OrderViewer').directive('orderViewerMap', ['gisHelper', function (gisHelper) {
        return {
            restrict: 'E',
            scope: {
                missions: '=', 
                zones: '='
            },
            template:'<div id="map" class="map"></div>',
            link: function(scope) {
                function activate() {
                    createMap();
                    addRouteLayer(scope.missions[0].route.wayPoints);
                    addPopupOverlay();
                }

                function addPopupOverlay() {
                    scope.popup = new ol.Overlay.Popup();
                    scope.map.addOverlay(scope.popup);
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

                function addRouteLayer(calculatedRoute) {
                    var coordinates = gisHelper.convertRouteToCoordinates(calculatedRoute);
                    scope.routeLayer = createRouteLayerWithRouteLine(coordinates);
                    scope.routeMarkers = gisHelper.createRouteMarkers(calculatedRoute);
                    scope.routeLayer.getSource().addFeatures(scope.routeMarkers);
                    scope.map.addLayer(scope.routeLayer);
                }
                
                function createRouteLayerWithRouteLine(coordinates) {
                    return new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: [new ol.Feature({
                                geometry: new ol.geom.LineString(coordinates, 'XY'),
                                name: 'Line'
                            })]
                        }),
                        style: gisHelper.getRouteStyle()
                    });
                }

                function createVectorLayer() {
                    scope.readOnlyFeatures = gisHelper.getFeaturesFromZones(scope.zones, scope.format);

                    scope.vectorLayer = new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: scope.readOnlyFeatures
                        }),
                        style: polygonStyle
                    });
                }
                
                function polygonStyle(feature) {
                    return [gisHelper.getZoneStyle(gisHelper.getZoneById(scope.zones, feature.getId()))];
                }
                
                activate();
            }
        };
    }]);
})();