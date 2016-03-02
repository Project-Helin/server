$ ->

  webSocketToServer = new WebSocket("ws://localhost:9000/messageviewer/register");
  webSocketToServer.onmessage  = (event) ->
    console.log("NaNaNa", event);
    $("#incomingMessages").append(event.data + "<br/>")

  $("#myButton").click(() ->
    console.log("Send message")
    $.get "/sendSampleMessage"
  )
