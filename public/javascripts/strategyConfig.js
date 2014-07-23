var msg1 = $.i18n.prop('will.be.collision');
var msg2 = $.i18n.prop('ships.cant.touch');
var msg3 = $.i18n.prop('ship.out.board');

var width = 32;
var aircraft = [];
var battleship = [];
var cruiser = [];
var submarine = [];
var destroyer = [];

var aircraftPosition = 0;
var battleshipPosition = 0;
var cruiserPosition = 0;
var submarinePosition = 0;
var destroyerPosition = 0;

var quantity = 0;

function setShipCoordinate(coordinateX, coordinateY, shipPosition, quantity){
	if(shipPosition != 0){
		return [{'x':coordinateX, 'y':coordinateY}, {'x':coordinateX, 'y':coordinateY + quantity}];
	}else{
		return [{'x':coordinateX, 'y':coordinateY}, {'x':coordinateX + quantity, 'y':coordinateY}];
	}
}

function setDobleClickFunction(ship, shipPosition, quantity, obj){
	if(ship.point != undefined) {
		var test = 0;
	    if (shipPosition != 0) {
	    	test = testCollision({'x':ship.point[0].x, 'y':ship.point[0].y}, {'x':ship.point[0].x + quantity, 'y':ship.point[0].y}, obj.id);
	    } else {
	        test = testCollision({'x':ship.point[0].x, 'y':ship.point[0].y}, {'x':ship.point[0].x, 'y':ship.point[0].y + quantity}, obj.id);
	    }
	    if (test) {
	    	alert($.i18n.prop('will.be.collision'));
	    } else {
			if (shipPosition != 0) {
				shipPosition = 0;
	        } else {
				shipPosition = 1;
	        }
			ship.point = setShipCoordinate(ship.point[0].x, ship.point[0].y, shipPosition, quantity);
	        rotate($(obj));
			return {'ship':ship, 'shipPosition':shipPosition};
	    }
	}else {
		rotate($(obj));
		if (shipPosition != 0) {
			return {'shipPosition':0};
		} else {
			return {'shipPosition':1};
		}
	}
}

$("#strategyBoard").droppable({
    accept:'#aircraft,#battleship,#cruiser,#submarine,#destroyer',
    drop:function (event, ui) {
        var ship = ui.draggable[0].id;
        var x = ui.offset.left - $(this).offset().left;
        var y = ui.offset.top - $(this).offset().top;
        var coordinateX = Math.floor(x / width) + 1;
        var coordinateY = Math.floor(y / width) + 1;

        var pointC = {'x':coordinateX, 'y':coordinateY}, pointD = [];
        if (ui.draggable[0].clientWidth > width) {
            pointD = {'x':coordinateX + quantity, 'y':coordinateY};
        } else {
            pointD = {'x':coordinateX, 'y':coordinateY + quantity};
        }

        //test collision
        switch (testCollision(pointC, pointD, ship)) {
			case 1:
				alert($.i18n.prop('will.be.collision'));
				animationBack(ship, ui.draggable);
	            return false;
			case 2:
				alert($.i18n.prop('ships.cant.touch'));
				animationBack(ship, ui.draggable);
	            return false;
			case 3:
				alert($.i18n.prop('ship.out.board'));
				animationBack(ship, ui.draggable);
	            return false;
		}

        var top = ui.position.top - (y % width);
        var left = ui.position.left - (x % width);
        switch (ship) {
            case 'aircraft':
				aircraft.point = setShipCoordinate(coordinateX, coordinateY, aircraftPosition, quantity);
                aircraft.lastPosition = {'top':top, 'left':left};
                break;
			case 'battleship':
				battleship.point = setShipCoordinate(coordinateX, coordinateY, battleshipPosition, quantity);
	            battleship.lastPosition = {'top':top, 'left':left};
	            break;
			case 'cruiser':
                cruiser.point = setShipCoordinate(coordinateX, coordinateY, cruiserPosition, quantity);
                cruiser.lastPosition = {'top':top, 'left':left};
                break;
            case 'submarine':
                submarine.point = setShipCoordinate(coordinateX, coordinateY, submarinePosition, quantity);
                submarine.lastPosition = {'top':top, 'left':left};
                break;
			case 'destroyer':
				destroyer.point = setShipCoordinate(coordinateX, coordinateY, destroyerPosition, quantity);
                destroyer.lastPosition = {'top':top, 'left':left};
                break;
        }
        ui.draggable.animate({
            top:top,
            left:left
        });
    }
});

$('#aircraft').draggable({
    revert:'invalid',
    start:function (event, ui) {
        quantity = 4;
        if (aircraft.point != undefined) {
            aircraft.lastPoint = aircraft.point;
            aircraft.point = [];
        } else {
            aircraft = [];
        }
    }
});

$('#battleship').draggable({
    revert:'invalid',
    start:function (event, ui) {
        quantity = 3;
        if (battleship.point != undefined) {
            battleship.lastPoint = battleship.point;
            battleship.point = [];
        } else {
            battleship = [];
        }
    }
});

$('#cruiser').draggable({
    revert:'invalid',
    start:function (event, ui) {
        quantity = 2;
        if (cruiser.point != undefined) {
            cruiser.lastPoint = cruiser.point;
            cruiser.point = [];
        } else {
            cruiser = [];
        }
    }
});

$('#submarine').draggable({
    revert:'invalid',
    start:function (event, ui) {
        quantity = 2;
        if (submarine.point != undefined) {
            submarine.lastPoint = submarine.point;
            submarine.point = [];
        } else {
            submarine = [];
        }
    }
});

$('#destroyer').draggable({
    revert:'invalid',
    start:function (event, ui) {
        quantity = 1;
        if (destroyer.point != undefined) {
            destroyer.lastPoint = destroyer.point;
            destroyer.point = [];
        } else {
            destroyer = [];
        }
    }
});

//Aircraft
$('#aircraft').dblclick(function() {
	var shipAttr = setDobleClickFunction(aircraft, aircraftPosition, 4, this);
	if(shipAttr.ship != undefined){
		aircraft = shipAttr.ship;
	}
	aircraftPosition = shipAttr.shipPosition;
});

//Battleship
$('#battleship').dblclick(function() {
	var shipAttr = setDobleClickFunction(battleship, battleshipPosition, 3, this);
	if(shipAttr.ship != undefined){
		battleship = shipAttr.ship;
	}
	battleshipPosition = shipAttr.shipPosition;
});

//Cruiser
$('#cruiser').dblclick(function() {
	var shipAttr = setDobleClickFunction(cruiser, cruiserPosition, 2, this);
	if(shipAttr.ship != undefined){
		cruiser = shipAttr.ship;
	}
	cruiserPosition = shipAttr.shipPosition;
});

//Submarine
$('#submarine').dblclick(function() {
	var shipAttr = setDobleClickFunction(submarine, submarinePosition, 2, this);
	if(shipAttr.ship != undefined){
		submarine = shipAttr.ship;
	}
	submarinePosition = shipAttr.shipPosition;
});

//Destroyer
$('#destroyer').dblclick(function() {
	var shipAttr = setDobleClickFunction(destroyer, destroyerPosition, 1, this);
	if(shipAttr.ship != undefined){
		destroyer = shipAttr.ship;
	}
	destroyerPosition = shipAttr.shipPosition;
});