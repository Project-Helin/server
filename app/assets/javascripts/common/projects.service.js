(function () {
    angular.module('common').service('ProjectsService', ['$http', function ($http) {

        this.orderUrl = "/api/projects/";

        this.loadProjects = function () {
            return $http.get(this.orderUrl)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    return error;
                });
        };
        
        this.loadProject = function (id) {
            return $http.get(this.orderUrl + id)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    //Do nothing because it could be a new Project
                    return error;
                });
        };

        this.saveProject = function (project) {
            return $http.post(this.orderUrl + project.id + '/update', project)
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    console.log("Failed to save ...", response);
                    return error;
                });
        };

        this.calculateRouteForProject = function (id, dronePosition, customerPosition) {
            var params = {
                dronePosition: dronePosition,
                customerPosition: customerPosition
            };

            return $http.get(this.orderUrl + id + "/calculate-route", {params: params})
                .then(function (response) {
                    return response.data;
                });
        };
    }])
}());
