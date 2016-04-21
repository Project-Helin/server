(function () {
    angular.module('common').service('gisHelper', function () {
        
        this.getFeaturesFromZones = function(zones, format) {
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

        this.convertZoneToFeature = function(zone, format) {
            var feature = format.readFeature(zone.polygon, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            });

            feature.setId(zone.id);

            return feature;
        };

        this.convertFeatureToWKT = function(feature, format) {

            return format.writeFeature(feature, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            });
        };

    })
}());

