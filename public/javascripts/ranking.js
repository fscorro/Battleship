var req;
var host = "http://dpoi2012api.appspot.com";
var credential = "credential=msBattleShip";

function fillTable() {
    requestRemoteData("GET", "api/1.0/list", null, loadTableData);
}

function requestRemoteData(method, url, params, callbackFunction) {
    if (window.XMLHttpRequest || window.ActiveXObject) {
        req = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
        req.onreadystatechange = function() {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var jsonData = req.responseText;
                    var jsonObj = JSON.parse(jsonData);
                    callbackFunction(jsonObj);
                } else {
                    showMessage("Can't establish connection ( Error " + req.status + " )");
                }
            }
        };
        var paramString = credential;
        if (params) {
            for (var i = 0; i < params.length; i++) {
                paramString += "&" + params[i];
            }
        }
        var url2 = host + "/" + url + "?" + credential + "&" + paramString;
        req.open(method, url2, true);
        req.send();
//        todo Put the parameters in the send line (above line)
    } else {
        showMessage("Can't identify browser");
    }
}

function loadTableData(jsonObj) {
    if (jsonObj.status.code == 1) {
        populateTable(jsonObj.payload.items);
        document.getElementsByTagName("table")[0].deleteTFoot();
    } else if (jsonObj.status.code == 3) {
        populateTable(jsonObj.payload.items);
        showMessage(jsonObj.status.msg);
    } else {
        showMessage(jsonObj.status.msg);
    }
}

function populateTable(items) {
    var tBody = document.getElementsByTagName("tbody").item(0);
    while (tBody.hasChildNodes()) {
        tBody.removeChild(tBody.firstChild);
    }
//   todo FIX THE BUG IN GAE SERVER THAT SEND 2 TIMES THE SAME DATA
    for (var i = 0; i < items.length/2; i++) {
        var row = document.createElement("TR");

        var cellLast = document.createElement("TD");
        var cellPhone = document.createElement("TD");

        var textLast = document.createTextNode(items[i].last);
        var textPhone = document.createTextNode(items[i].phone);

        cellLast.appendChild(textLast);
        cellPhone.appendChild(textPhone);

        row.appendChild(cellLast);
        row.appendChild(cellPhone);

        tBody.appendChild(row);
    }
}

function showMessage(msj) {
    document.getElementsByTagName("table")[0].deleteTFoot();

    var table = document.getElementsByTagName("table").item(0);
    var tFoot = document.createElement("TFOOT");
    var row = document.createElement("TR");
    var cell = document.createElement("TD");
    var text = document.createTextNode(msj);
    cell.appendChild(text);

    cell.setAttribute("class", "important");
    cell.setAttribute("colspan", "7");
    row.appendChild(cell);

    tFoot.appendChild(row);
    table.appendChild(tFoot);
}

function showView(id) {
    showEdit(id);
    disableInputs();
}

function doDelete(id) {
    var params = ["id=" + id];
    requestRemoteData("POST", "api/1.0/delete", params, loadResponse);
    resetViewTable();
    showMessage("Deleting...");
}

function loadResponse(jsonObj) {
    showMessage(jsonObj.status.msg);
    refreshTable();
}

function showEdit(id) {
    enableInputs();
    var params = ["id=" + id];
    requestRemoteData("GET", "api/1.0/view", params, loadRowData);
    document.getElementById("addBt").name = id;
    showMessage("Viewing...");
}

function loadRowData() {
    if (req.readyState == 4 && req.status == 200) {
        var jsonData = req.responseText;
        var jsonObj = JSON.parse(jsonData);
        if (jsonObj.status.code == 1) {
            var item = jsonObj.payload;
            document.getElementById("first").value = item.first;
            document.getElementById("last").value = item.last;
            document.getElementById("mail").value = item.mail;
            document.getElementById("phone").value = item.phone;
            document.getElementById("addBt").value = "Update";
            document.getElementById("addBt").onclick = updateRow;
            showMoreInfo(item);
        } else {
            resetViewTable();
            showViewTableMessage(jsonObj.status.msg);
        }
    }
    loadResponse(jsonObj);
}

function updateRow() {
    var first = document.getElementById("first").value;
    var last = document.getElementById("last").value;
    var mail = document.getElementById("mail").value;
    var phone = document.getElementById("phone").value;
    var id = document.getElementById("addBt").name;
    var params = ["id=" + id,"first=" + first,"last=" + last,"mail=" + mail, "phone=" + phone];
    requestRemoteData("POST", "api/1.0/update", params, loadResponse);
    resetViewTable();
    showMessage("Updating...");
}

function addRow() {
    var first = document.getElementById("first").value;
    var last = document.getElementById("last").value;
    var mail = document.getElementById("mail").value;
    var phone = document.getElementById("phone").value;
    var params = ["first=" + first,"last=" + last,"mail=" + mail, "phone=" + phone];

//    Activate the next to lines to create with a custom ID
//    var customId = "123123";
//    var params = ["first=" + first,"last=" + last,"mail=" + mail, "phone=" + phone,"id=" + customId];
    requestRemoteData("POST", "api/1.0/create", params, loadResponse);
    resetViewTable();
    showMessage("Creating...");
}

function resetViewTable() {
    enableInputs();
    clearInputValues();
    document.getElementsByTagName("table")[1].deleteTFoot();
    document.getElementById("addBt").value = "Add Row";
    document.getElementById("addBt").onclick = addRow;
}

function showMoreInfo(item) {
    document.getElementsByTagName("table")[1].deleteTFoot();

    var table = document.getElementsByTagName("table").item(1);
    var tFoot = document.createElement("TFOOT");
    var row = document.createElement("TR");

    var createdCell = document.createElement("TD");
    var itemIdCell = document.createElement("TD");

    var created = document.createTextNode("Created: " + item.created);
    var itemId = document.createTextNode("Id: " + item.id);

    createdCell.appendChild(created);
    createdCell.setAttribute("class", "important");
    createdCell.setAttribute("colspan", "3");

    itemIdCell.appendChild(itemId);
    itemIdCell.setAttribute("class", "important");
    itemIdCell.setAttribute("colspan", "1");

    row.appendChild(itemIdCell);
    row.appendChild(createdCell);

    tFoot.appendChild(row);
    table.appendChild(tFoot);
}

function clearInputValues() {
    document.getElementById("first").value = "";
    document.getElementById("last").value = "";
    document.getElementById("mail").value = "";
    document.getElementById("phone").value = "";
}

function refreshTable() {
    var oldCredential = credential;
    credential = "credential=" + document.getElementById("credentialName").value;
    showMessage("Refreshing...");
    fillTable();
    if (oldCredential != credential) {
        resetViewTable();
    }
}

function showViewTableMessage(msj) {
    document.getElementsByTagName("table")[1].deleteTFoot();

    var table = document.getElementsByTagName("table").item(1);
    var tFoot = document.createElement("TFOOT");
    var row = document.createElement("TR");
    var cell = document.createElement("TD");
    var text = document.createTextNode(msj);
    cell.appendChild(text);

    cell.setAttribute("class", "important");
    cell.setAttribute("colspan", "7");
    row.appendChild(cell);

    tFoot.appendChild(row);
    table.appendChild(tFoot);
}

function disableInputs() {
    document.getElementById("first").disabled = true;
    document.getElementById("last").disabled = true;
    document.getElementById("mail").disabled = true;
    document.getElementById("phone").disabled = true;
}

function enableInputs() {
    document.getElementById("first").disabled = false;
    document.getElementById("last").disabled = false;
    document.getElementById("mail").disabled = false;
    document.getElementById("phone").disabled = false;
}
