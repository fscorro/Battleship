package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mart0
 * Date: 5/22/12
 */
public class StrategyFactory {

    public static List<Ship> createRandomStrategy() {
        List<Ship> ships = new ArrayList<Ship>();
        boolean[][] collisionGrid = new boolean[12][12];
        ships.add(generateRandomShip(Ship.ShipType.AIRCRAFT_CARRIER, collisionGrid));
        ships.add(generateRandomShip(Ship.ShipType.BATTLESHIP, collisionGrid));
        ships.add(generateRandomShip(Ship.ShipType.CRUISER, collisionGrid));
        ships.add(generateRandomShip(Ship.ShipType.SUBMARINE, collisionGrid));
        ships.add(generateRandomShip(Ship.ShipType.DESTROYER, collisionGrid));
        return ships;
    }

    public static List<Ship> createDefaultStrategy() {
        List<BoardPoint> aList = new LinkedList<BoardPoint>();
        aList.add(new BoardPoint('B', 1));
        aList.add(new BoardPoint('B', 2));
        aList.add(new BoardPoint('B', 3));
        aList.add(new BoardPoint('B', 4));
        aList.add(new BoardPoint('B', 5));
        Ship a = new Ship(Ship.ShipType.AIRCRAFT_CARRIER, aList);

        List<BoardPoint> bList = new LinkedList<BoardPoint>();
        bList.add(new BoardPoint('G', 9));
        bList.add(new BoardPoint('H', 9));
        bList.add(new BoardPoint('I', 9));
        bList.add(new BoardPoint('J', 9));
        Ship b = new Ship(Ship.ShipType.BATTLESHIP, bList);

        List<BoardPoint> cList = new LinkedList<BoardPoint>();
        cList.add(new BoardPoint('I', 1));
        cList.add(new BoardPoint('I', 2));
        cList.add(new BoardPoint('I', 3));
        Ship c = new Ship(Ship.ShipType.CRUISER, cList);

        List<BoardPoint> dList = new LinkedList<BoardPoint>();
        dList.add(new BoardPoint('D', 5));
        dList.add(new BoardPoint('E', 5));
        dList.add(new BoardPoint('F', 5));
        Ship d = new Ship(Ship.ShipType.SUBMARINE, dList);

        List<BoardPoint> eList = new LinkedList<BoardPoint>();
        eList.add(new BoardPoint('B', 8));
        eList.add(new BoardPoint('C', 8));
        Ship e = new Ship(Ship.ShipType.DESTROYER, eList);

        List<Ship> strategy = new ArrayList<Ship>();
        strategy.add(a);
        strategy.add(b);
        strategy.add(c);
        strategy.add(d);
        strategy.add(e);
        return strategy;
    }

    private static Ship generateRandomShip(Ship.ShipType shipType, boolean[][] collisionGrid) {
        int shipSize = shipType.getShipSize();
        Random random = new Random();
        Ship.Position position = random.nextBoolean() ? Ship.Position.VERTICAL : Ship.Position.HORIZONTAL;
        int x, y;
        if (position == Ship.Position.VERTICAL) {
            x = random.nextInt(10) + 1;
            y = random.nextInt(10 - shipSize) + 1;

        } else {
            x = random.nextInt(10 - shipSize) + 1;
            y = random.nextInt(10) + 1;

        }
        Ship ship = new Ship(x, y, shipType, position);
        if (isColliding(ship, collisionGrid)) {
            return generateRandomShip(shipType, collisionGrid);
        } else {
            loadInCollisionGrid(ship, collisionGrid);
            return ship;
        }
    }

    private static void loadInCollisionGrid(Ship ship, boolean[][] collisionGrid) {
        for (BoardPoint boardPoint : ship.getBody()) {
            int x = boardPoint.getXInt();
            int y = boardPoint.getY();
            collisionGrid[x][y] = true;
            // No ship next to each other (vertical and horizontal)
            collisionGrid[x + 1][y] = true;
            collisionGrid[x][y + 1] = true;
            collisionGrid[x - 1][y] = true;
            collisionGrid[x][y - 1] = true;
            // No ship next to each other (diagonal)
            collisionGrid[x + 1][y + 1] = true;
            collisionGrid[x + 1][y - 1] = true;
            collisionGrid[x - 1][y + 1] = true;
            collisionGrid[x - 1][y - 1] = true;
        }
    }

    private static boolean isColliding(Ship ship, boolean[][] collisionGrid) {
        for (BoardPoint boardPoint : ship.getBody()) {
            int x = boardPoint.getXInt();
            int y = boardPoint.getY();
            if (collisionGrid[x][y]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidStrategy(List<Ship> strategy){
        return checkCollision(strategy);
    }


    private static boolean checkCollision(List<Ship> shipList) {
        boolean[][] collisionGrid = new boolean[12][12];
        for (Ship ship : shipList) {
            if(isColliding(ship,collisionGrid)){
               return false;
            }else {
                loadInCollisionGrid(ship,collisionGrid);
            }
        }
        return true;
    }

    private static String getGridString(boolean[][] collisionGrid) {
        String out = "INICIA:";
        for (int i = 0; i < collisionGrid.length; i++) {
            out += "\n";
            for (int j = 0; j < collisionGrid.length; j++) {
                if (collisionGrid[j][i]) {
                    out += "T" + " , ";
                } else {
                    out += "F" + " , ";
                }
            }
        }
        return out;
    }

}
