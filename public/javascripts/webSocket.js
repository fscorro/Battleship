/**
 * User: Mart0
 * Date: 4/30/12
 */
var socket;

function initWebSocket(webSocketUrl) {
    if (WebSocket || window["MozWebSocket"]) {
        var WS = WebSocket ? WebSocket : window["MozWebSocket"];
        socket = new WS(webSocketUrl);
        socket.onopen = null;
        socket.onclose = null;
        socket.onmessage = null;
        socket.onerror = null;
        initApp();
    } else {
        alert("Your browser doesn't support WebSocket");
    }
}

function initApp() {
    initBattleShipGame();
    initBattleBoards();
    initChat();
}

function sentToServer(json) {
    socket.send(json);
    console.log("Send to server:" + json);
}

function setOnMessageCallback(fcallback) {
    socket.onmessage = fcallback;
}

function setOnOpenCallback(fcallback) {
    socket.onopen = fcallback;
}

function setOnCloseCallback(fcallback) {
    socket.onclose = fcallback;
}

function setOnErrorCallback(fcallback) {
    socket.onerror = fcallback;
}