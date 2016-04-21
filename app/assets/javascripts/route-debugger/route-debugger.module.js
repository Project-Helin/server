(function () {

    var app = angular.module('RouteDebugger', ['common', 'ui.bootstrap', 'xeditable']);

    app.run(function(editableOptions) {
        editableOptions.theme = 'bs3';
    });

}());