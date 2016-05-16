(function () {
    angular.module('OrderViewer').directive('orderViewer', function () {
        return {
            restrict: 'E',
            scope: {
                orderId: '@'
            },
            templateUrl: '/assets/javascripts/orders/order-viewer.tmpl.html',
            controller: 'OrderViewerCtrl'
        };
    });
})();