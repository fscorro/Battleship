package models;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CpuPlayer {
    private static String NAME = "Bot";
    //    private List<Ship> shipList;
    private List<Point> availablePointList;
    private int x;
    private int y;
    private int[][] possibilityMatrix;

    public CpuPlayer() {
        availablePointList = new ArrayList<Point>();
        possibilityMatrix = new int[10][10];
//        shipList = new ArrayList<Ship>();
    }

    public String getName() {
        return NAME;
    }

    public void initPlayer(List<Ship> ships) {
//        shipList.addAll(ships);
    }


    public BoardPoint play() {
//        showPossibilityMatrix(possibilityMatrix);
        buildNearHitAvailablePointList();
        if (availablePointList.size() == 0) {
            buildAvailablePointList();
        }
        Collections.shuffle(availablePointList);
        x = (int) availablePointList.get(0).getX();
        y = (int) availablePointList.get(0).getY();

//         Change point to adapt to from a 1-10 to a 0-9 paradigm
        return new BoardPoint(x + 1, y + 1);
    }

    public void playResult(Game.ShootResult hitResult) {
        analizeResult(hitResult);
    }


    public void update(BoardPoint point, Game.ShootResult result) {
//         Change point to adapt to from a 1-10 to a 0-9 paradigm
        x = point.getXInt() - 1;
        y = point.getY() - 1;
        playResult(result);
    }

    private void buildNearHitAvailablePointList() {
        availablePointList.clear();
        for (x = 0; x <= 9; x++) {
            for (y = 0; y <= 9; y++) {
                if (possibilityMatrix[x][y] == 1) {
                    availablePointList.add(new Point(x, y));
                }
            }
        }
    }

    private void buildAvailablePointList() {
        availablePointList.clear();
        for (x = 0; x <= 9; x++) {
            for (y = 0; y <= 9; y++) {
                if (possibilityMatrix[x][y] == 0) {
                    availablePointList.add(new Point(x, y));
                }
            }
        }
    }

    private void analizeResult(Game.ShootResult hitResult) {
        possibilityMatrix[x][y] = 2;
        if (hitResult == Game.ShootResult.HIT || hitResult == Game.ShootResult.SINK) {
            if (x - 1 >= 0) {
                if (y - 1 >= 0) {
                    possibilityMatrix[x - 1][y - 1] = 2;
                    if (hitResult == Game.ShootResult.HIT) {
                        if (possibilityMatrix[x][y - 1] == 0) {
                            possibilityMatrix[x][y - 1] = 1;
                        }
                        if (possibilityMatrix[x - 1][y] == 0) {
                            possibilityMatrix[x - 1][y] = 1;
                        }
                    }
                    if (hitResult == Game.ShootResult.SINK) {
                        possibilityMatrix[x][y - 1] = 2;
                        possibilityMatrix[x - 1][y] = 2;
                    }
                }
                if (y + 1 <= 9) {
                    possibilityMatrix[x - 1][y + 1] = 2;
                    if (hitResult == Game.ShootResult.HIT) {
                        if (possibilityMatrix[x][y + 1] == 0) {
                            possibilityMatrix[x][y + 1] = 1;
                        }
                    }
                    if (hitResult == Game.ShootResult.SINK) {
                        possibilityMatrix[x][y + 1] = 2;
                    }
                }
            }
            if (x + 1 <= 9) {
                if (y - 1 >= 0) {
                    possibilityMatrix[x + 1][y - 1] = 2;
                    if (hitResult == Game.ShootResult.HIT) {
                        if (possibilityMatrix[x + 1][y] == 0) {
                            possibilityMatrix[x + 1][y] = 1;
                        }
                        if (possibilityMatrix[x][y - 1] == 0) {
                            possibilityMatrix[x][y - 1] = 1;
                        }
                    }
                    if (hitResult == Game.ShootResult.SINK) {
                        possibilityMatrix[x + 1][y] = 2;
                    }
                }
                if (y + 1 <= 9) {
                    possibilityMatrix[x + 1][y + 1] = 2;
                    if (hitResult == Game.ShootResult.HIT) {
                        if (possibilityMatrix[x][y + 1] == 0) {
                            possibilityMatrix[x][y + 1] = 1;
                        }
                        if (x - 1 >= 0) {
                            if (possibilityMatrix[x - 1][y] == 0) {
                                possibilityMatrix[x - 1][y] = 1;
                            }
                        }
                        if (possibilityMatrix[x + 1][y] == 0) {
                            possibilityMatrix[x + 1][y] = 1;
                        }
                        if (possibilityMatrix[x][y + 1] == 0) {
                            possibilityMatrix[x][y + 1] = 1;
                        }
                    }
                }
            }
        }
    }

    private void showPossibilityMatrix(int[][] warZone) {
        JOptionPane.showMessageDialog(null, "Possibility Matrix:\n"
                + warZone[0][0] + " " + warZone[1][0] + " " + warZone[2][0] + " " + warZone[3][0] + " " + warZone[4][0] + " " + warZone[5][0] + " " + warZone[6][0] + " " + warZone[7][0] + " " + warZone[8][0] + " " + warZone[9][0] + "\n"
                + warZone[0][1] + " " + warZone[1][1] + " " + warZone[2][1] + " " + warZone[3][1] + " " + warZone[4][1] + " " + warZone[5][1] + " " + warZone[6][1] + " " + warZone[7][1] + " " + warZone[8][1] + " " + warZone[9][1] + "\n"
                + warZone[0][2] + " " + warZone[1][2] + " " + warZone[2][2] + " " + warZone[3][2] + " " + warZone[4][2] + " " + warZone[5][2] + " " + warZone[6][2] + " " + warZone[7][2] + " " + warZone[8][2] + " " + warZone[9][2] + "\n"
                + warZone[0][3] + " " + warZone[1][3] + " " + warZone[2][3] + " " + warZone[3][3] + " " + warZone[4][3] + " " + warZone[5][3] + " " + warZone[6][3] + " " + warZone[7][3] + " " + warZone[8][3] + " " + warZone[9][3] + "\n"
                + warZone[0][4] + " " + warZone[1][4] + " " + warZone[2][4] + " " + warZone[3][4] + " " + warZone[4][4] + " " + warZone[5][4] + " " + warZone[6][4] + " " + warZone[7][4] + " " + warZone[8][4] + " " + warZone[9][4] + "\n"
                + warZone[0][5] + " " + warZone[1][5] + " " + warZone[2][5] + " " + warZone[3][5] + " " + warZone[4][5] + " " + warZone[5][5] + " " + warZone[6][5] + " " + warZone[7][5] + " " + warZone[8][5] + " " + warZone[9][5] + "\n"
                + warZone[0][6] + " " + warZone[1][6] + " " + warZone[2][6] + " " + warZone[3][6] + " " + warZone[4][6] + " " + warZone[5][6] + " " + warZone[6][6] + " " + warZone[7][6] + " " + warZone[8][6] + " " + warZone[9][6] + "\n"
                + warZone[0][7] + " " + warZone[1][7] + " " + warZone[2][7] + " " + warZone[3][7] + " " + warZone[4][7] + " " + warZone[5][7] + " " + warZone[6][7] + " " + warZone[7][7] + " " + warZone[8][7] + " " + warZone[9][7] + "\n"
                + warZone[0][8] + " " + warZone[1][8] + " " + warZone[2][8] + " " + warZone[3][8] + " " + warZone[4][8] + " " + warZone[5][8] + " " + warZone[6][8] + " " + warZone[7][8] + " " + warZone[8][8] + " " + warZone[9][8] + "\n"
                + warZone[0][9] + " " + warZone[1][9] + " " + warZone[2][9] + " " + warZone[3][9] + " " + warZone[4][9] + " " + warZone[5][9] + " " + warZone[6][9] + " " + warZone[7][9] + " " + warZone[8][9] + " " + warZone[9][9] + "\n");
    }
}