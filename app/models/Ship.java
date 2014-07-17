package models;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mart0
 * Date: 4/29/12
 */
public class Ship {
    private ShipType type;
    private List<BoardPoint> body;
    private List<BoardPoint> hits;
    private boolean sink;

    public Ship(int x, int y, ShipType shipType, Position position) {
        init(x, y, shipType, position);
    }

    private void init(int x, int y, ShipType shipType, Position position) {
        int shipSize = shipType.getShipSize();
        body = new LinkedList<BoardPoint>();
        for (int i = 0; i < shipSize; i++) {
            if (position == Position.VERTICAL) {
                body.add(new BoardPoint(x, y + i));
            } else {
                body.add(new BoardPoint(x + i, y));
            }
        }
        type = shipType;
        sink = false;
        hits = new LinkedList<BoardPoint>();
    }

    public Ship(int x, int y, String type, String position) {
        Position pos = position.equalsIgnoreCase("VERTICAL") ? Position.VERTICAL : Position.HORIZONTAL;
        ShipType shipType;
        if (type.equalsIgnoreCase(ShipType.AIRCRAFT_CARRIER.toString())) {
            shipType = ShipType.AIRCRAFT_CARRIER;
        } else if (type.equalsIgnoreCase(ShipType.BATTLESHIP.toString())) {
            shipType = ShipType.BATTLESHIP;
        } else if (type.equalsIgnoreCase(ShipType.CRUISER.toString())) {
            shipType = ShipType.CRUISER;
        } else if (type.equalsIgnoreCase(ShipType.SUBMARINE.toString())) {
            shipType = ShipType.SUBMARINE;
        } else {
            shipType = ShipType.DESTROYER;
        }
        init(x, y, shipType, pos);
    }

    public enum Position {
        VERTICAL, HORIZONTAL
    }

    public enum ShipType {
        AIRCRAFT_CARRIER(5), BATTLESHIP(4), CRUISER(3), SUBMARINE(3), DESTROYER(2);
        private int shipSize;

        ShipType(int shipSize) {
            this.shipSize = shipSize;
        }

        public int getShipSize() {
            return shipSize;
        }
    }

    public Ship(ShipType type, List<BoardPoint> body) {
        sink = false;
        this.type = type;
        this.body = body;
        hits = new LinkedList<BoardPoint>();
    }

    public Game.ShootResult shoot(BoardPoint boardPoint) {
        Game.ShootResult result = Game.ShootResult.MISS;
        if (isContained(boardPoint, body)) {
            if (!isContained(boardPoint, hits)) hits.add(boardPoint);
            result = Game.ShootResult.HIT;
            if (hits.size() == body.size()) {
                sink = true;
                result = Game.ShootResult.SINK;
            }
        }
        return result;
    }

    private boolean isContained(BoardPoint point, List<BoardPoint> list) {
        for (BoardPoint boardPoint : list) {
            if (boardPoint.equals(point)) return true;
        }
        return false;
    }

    public ShipType getType() {
        return type;
    }

    public List<BoardPoint> getBody() {
        return body;
    }

    public boolean isSink() {
        return sink;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "type=" + type +
                ", body=" + body +
                '}';
    }
}
