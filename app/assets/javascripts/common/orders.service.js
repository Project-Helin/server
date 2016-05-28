(function () {
    angular.module('common').service('OrdersService', ['$http', function ($http) {

        this.orderUrl = "/api/orders/";

        this.loadOrders = function () {
            return $http.get(this.orderUrl)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    return error;
                });
        };

        this.loadOrder = function (id) {
            return $http.get(this.orderUrl + id)
                .then(function (response) {
                    return response.data;
                }).catch(function (error) {
                    //Do nothing because it could be a new Project
                    return error;
                });
        };

        this.create = function (orderCargoDto) {
            return $http.post(this.orderUrl, orderCargoDto);
        };

        this.confirmOrder = function (orderId, customerId) {
            return $http.post(this.orderUrl + orderId + "/confirm/" + customerId, {});
        };

    }])
}());

