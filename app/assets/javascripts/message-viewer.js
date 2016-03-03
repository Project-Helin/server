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


//$ ->
//
//  webSocketToServer = new WebSocket("ws://localhost:9000/messageviewer/register");
//  webSocketToServer.onmessage  = (event) ->
//    console.log("NaNaNa", event);
//
//    time = new Date();
//    timeAsString = "Received Data at " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds()
//    messageToLog = timeAsString + " " + event.data;
//    $("#incomingMessages").prepend(messageToLog + "<br/>")
//
//  $("#myButton").click(() ->
//    console.log("Send message")
//    $.get "/sendSampleMessage"
//  )
//

