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

                scope.$watch('selectedZone', function (newValue, oldValue) {
                    updateInteractionPossibilities(newValue);
                    if (newValue) {
                        getFeatureForZone(newValue).changed();
                    }

                });

                var raster = new ol.layer.Tile({
                    source: new ol.source.OSM()
                });

                var format = new ol.format.WKT();

                scope.readOnlyFeatures = getFeaturesFromZones(scope.zones);

                var vectorLayer = new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: scope.readOnlyFeatures
                    }),
                    style: styleFunction()
                });

                scope.map = new ol.Map({
                    target: 'map',
                    layers: [raster, vectorLayer],
                    view: new ol.View({
                        center: [981481.3, 5978619.7],
                        zoom: 18
                    })
                });

                scope.drawInteraction = new ol.interaction.Draw({
                    features: scope.readOnlyFeatures,
                    type: 'Polygon'
                });

                scope.drawInteraction.on('drawend', function (event) {
                    var drawedFeature = event.feature;

                    drawedFeature.setId(scope.selectedZone.id);
                    scope.$apply(function () {
                        scope.selectedZone.polygon = convertFeatureToWKT(drawedFeature);
                    });
                });

                scope.readOnlyFeatures.on('add', function (event) {
                    updateInteractionPossibilities(scope.selectedZone);
                });

                function styleFunction() {
                    var image = new ol.style.Circle({
                        radius: 5,
                        fill: null,
                        stroke: new ol.style.Stroke({color: 'orange', width: 2})
                    });
                    return [
                        new ol.style.Style({
                            image: image,
                            geometry: function (feature) {
                                if (scope.selectedZone && feature.getId() === scope.selectedZone.id) {
                                    var coordinates = feature.getGeometry().getCoordinates()[0];
                                    return new ol.geom.MultiPoint(coordinates);
                                }
                            }
                        }),
                        new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: 'blue',
                                width: 3
                            }),
                            fill: new ol.style.Fill({
                                color: 'rgba(0, 0, 255, 0.1)'
                            })
                        })
                    ];
                }

                function updateInteractionPossibilities(currentZone) {
                    removeCurrentInteractions();

                    if (currentZone) {
                        if (currentZone.polygon === null) {
                            scope.map.addInteraction(scope.drawInteraction);
                        } else {
                            scope.modifyInteraction = getModifyInteraction(currentZone);
                            scope.map.addInteraction(scope.modifyInteraction);
                        }
                    }
                }

                function getModifyInteraction(zone) {
                    scope.modifyFeatures = new ol.Collection();
                    scope.modifyFeatures.push(getFeatureForZone(zone));

                    var modifyInteraction = new ol.interaction.Modify({
                        features: scope.modifyFeatures,
                        // the SHIFT key must be pressed to delete vertices, so
                        // that new vertices can be drawn at the same position
                        // of existing vertices
                        deleteCondition: function (event) {
                            return ol.events.condition.shiftKeyOnly(event) &&
                                ol.events.condition.singleClick(event);
                        }
                    });

                    modifyInteraction.on('modifyend', function (event) {
                        var modifiedFeature = event.features.getArray()[0];

                        var zone = scope.zones.filter(function (zone) {
                            return zone.id === modifiedFeature.getId();
                        })[0];

                        scope.$apply(function () {
                                zone.polygon = convertFeatureToWKT(modifiedFeature);
                            }
                        );

                    });

                    return modifyInteraction;
                }

                function removeCurrentInteractions() {
                    if (scope.modifyInteraction) {
                        scope.map.removeInteraction(scope.modifyInteraction);
                        delete scope.modifyInteraction;
                    }

                    scope.map.removeInteraction(scope.drawInteraction);
                }


                function getFeaturesFromZones(zones) {
                    var readOnlyFeatures = new ol.Collection();

                    zones.forEach(function (zone) {
                            if (zone.polygon !== null) {
                                readOnlyFeatures.push(convertZoneToFeature(zone));
                            }
                        }
                    );

                    return readOnlyFeatures;
                }

                function getFeatureForZone(zone) {
                    return vectorLayer.getSource().getFeatureById(zone.id);
                }

                function convertZoneToFeature(zone) {
                    var feature = format.readFeature(zone.polygon, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:3857'
                    });

                    feature.setId(zone.id);

                    return feature;
                }

                function convertFeatureToWKT(feature) {

                    return format.writeFeature(feature, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:3857'
                    });
                }


            },
            controller: 'ZoneMapCtrl'
        };


    }]);


}());