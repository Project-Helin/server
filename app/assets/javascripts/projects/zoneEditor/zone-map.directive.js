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

                function activate() {
                    createMap();
                    createDrawInteraction();
                    addScopeListeners();
                    addMapListeners();
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

                function createDrawInteraction() {
                    scope.drawInteraction = new ol.interaction.Draw({
                        features: scope.readOnlyFeatures,
                        type: 'Polygon'
                    });
                }

                function addScopeListeners() {
                    scope.$watch('selectedZone', function (newValue, oldValue) {
                        updateInteractionPossibilities(newValue);
                        updateStyle(newValue, oldValue);
                    });

                    scope.$watch('zones', function (newValue, oldValue) {
                        if (zoneWasDeleted(newValue, oldValue)) {
                            removeDeletedZoneFromMap(oldValue, newValue);
                        }
                    });
                }

                function selectZoneIfnotInDrawMode(event) {
                    if(!scope.inDrawMode) {
                        scope.map.forEachFeatureAtPixel(event.pixel, function (feature, layer) {
                            if (feature.getId()) {
                                scope.$apply(function () {
                                    scope.selectedZone = getZone(feature.getId());
                                })
                            }
                        });
                    }
                }

                function addMapListeners() {
                    scope.drawInteraction.on('drawstart', function (event) {
                        scope.inDrawMode = true;
                    });
                    scope.drawInteraction.on('drawend', function (event) {
                        scope.inDrawMode = false;
                        writePolygonValueToZone(event);
                    });

                    scope.readOnlyFeatures.on('add', function (event) {
                        updateInteractionPossibilities(scope.selectedZone);
                    });

                    scope.map.on("click", function(e) {
                        selectZoneIfnotInDrawMode(e);
                    });
                }

                function createVectorLayer() {
                    scope.readOnlyFeatures = getFeaturesFromZones(scope.zones);

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

                function updateStyle(newZone, oldZone) {
                    if (newZone && newZone.polygon) {
                        getFeatureForZone(newZone).changed();
                    }

                    if (oldZone && oldZone.polygon) {
                        if (getFeatureForZone(oldZone)) {
                            getFeatureForZone(oldZone).changed();
                        }
                    }
                }


                function getModifyInteraction(zone) {
                    var modifyFeatures = new ol.Collection();
                    modifyFeatures.push(getFeatureForZone(zone));

                    var modifyInteraction = new ol.interaction.Modify({
                        features: modifyFeatures,
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
                    return scope.vectorLayer.getSource().getFeatureById(zone.id);
                }

                function convertZoneToFeature(zone) {
                    var feature = scope.format.readFeature(zone.polygon, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:3857'
                    });

                    feature.setId(zone.id);

                    return feature;
                }

                function convertFeatureToWKT(feature) {

                    return scope.format.writeFeature(feature, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:3857'
                    });
                }

                function removeDeletedZoneFromMap(oldValue, newValue) {
                    var deletedZone = oldValue.filter(function (i) {
                        return newValue.indexOf(i) < 0;
                    })[0];
                    var featureToDelete = getFeatureForZone(deletedZone);
                    scope.vectorLayer.getSource().removeFeature(featureToDelete);
                }

                function getZone(id) {
                    return scope.zones.filter(function(zone) {
                        return zone.id === id;
                    })[0];
                }

                function zoneWasDeleted(newValue, oldValue) {
                    return newValue.length < oldValue.length;
                }

                function writePolygonValueToZone(event) {
                    var drawedFeature = event.feature;

                    drawedFeature.setId(scope.selectedZone.id);
                    scope.$apply(function () {
                        scope.selectedZone.polygon = convertFeatureToWKT(drawedFeature);
                    });
                }

                activate();
            },
            controller: 'ZoneMapCtrl'
        };


    }
    ])
    ;

}());