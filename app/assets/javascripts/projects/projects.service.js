(function () {
    angular.module('ProjectsApp').service('ProjectsService', ['$http', function ($http) {

        this.projectUrl = "/projects/";

        this.loadProject = function (id) {
            return $http.get(this.projectUrl + id)
                .then(function (response) {
                    return response.data;
                }).catch(function (e) {
                    console.log("Failed while loading project", e);
                    return {};
                });
        }

    }])
}());
