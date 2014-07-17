/**
 * User: Mart0
 * Date: 5/17/12
 */

function initBattleShipGame() {
    setOnMessageCallback(battleEvent);
}

function initBattleBoards() {
    $("#autoBt").hide();
    $("#sendStrategyBt").hide();
    $("#randomStrategyBt").hide();
    addGrid(document.getElementById("myBoard"), "water", "my");
    addGrid(document.getElementById("opBoard"), "water", "op");
    addTableBoard(document.getElementById("strategyBoard"), "my");
    $("#myBoard").show();
    $("#opBoard").show();
    $("#hideChatBt").show();
    hideDevelopBts();
}

function initChat() {
    $("#talk").keypress(handleReturnKey);
}

function hideDevelopBts() {
    $("#fireBt").hide();
    $("#xArea").hide();
    $("#yArea").hide();
    $("#playersBt").hide();
    $("#infoBt").hide();
}

function battleEvent(event) {
    var data = JSON.parse(event.data);
    console.log("Event Received: " + data.type);
    // Create the message element

//  var chatLine = $("<div class='message'><span></span><user></user><p></p></div>");
    var chatLine = $("<div class='message'><user></user><p></p></div>");

    if (data.type == "chat") {
        $(chatLine).addClass("chat");
        $("user", chatLine).text(data.name + ":");
    }
    if (data.type == "start") {
        $(chatLine).addClass("start");
    }

    if (data.type == "askStrategy") {
        showStrategyInterface();
    }

    if (data.type == "strategyReceived") {
        $("#randomStrategyBt").hide();
        $("#sendStrategyBt").hide();
    }

    if (data.type == "strategy") {
        $("#strategyBoard").hide();
        $("#shipsBoard").hide();
        var strategy = JSON.parse(data.strategy);
        setStrategy(strategy);
        $("#autoBt").show();
        $("#myBoard").show();
        $("#opBoard").show();
    }
    if (data.type == "my-shot") {
        $(chatLine).addClass("myShot");
        var id = "op" + data.point;
        var shootResult = data.result + "";
        setResult(id, shootResult);
    }

    if (data.type == "op-shot") {
        $(chatLine).addClass("opShot");
        var id = "my" + data.point;
        var shootResult = data.result + "";
        setResult(id, shootResult);
    }

    if (data.type == "end") {
        $(chatLine).addClass("end");
        $("#autoBt").hide();
    }

    if (data.type == "mistake") $(chatLine).addClass("mistake");

    if (data.type == "leave") $(chatLine).addClass("leave");

    if (data.type == "autoPlay") {
        $(chatLine).addClass("autoPlay");
        if (data.message == "AutoPlay is Enable") {
            $("#autoBt").text("Disable Auto Play");
        } else $("#autoBt").text("Auto Play");
    }

    if (data.type == "info") $(chatLine).addClass("info");

//    $("span", chatLine).text(data.type);
    $("p", chatLine).text(data.message);
    addChatLine(chatLine);
}

function showStrategyInterface() {
    $("#myBoard").hide();
    $("#opBoard").hide();
    $("#sendStrategyBt").show();
    $("#randomStrategyBt").show();
    $("#strategyBoard").show();
    $("#shipsBoard").show();
}

function sendMessage(type) {
    var json = JSON.stringify(
        {
            type:type,
            text:$("#talk").val(),
            x:$("#xArea").val(),
            y:$("#yArea").val()
        }
    );
    sentToServer(json);
    $("#talk").val("");
}

function addChatLine(chatLine) {
    $('#messages').append(chatLine);
}

function handleReturnKey(e) {
    if (e.charCode == 13 || e.keyCode == 13) {
        e.preventDefault();
        sendMessage("chat");
    }
}

function getServerInfo() {
    sendMessage("serverInfo");
}

function fire() {
    sendMessage("shot");
    $("#xArea").val("");
    $("#yArea").val("");
}

function playersInfo() {
    $("#players").toggle();
}

function autoPlay() {
    sendMessage("autoPlay");
}

function hideChat(){
    $("#onChat").toggle();
}


function addTableBoard(div, cellsIdPrefix) {
    var letters = "ABCDEFGHIJ".split("");
    for (var j = 0; j < letters.length + 1; j++) {
        for (var i = 0; i < letters.length + 1; i++) {
            var b = document.createElement("div");
            b.setAttribute("class", "boardDiv");
            if (j == 0 && i == 0) {
                b.innerHTML = "&nbsp;";
            } else if (j == 0 && i >= 1) {
                var aux = document.createElement("div");
                aux.innerHTML = letters[i - 1];
                aux.setAttribute("class", "boardAux");
                b.appendChild(aux);
            } else if (i == 0) {
                var aux = document.createElement("div");
                aux.innerHTML = j;
                aux.setAttribute("class", "boardAux");
                b.appendChild(aux);
            } else {
                b.id = cellsIdPrefix + letters[i - 1] + j + "";
                var pointId = letters[i - 1] + "-" + j + "";
                b.setAttribute("class", "boardDivWater");
                if (cellsIdPrefix == "op") {
                    b.setAttribute("onclick", "cellEvent('" + pointId + "')");
                }
            }
            div.appendChild(b);
        }
    }
}

function addGrid(div, state, cellsIdPrefix) {
    var letters = "ABCDEFGHIJ".split("");
    for (var j = 0; j < letters.length + 1; j++) {
        for (var i = 0; i < letters.length + 1; i++) {
            // create a new button
            var b = document.createElement("BUTTON");
            b.type = "button";
            b.setAttribute("class", "anno");

            if (j == 0 && i == 0) {
                // add blank button at origin
                b.innerHTML = "&nbsp;";
            }
            else if (j == 0 && i >= 1) {
                // add horizontal A-J
                b.innerHTML = letters[i - 1];
            }
            else if (i == 0) {
                // add vertical 1-10
                b.innerHTML = j;
            }
            else {
                // create and fill
                b.innerHTML = "&nbsp;";
                b.id = cellsIdPrefix + letters[i - 1] + j + "";
                b.setAttribute("class", state);
                var pointId = letters[i - 1] + "-" + j + "";
                if (cellsIdPrefix == "op") {
                    b.setAttribute("onclick", "cellEvent('" + pointId + "')");
                }
            }
            div.appendChild(b);
        }
        div.appendChild(document.createElement("BR"));
    }
}

function cellEvent(pointId) {
    var point = pointId.split("-");
    $("#xArea").val(point[0]);
    $("#yArea").val(point[1]);
    fire();
}

function setStrategy(strategy) {
    for (var i = 0; i < strategy.length; i++) {
        var ship = strategy[i];
        for (var j = 0; j < ship.body.length; j++) {
            var point = ship.body[j];
            var pointId = "my" + point.x + point.y;
            changeCellState(pointId, "ship");
        }
    }
}

function setResult(id, shootResult) {
    changeCellState(id, shootResult.toLowerCase());
}

function changeCellState(id, newState) {
    var cell = document.getElementById(id);
    cell.setAttribute("class", newState);
}
