//a, b, c, d are points (x, y)
//if there's a collision, return 1

function collision(a, b, c, d) {
    var parallel = (a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x);
    if (parallel == 0) {
        if (a.y == c.y) {
            if (a.x <= d.x && b.x >= c.x)
                return 1;
            return 0;
        }
        if (a.x == c.x) {
            if (a.y <= d.y && b.y >= c.y)
                return 1;
            return 0;
        }
        return 0;
    } else {
        var px = ((a.x * b.y - a.y * b.x) * (c.x - d.x) - (a.x - b.x) * (c.x * d.y - c.y * d.x)) / parallel;
        var py = ((a.x * b.y - a.y * b.x) * (c.y - d.y) - (a.y - b.y) * (c.x * d.y - c.y * d.x)) / parallel;

        var k1 = (px - a.x) / (b.x - a.x);
        var k2 = (px - c.x) / (d.x - c.x);
        var k3 = (py - a.y) / (b.y - a.y);
        var k4 = (py - c.y) / (d.y - c.y);

        if ((k1 >= 0 && k1 <= 1) || isNaN(k1)) {
            if ((k2 >= 0 && k2 <= 1) || isNaN(k2)) {
                if ((k3 >= 0 && k3 <= 1) || isNaN(k3)) {
                    if ((k4 >= 0 && k4 <= 1) || isNaN(k4)) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}

function rotate(obj) {
    var width = obj.width();
    var height = obj.height();
    obj.width(height);
    obj.height(width);
}

function setLastPosition(ship, draggable){
	if (ship.lastPosition != undefined) {
        draggable.animate({
            top:ship.lastPosition.top,
            left:ship.lastPosition.left
        });
		return 1;
    } else {
        draggable.animate({
            top:0,
            left:0
        });
		return 0;
    }
}

function animationBack(ship, draggable) {
    var result = -1;
	switch (ship) {
        case 'aircraft':
            result = setLastPosition(aircraft, draggable);
			if(result == 1){
				aircraft.point = aircraft.lastPoint;
			}else{
				aircraft = [];
			}
            break;
        case 'battleship':
            result = setLastPosition(battleship, draggable);
			if(result == 1){
				battleship.point = battleship.lastPoint;
			}else{
				battleship = [];
			}
            break;
        case 'cruiser':
            result = setLastPosition(cruiser, draggable);
			if(result == 1){
				cruiser.point = cruiser.lastPoint;
			}else{
				cruiser = [];
			}
            break;
        case 'submarine':
            result = setLastPosition(submarine, draggable);
			if(result == 1){
				submarine.point = submarine.lastPoint;
			}else{
				submarine = [];
			}
            break;
        case 'destroyer':
            result = setLastPosition(destroyer, draggable);
			if(result == 1){
				destroyer.point = destroyer.lastPoint;
			}else{
				destroyer = [];
			}
            break;
    }
}

function testShipCollision(pointC, pointD, ship, shipPosition){
	if (collision(ship.point[0], ship.point[1], pointC, pointD)) {
        return 1;
    }
	if(shipPosition != 0){
		if(collision({'x':ship.point[0].x - 1, 'y':ship.point[0].y - 1}, {'x':ship.point[0].x + 1, 'y':ship.point[0].y - 1}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[0].x - 1, 'y':ship.point[0].y}, {'x':ship.point[0].x - 1, 'y':ship.point[1].y}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[0].x + 1, 'y':ship.point[0].y}, {'x':ship.point[0].x + 1, 'y':ship.point[1].y}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[1].x - 1, 'y':ship.point[1].y + 1}, {'x':ship.point[1].x + 1, 'y':ship.point[1].y + 1}, pointC, pointD)){
			return 2;
		}
	}else{
		if(collision({'x':ship.point[0].x, 'y':ship.point[0].y - 1}, {'x':ship.point[1].x, 'y':ship.point[1].y - 1}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[0].x, 'y':ship.point[0].y + 1}, {'x':ship.point[1].x, 'y':ship.point[1].y + 1}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[0].x - 1, 'y':ship.point[0].y - 1}, {'x':ship.point[0].x - 1, 'y':ship.point[0].y + 1}, pointC, pointD)){
			return 2;
		}
		if(collision({'x':ship.point[1].x + 1, 'y':ship.point[1].y - 1}, {'x':ship.point[1].x + 1, 'y':ship.point[1].y + 1}, pointC, pointD)){
			return 2;
		}
	}
	return -1;
}

function testCollision(pointC, pointD, id) {
    var result = 9;
	if (aircraft.point != undefined && id != 'aircraft') {
        result = testShipCollision(pointC, pointD, aircraft, aircraftPosition);
		if(result != -1)
			return result;
    }
	if (battleship.point != undefined && id != 'battleship') {
		result = testShipCollision(pointC, pointD, battleship, battleshipPosition);
		if(result != -1)
			return result;
    }
	if (cruiser.point != undefined && id != 'cruiser') {
       	result = testShipCollision(pointC, pointD, cruiser, cruiserPosition);
		if(result != -1)
			return result;
    }
    if (submarine.point != undefined && id != 'submarine') {
        result = testShipCollision(pointC, pointD, submarine, submarinePosition);
		if(result != -1)
			return result;
    }
	if (destroyer.point != undefined && id != 'destroyer') {
        result = testShipCollision(pointC, pointD, destroyer, destroyerPosition);
		if(result != -1)
			return result;
    }
	if (pointD.x > 11 || pointD.y > 11 || pointC.x < 2 || pointC.y < 2) {
        return 3;
    }
    return 0;
}

function shipsPositionJSON() {
    var letters = "ABCDEFGHIJ".split("");
    var strategy = [];
    //aircraft
    strategy.push({'type':"AIRCRAFT_CARRIER", 'sink':false, 'body':[
        {'x':letters[(aircraft.point[0].x - 1) - 1], 'xint':(aircraft.point[0].x - 1), 'y':(aircraft.point[0].y - 1)},
        {'x':letters[(aircraft.point[1].x - 1) - 1], 'xint':(aircraft.point[1].x - 1), 'y':(aircraft.point[1].y - 1)}
    ]});
    //battleship
    strategy.push({'type':"BATTLESHIP", 'sink':false, 'body':[
        {'x':letters[(battleship.point[0].x - 1) - 1], 'xint':(battleship.point[0].x - 1), 'y':(battleship.point[0].y - 1)},
        {'x':letters[(battleship.point[1].x - 1) - 1], 'xint':(battleship.point[1].x - 1), 'y':(battleship.point[1].y - 1)}
    ]});
    //cruiser
    strategy.push({'type':"CRUISER", 'sink':false, 'body':[
        {'x':letters[(cruiser.point[0].x - 1) - 1], 'xint':(cruiser.point[0].x - 1), 'y':(cruiser.point[0].y - 1)},
        {'x':letters[(cruiser.point[1].x - 1) - 1], 'xint':(cruiser.point[1].x - 1), 'y':(cruiser.point[1].y - 1)}
    ]});
    //submarine
    strategy.push({'type':"SUBMARINE", 'sink':false, 'body':[
        {'x':letters[(submarine.point[0].x - 1) - 1], 'xint':(submarine.point[0].x - 1), 'y':(submarine.point[0].y - 1)},
        {'x':letters[(submarine.point[1].x - 1) - 1], 'xint':(submarine.point[1].x - 1), 'y':(submarine.point[1].y - 1)}
    ]});
    //destroyer
    strategy.push({'type':"DESTROYER", 'sink':false, 'body':[
        {'x':letters[(destroyer.point[0].x - 1) - 1], 'xint':(destroyer.point[0].x - 1), 'y':(destroyer.point[0].y - 1)},
        {'x':letters[(destroyer.point[1].x - 1) - 1], 'xint':(destroyer.point[1].x - 1), 'y':(destroyer.point[1].y - 1)}
    ]});
    return strategy;
}

function sendStrategy() {
    if (aircraft.point.length == 0 || battleship.point.length == 0 || cruiser.point.length == 0 || submarine.point.length == 0 || destroyer.point.length == 0) {
        alert("You must place all the ships");
    } else {
        var aircraftString = "AIRCRAFT_CARRIER-" +(aircraft.point[0].x - 1) + "," + (aircraft.point[0].y - 1);
        if (aircraft.point[0].x != aircraft.point[1].x) aircraftString += "-HORIZONTAL";
        else aircraftString += "-VERTICAL";

        var battleshipString = "BATTLESHIP-" +(battleship.point[0].x - 1) + "," + (battleship.point[0].y - 1);
        if (battleship.point[0].x != battleship.point[1].x) battleshipString += "-HORIZONTAL";
        else battleshipString += "-VERTICAL";

        var cruiserString = "CRUISER-" +(cruiser.point[0].x - 1) + "," + (cruiser.point[0].y - 1);
        if (cruiser.point[0].x != cruiser.point[1].x) cruiserString += "-HORIZONTAL";
        else cruiserString += "-VERTICAL";

        var submarineString = "SUBMARINE-" +(submarine.point[0].x - 1) + "," + (submarine.point[0].y - 1);
        if (submarine.point[0].x != submarine.point[1].x) submarineString += "-HORIZONTAL";
        else submarineString += "-VERTICAL";

        var destroyerString = "DESTROYER-" +(destroyer.point[0].x - 1) + "," + (destroyer.point[0].y - 1);
        if (destroyer.point[0].x != destroyer.point[1].x) destroyerString += "-HORIZONTAL";
        else destroyerString += "-VERTICAL";

        var json = JSON.stringify(
            {
                type:"strategy",
                AIRCRAFT_CARRIER:aircraftString,
                BATTLESHIP:battleshipString,
                CRUISER:cruiserString,
                SUBMARINE:submarineString,
                DESTROYER:destroyerString
            }
        );
        sentToServer(json);
    }
}

function sendRandomStrategy(){
    var json = JSON.stringify(
                {
                    type:"randomStrategy",
                    random: "selected"
                }
            );
            sentToServer(json);
}