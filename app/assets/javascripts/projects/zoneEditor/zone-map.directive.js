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
                    removeCurrentInteractions();

                    if (newValue && newValue !== oldValue) {
                        if (newValue.polygon === null) {
                            scope.map.addInteraction(scope.drawInteraction);
                        } else {
                            scope.modifyInteraction = getModifyInteraction(newValue);
                            scope.map.addInteraction(scope.modifyInteraction);
                        }
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
                    })
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

                function getModifyInteraction(zone) {
                    var modifyFeatures = new ol.Collection();
                    modifyFeatures.push(removeFeatureFromReadonlyCollection(zone));

                    return new ol.interaction.Modify({
                        features: modifyFeatures,
                        // the SHIFT key must be pressed to delete vertices, so
                        // that new vertices can be drawn at the same position
                        // of existing vertices
                        deleteCondition: function (event) {
                            return ol.events.condition.shiftKeyOnly(event) &&
                                ol.events.condition.singleClick(event);
                        }
                    });
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
                                readOnlyFeatures.push(getFeatureFromZone(zone));
                            }
                        }
                    );

                    return readOnlyFeatures;
                }

                function removeFeatureFromReadonlyCollection (zone) {
                    if (scope.readOnlyFeatures) {
                        return vectorLayer.getSource().getFeatureById(zone.id);
                    }
                }

                function getFeatureFromZone(zone) {

                    var feature = format.readFeature(zone.polygon, {
                        dataProjection: 'EPSG:4326',
                        featureProjection: 'EPSG:3857'
                    });

                    feature.setId(zone.id);

                    return feature;

                }

            },
            controller: 'ZoneMapCtrl'
        };


    }]);


}());