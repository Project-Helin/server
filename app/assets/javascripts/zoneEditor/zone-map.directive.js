(function () {
    angular.module('ZoneEditorApp').directive('zoneMap', [function () {
        return {
            restrict: 'E',
            scope: {
                zones: '='
            },
            replace: true,
            template: '<div id="map" class="map"></div>',
            link: function (scope, elem, attrs) {

                var raster = new ol.layer.Tile({
                    source: new ol.source.OSM()
                });

                var map = new ol.Map({
                    target: 'map',
                    layers: [raster],
                    view: new ol.View({
                        center: [981481.3,5978619.7],
                        zoom: 18
                    })
                });
            },
            controller: 'ZoneMapCtrl'
        };


    }]);


}());