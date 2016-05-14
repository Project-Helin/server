(function () {
    angular.module('RouteDebugger').directive('routeDebugger', function () {
        return {
            restrict: 'E',
            scope: {
                projectId: '@',
                missionId: '@'
            },
            templateUrl: '/assets/javascripts/route-debugger/route-debugger.tmpl.html',
            controller: 'RouteDebuggerCtrl'
        };
    });
})();