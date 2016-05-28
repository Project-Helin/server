(function () {
    angular.module('common').service('ProductsService', ['$http', function ($http) {

        this.apiUrl = "/api/products/";

        this.findByProjects = function (projectId) {
            return $http.get(this.apiUrl + 'by-project?projectId=' + projectId)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    console.log("Failed to get produts by location", response);
                    return error;
                });
        };

    }])
}());
