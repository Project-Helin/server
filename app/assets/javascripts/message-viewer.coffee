$ ->

  webSocketToServer = new WebSocket("ws://localhost:9000/messageviewer/register");
  webSocketToServer.onmessage  = (event) ->
    console.log("NaNaNa", event);

    time = new Date();
    timeAsString = "Received Data at " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds()
    messageToLog = timeAsString + " " + event.data;
    $("#incomingMessages").prepend(messageToLog + "<br/>")

  $("#myButton").click(() ->
    console.log("Send message")
    $.get "/sendSampleMessage"
  )
