// main js script
var socket;

$(document).ready(function() {
  var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
  socket = new WS(wsUrl());
  socket.onmessage = onMessage;
  
  $(document).keypress(onKeyPress);
  window.setInterval(function() { $('#prompt').focus(); }, 500);
});

function onKeyPress(event) {
  // ENTER 
  if (event.which == 13) {
    var prompt = $('#prompt');
    var command = prompt.val();
    var prev = $('<div class="prev"></div>')
    prev.text("> " + command)
    $('#result').append(prev);
    socket.send(command);
    prompt.val('');
  }
  // TODO UP
  // TODO DOWN
}

function onMessage(event) {
  var lines = event.data.split(/\n/);
  for(i in lines) {
    addLineToResult(lines[i]);
  }
  $('#repl').scrollTop($('#repl').height())
}

function addLineToResult(str) {
  var line = $('<div/>');
  line.text(str);
  $('#result').append(line);
}
