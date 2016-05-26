(function () {
    angular.module('ProjectsApp').directive('zoneMap', ['gisHelper', function (gisHelper) {
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
                    scope.map.getView().fit(scope.vectorLayer.getSource().getExtent(), scope.map.getSize());
                }

                function getDrawInteraction() {
                    var drawInteraction = new ol.interaction.Draw({
                        features: scope.readOnlyFeatures,
                        type: 'Polygon'
                    });


                    drawInteraction.on('drawstart', function (event) {
                        scope.inDrawMode = true;
                    });

                    drawInteraction.on('drawend', function (event) {
                        writePolygonValueToZone(event);
                        scope.inDrawMode = false;
                    });

                    return drawInteraction;
                }

                function addScopeListeners() {
                    scope.$watch('selectedZone', function (newValue, oldValue) {
                        if(!scope.inDrawMode) {
                            updateInteractionPossibilities(newValue);
                            updateStyle(newValue, oldValue);
                        }
                    }, true);

                    scope.$watch('zones', function (newValue, oldValue) {
                        if (zoneWasDeleted(newValue, oldValue)) {
                            removeDeletedZoneFromMap(oldValue, newValue);
                        }
                    });
                }

                function selectZoneIfnotInDrawMode(event) {
                    if (!scope.inDrawMode) {
                        scope.map.forEachFeatureAtPixel(event.pixel, function (feature, layer) {
                            if (feature.getId()) {
                                var zone = gisHelper.getZoneById(scope.zones, feature.getId());
                                if (zone.type != 'OrderZone') {
                                    scope.$apply(function () {
                                        scope.selectedZone = zone;
                                    })
                                }
                            }
                        });
                    }
                }

                function addMapListeners() {
                    scope.readOnlyFeatures.on('add', function (event) {
                        updateInteractionPossibilities(scope.selectedZone);
                    });

                    scope.map.on("click", function (e) {
                        selectZoneIfnotInDrawMode(e);
                    });
                }

                function createVectorLayer() {
                    scope.readOnlyFeatures = gisHelper.getFeaturesFromZones(scope.zones, scope.format);

                    scope.vectorLayer = new ol.layer.Vector({
                        source: new ol.source.Vector({
                            features: scope.readOnlyFeatures
                        }),
                        style: styleFunction
                    });
                }


                function styleFunction(feature) {
                    var zone = gisHelper.getZoneById(scope.zones, feature.getId());
                    return [getEditInteractionStyle(), gisHelper.getZoneStyle(zone)];
                }

                function getEditInteractionStyle() {
                    var circlesAtEdges = new ol.style.Circle({
                        radius: 5,
                        fill: new ol.style.Fill({
                            color: 'rgba(0, 0, 255, 0.8)'
                        }),
                        stroke: null
                    });

                    return new ol.style.Style({
                        image: circlesAtEdges,
                        geometry: function (feature) {
                            if (scope.selectedZone && feature.getId() === scope.selectedZone.id) {
                                var coordinates = feature.getGeometry().getCoordinates()[0];
                                return new ol.geom.MultiPoint(coordinates);
                            }
                        }
                    });
                }

                function updateInteractionPossibilities(currentZone) {
                    removeCurrentInteractions();

                    if (currentZone) {
                        if (currentZone.polygon === null) {
                            scope.drawInteraction = getDrawInteraction();
                            scope.map.addInteraction(scope.drawInteraction);
                        } else {
                            scope.modifyInteraction = getModifyInteraction(currentZone);
                            scope.map.addInteraction(scope.modifyInteraction);
                        }
                    }
                }

                function removeCurrentInteractions() {
                    if (scope.modifyInteraction) {
                        scope.map.removeInteraction(scope.modifyInteraction);
                        delete scope.modifyInteraction;
                    }
                    if (scope.drawInteraction) {
                        scope.map.removeInteraction(scope.drawInteraction);
                        delete  scope.drawInteraction;
                    }
                }


                function updateStyle(newZone, oldZone) {
                    if (newZone && newZone.polygon) {
                        gisHelper.getFeatureForZone(newZone, scope.vectorLayer).changed();
                    }

                    if (oldZone && oldZone.polygon) {
                        if (gisHelper.getFeatureForZone(oldZone, scope.vectorLayer)) {
                            gisHelper.getFeatureForZone(oldZone, scope.vectorLayer).changed();
                        }
                    }
                }


                function getModifyInteraction(zone) {
                    var modifyFeatures = new ol.Collection();
                    modifyFeatures.push(gisHelper.getFeatureForZone(zone, scope.vectorLayer));

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
                                zone.polygon = gisHelper.convertFeatureToWKT(modifiedFeature, scope.format);
                            }
                        );

                    });

                    return modifyInteraction;
                }

                function removeDeletedZoneFromMap(oldValue, newValue) {
                    var deletedZone = oldValue.filter(function (i) {
                        return newValue.indexOf(i) < 0;
                    })[0];
                    if (deletedZone.polygon) {
                        var featureToDelete = gisHelper.getFeatureForZone(deletedZone, scope.vectorLayer);
                        scope.vectorLayer.getSource().removeFeature(featureToDelete);
                    }
                }

                function zoneWasDeleted(newValue, oldValue) {
                    return newValue.length < oldValue.length;
                }

                function writePolygonValueToZone(event) {
                    var drawedFeature = event.feature;

                    drawedFeature.setId(scope.selectedZone.id);
                    scope.$apply(function () {
                        scope.selectedZone.polygon = gisHelper.convertFeatureToWKT(drawedFeature, scope.format);
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