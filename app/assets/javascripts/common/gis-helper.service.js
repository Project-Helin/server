(function () {
    angular.module('common').service('gisHelper', function () {

        this.zoneColors = {
            "Order Zone": 'rgba(0, 192, 239, 1.0)',
            "Flight Zone": 'rgba(0, 28, 247, 0.8)',
            "Delivery Zone": 'rgba(0, 166, 90, 0.8)',
            "Loading Zone": 'rgba(243, 156, 18, 0.8)'
        };

        this.flownRouteColor = '#e50be8';
        this.calculatedRouteColor = 'rgba(33, 231, 6, 1)';
        this.dataProjectionCode = 'EPSG:4326';
        this.mapProjectionCode = 'EPSG:3857';

        this.getFeaturesFromZones = function (zones, format) {
            var _this = this;
            var features = new ol.Collection();

            zones.forEach(function (zone) {
                    if (zone.polygon !== null) {
                        features.push(_this.convertZoneToFeature(zone, format));
                    }
                }
            );

            return features;
        };

        this.getFeatureForZone = function (zone, vectorLayer) {
            return vectorLayer.getSource().getFeatureById(zone.id);
        };

        this.convertZoneToFeature = function (zone, format) {
            var feature = format.readFeature(zone.polygon, {
                dataProjection: this.dataProjectionCode,
                featureProjection: this.mapProjectionCode
            });

            feature.setId(zone.id);

            return feature;
        };

        this.convertFeatureToWKT = function (feature, format) {

            return format.writeFeature(feature, {
                dataProjection: this.dataProjectionCode,
                featureProjection: this.mapProjectionCode
            });
        };

        this.convertPositionToCoordinate = function (position) {
            return ol.proj.transform([position.lon, position.lat], this.dataProjectionCode, this.mapProjectionCode);
        };

        this.convertRouteToCoordinates = function (route) {
            var _this = this;
            var coordinates = route.map(function (wayPoint) {
                    return _this.convertPositionToCoordinate(wayPoint.position)
                }
            );
            return coordinates;
        };

        this.convertDroneInfoToCoordinate = function (droneInfo) {
            var _this = this;
            var gpsState = droneInfo.gpsState;
            return _this.convertPositionToCoordinate({lon: gpsState.posLon, lat: gpsState.posLat});
        };

        this.convertDroneInfosToCoordinates = function (droneInfos) {
            var _this = this;
            var coordinates = droneInfos.map(function (droneInfo) {
                    return _this.convertDroneInfoToCoordinate(droneInfo);
                }
            );
            return coordinates;
        };


        this.getZoneStyle = function (zone) {
            var styleForZoneType;

            var zoneType = zone.type;

            if (zoneType === 'OrderZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: this.zoneColors["Order Zone"],
                        width: 3
                    }),
                    fill: null
                });
            } else if (zoneType === 'FlightZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: this.zoneColors["Flight Zone"]
                    })
                });
            } else if (zoneType === 'DeliveryZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: this.zoneColors["Delivery Zone"]
                    })
                });
            } else if (zoneType === 'LoadingZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: this.zoneColors["Loading Zone"]
                    })
                });
            }
            return styleForZoneType;
        };

        this.getRouteStyle = function () {
            var _this = this;
            return new ol.style.Style({
                stroke: new ol.style.Stroke({
                    color: _this.calculatedRouteColor,
                    width: 2
                })
            });
        };

        this.createRouteMarkers = function (route) {
            var _this = this;
            return route.map(function (wayPoint) {
                return createRouteMarker(_this.convertPositionToCoordinate(wayPoint.position), wayPoint.id)
            });
        };


        this.createDroneInfoMarkers = function (droneInfos) {
            var _this = this;
            return droneInfos.map(function (droneInfo) {
                var gpsState = droneInfo.gpsState;
                return _this.createDroneInfoMarker(_this.convertPositionToCoordinate({
                    lon: gpsState.posLon,
                    lat: gpsState.posLat
                }), droneInfo.id)
            });
        };

        function createRouteMarker(coordinates, id) {
            var _this = this;
            var marker = new ol.Feature({
                geometry: new ol.geom.Point(coordinates)
            });

            if (id) {
                marker.setId(id);
            }

            var circle = new ol.style.Circle({
                radius: 7,
                fill: new ol.style.Fill({
                    color: 'rgba(33, 231, 6, 1)'
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

        this.createDroneInfoMarker = function (coordinates, id) {
            var _this = this;
            var marker = new ol.Feature({
                geometry: new ol.geom.Point(coordinates)
            });

            if (id) {
                marker.setId(id);
            }

            var circle = new ol.style.Circle({
                radius: 6,
                fill: new ol.style.Fill({
                    color: _this.flownRouteColor
                }),
                stroke: null
            });

            var markerStyle = new ol.style.Style({
                image: circle,
                zIndex: 5000
            });

            marker.setStyle(markerStyle);
            return marker;
        };

        this.getZoneById = function (zones, zoneId) {
            return zones.filter(function (zone) {
                return zone.id === zoneId;
            })[0];
        };


        this.panTo = function (map, coordinates) {
            var pan = ol.animation.pan({
                source: map.getView().getCenter()
            });
            map.beforeRender(pan);
            map.getView().setCenter(coordinates);
        };

        this.isLayerOnMap = function (map, layer) {
            var isOnMap = map.getLayers().getArray().filter(function (existingLayer) {
                return existingLayer === layer;
            })[0];

            return isOnMap;
        };


        this.getBaseAndSatelliteLayer = function () {
            return new ol.layer.Group({
                'title': 'Base maps',
                layers: [
                    new ol.layer.Tile({
                        title: 'OSM',
                        type: 'base',
                        visible: true,
                        source: new ol.source.OSM()
                    }),
                    new ol.layer.Tile({
                        title: 'Bing Sattelite',
                        type: 'base',
                        visible: true,
                        preload: Infinity,
                        source: new ol.source.BingMaps({
                            key: 'Asp51bKxq0601cQL6hIYa-figI3wTNxsTX-kw8wzVZD16nwy2mNGmCA1xXlliZxx',
                            imagerySet: 'Aerial',
                            // use maxZoom 19 to see stretched tiles instead of the BingMaps
                            // "no photos at this zoom level" tiles
                            maxZoom: 19
                        })
                    })
                ]
            });
        };


    })
}());

