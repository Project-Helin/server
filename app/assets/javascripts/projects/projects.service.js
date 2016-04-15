(function () {
    angular.module('ProjectsApp').service('ProjectsService', ['$http', function ($http) {

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

        this.saveProject = function (project) {
            return $http.post(this.projectUrl + project.id + '/update', project)
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    console.log("Failed to save ...", response);
                    return error;
                });
        };

    }])
}());
