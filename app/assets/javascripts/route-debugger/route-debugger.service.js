
(function () {
    angular.module('RouteDebugger').service('RouteDebuggerService', ['$http', function ($http) {

        this.projectUrl = "/api/projects/";

        this.loadProject = function (id) {
            return $http.get(this.projectUrl + id)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    //Do nothing because it could be a new Project
                    return error;
                });
        };
    }])
}());
