(function () {

    var app = angular.module('ProjectsApp', ['common', 'ui.bootstrap', 'xeditable']);

    app.run(function(editableOptions) {
        editableOptions.theme = 'bs3';
    });

}());