(function () {
    angular.module('common').service('CustomersService', ['$http', function ($http) {

        this.apiUrl = "/api/customers/";

        this.save = function (email, givenName, familyName) {

            var customer = {
                "email": email,
                "given_name" :  givenName,
                "family_name" : familyName
            };

            return $http.post(this.apiUrl + 'save', customer)
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
