angular.module('debugApp', []).controller('MainController', ['$scope', '$http', function($scope, $http) {
    $scope.missionMessage = {};
    $scope.responses = [];
    var url = '/bla';

    $scope.triggerMissionMessage = function () {
        $http.post(url, $scope.missionMessage).then(function(response) {
            $scope.responses.push(response.data);
        }).catch(function (e) {
            console.log("Problem with Server Call", e);
        });
    };

}]);


window.onload = function() {


    var url = "ws://localhost:9000/messageviewer/register";
    var webSocketToServer = new WebSocket(url);

    console.log("Connect to " + url);

    webSocketToServer.onmessage = function(event) {
        var messageToLog, time, timeAsString;
        console.log("Got event", event);

        time = new Date();
        timeAsString = "Received Data at " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
        messageToLog = timeAsString + " " + event.data;
        return $("#incomingMessages").prepend(messageToLog + "<br/>");
    };
    return $("#myButton").click(function() {
        console.log("Send message");
        return $.get("/sendSampleMessage");
    });
};

