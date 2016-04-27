(function () {
    angular.module('common').service('gisHelper', function () {

        this.getFeaturesFromZones = function (zones, format) {
            var _this = this;
            var readOnlyFeatures = new ol.Collection();

            zones.forEach(function (zone) {
                    if (zone.polygon !== null) {
                        readOnlyFeatures.push(_this.convertZoneToFeature(zone, format));
                    }
                }
            );

            return readOnlyFeatures;
        };

        this.getFeatureForZone = function (zone, vectorLayer) {
            return vectorLayer.getSource().getFeatureById(zone.id);
        };

        this.convertZoneToFeature = function (zone, format) {
            var feature = format.readFeature(zone.polygon, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            });

            feature.setId(zone.id);

            return feature;
        };

        this.convertFeatureToWKT = function (feature, format) {

            return format.writeFeature(feature, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            });
        };

        this.convertPositionToCoordinate = function (position) {
            return ol.proj.transform([position.lon, position.lat], 'EPSG:4326', 'EPSG:3857');
        };

        this.convertRouteToCoordinates = function (route) {
            var _this = this;
            var coordinates = route.map(function (wayPoint) {
                    return _this.convertPositionToCoordinate(wayPoint.position)
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
                        color: 'rgba(0, 192, 239, 1.0)',
                        width: 3
                    }),
                    fill: null
                });
            } else if (zoneType === 'FlightZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: 'rgba(0, 28, 247, 0.8)'
                    })
                });
            } else if (zoneType === 'DeliveryZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: 'rgba(0, 166, 90, 0.8)'
                    })
                });
            } else if (zoneType === 'LoadingZone') {
                styleForZoneType = new ol.style.Style({
                    stroke: null,
                    fill: new ol.style.Fill({
                        color: 'rgba(243, 156, 18, 0.8)'
                    })
                });
            }
            return styleForZoneType;
        };

        this.getZoneById = function (zones, zoneId) {
            return zones.filter(function (zone) {
                return zone.id === zoneId;
            })[0];
        }

    })
}());

