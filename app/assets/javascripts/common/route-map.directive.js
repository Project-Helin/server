(function () {
    angular.module('common').directive('routeMap', ['gisHelper', function (gisHelper) {
        return {
            restrict: 'E',
            scope: {
                zones: '=',
                routeData: '=',
                route: '='
            },
            replace: true,
            template: '<div id="map" class="map"></div>',
            link: function (scope) {

                function activate() {
                    createMap();
                    addMapInteractions();
                    addScopeListeners();
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

                function addMapInteractions() {
                    scope.map.on('click', function (evt) {
                        var coordinates = evt.coordinate;

                        if (!scope.droneMarker) {
                            scope.droneMarker = addDroneMarker(coordinates);
                            scope.routeData.dronePosition = gisHelper.convertFeatureToWKT(scope.droneMarker, scope.format);
                        } else if (!scope.customerMarker) {
                            scope.customerMarker = addCustomerMarker(coordinates);
                            scope.routeData.customerPosition = gisHelper.convertFeatureToWKT(scope.customerMarker, scope.format);
                        }

                    });

                    scope.map.on('click', function (evt) {
                        scope.map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
                            if (scope.route && feature.getId()) {
                                var foundWayPoint = scope.route.filter(function (wayPoint) {
                                    return wayPoint.id == feature.getId();
                                });

                                if (foundWayPoint[0]) {
                                    scope.popup.show(evt.coordinate, '<div><h2>Height</h2><p>' + foundWayPoint[0].position.height + '</p></div>');
                                }
                            }
                        });
                    });
                }

                function createRouteMarkers(route) {
                    return route.map(function (wayPoint) {
                        return addRouteMarker(gisHelper.convertPositionToCoordinate(wayPoint.position), wayPoint.id)
                    });
                }

                function addScopeListeners() {
                    scope.$watch('route', function (newRoute, oldValue) {
                        if (newRoute != null) {
                            var coordinates = gisHelper.convertRouteToCoordinates(newRoute);
                            scope.routeLayer = createRouteLayerWithRouteLine(coordinates);
                            scope.routeMarkers = createRouteMarkers(newRoute);
                            scope.routeLayer.getSource().addFeatures(scope.routeMarkers);
                            scope.map.addLayer(scope.routeLayer);
                        }
                    });

                    scope.$on('resetMap', function () {
                        scope.map.removeLayer(scope.routeLayer);
                        scope.vectorLayer.getSource().removeFeature(scope.customerMarker);
                        scope.vectorLayer.getSource().removeFeature(scope.droneMarker);
                        scope.customerMarker = null;
                        scope.droneMarker = null;
                        if (scope.popup) {
                            scope.popup.hide();
                        }

                    });
                }

                function createRouteLayerWithRouteLine(coordinates) {
                    return new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: [new ol.Feature({
                                geometry: new ol.geom.LineString(coordinates, 'XY'),
                                name: 'Line'
                            })]
                        }),
                        style: routeStyle()
                    });
                }

                function createVectorLayer() {
                    scope.readOnlyFeatures = gisHelper.getFeaturesFromZones(scope.zones, scope.format);

                    scope.vectorLayer = new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: scope.readOnlyFeatures
                        }),
                        style: polygonStyle()
                    });
                }

                function routeStyle() {
                    return [
                        new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: 'rgba(243, 156, 18, 0.8)',
                                width: 2
                            })
                        })
                    ];
                }

                function polygonStyle() {
                    return [
                        new ol.style.Style({
                            stroke: null,
                            fill: new ol.style.Fill({
                                color: 'rgba(0, 0, 255, 0.5)'
                            })
                        })
                    ];
                }

                function addDroneMarker(coordinates) {
                    return addMarker(coordinates, "assets/images/drone-icon.png");
                }

                function addCustomerMarker(coordinates) {
                    return addMarker(coordinates, "assets/images/customer-icon.png");
                }

                function addMarker(coordinates, imageUrl) {
                    var marker = new ol.Feature({
                        geometry: new ol.geom.Point(coordinates)
                    });

                    var markerStyle = new ol.style.Style({
                        image: new ol.style.Icon({
                            anchor: [0.5, 1.0],
                            anchorXUnits: "fraction",
                            anchorYUnits: "fraction",
                            src: imageUrl
                        }),
                        zIndex: 100000
                    });
                    marker.setStyle(markerStyle);
                    scope.vectorLayer.getSource().addFeature(marker);
                    return marker;
                }

                function addRouteMarker(coordinates, id) {
                    var marker = new ol.Feature({
                        geometry: new ol.geom.Point(coordinates),
                    });

                    if(id) {
                        marker.setId(id);
                    }

                    var circle = new ol.style.Circle({
                        radius: 8,
                        fill: new ol.style.Fill({
                            color: 'rgba(243, 156, 18, 0.8)'
                        }),
                        stroke: null
                    });

                    var markerStyle = new ol.style.Style({
                        image: circle,
                        zIndex: 5000
                    });

                    marker.setStyle(markerStyle);
                    return marker;
                }

                activate();
            }
        };


    }]);

}());