package models;

import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.i18n.Messages;
import play.i18n.Lang;
import controllers.Language;
import java.util.*;

/**
 * Created by Mart0
 * Date: 4/24/12
 */
public class Game {
    private String gameId;
    private Player playerOne;
    private Player playerTwo;
    private Player currentPlayer;
    private boolean start;
    private boolean end;
    private int leavers;
    private CpuPlayer bot1;
    private CpuPlayer bot2;
    private Timer timer;

    public void toggleAutoPlay(Player player) {
        if (player.isAutoPlay()) {
            player.setAutoPlay(false);
            message(player, "autoPlay", Messages.get(player.getLang(), "autoPlay.disable"));
        } else {
            player.setAutoPlay(true);
            System.out.println(player.getLang());
            message(player, "autoPlay", Messages.get(player.getLang(), "autoPlay.enable"));
            if (player == currentPlayer) autoPlay();
        }
    }

    private void autoPlay() {
        TimerTask thinking = new TimerTask() {
            @Override
            public void run() {
                play();
            }
        };
        timer.schedule(thinking, 1500);
    }

    private void play() {
        CpuPlayer currentBot = currentPlayer == playerOne ? bot1 : bot2;
        BoardPoint point = currentBot.play();
        move(currentPlayer, point.getX() + "", point.getY() + "");
    }


    public enum ShootResult {
        MISS, HIT, SINK
    }

    public Game() {
        gameId = UUID.randomUUID().toString();
    }

    void startGame() {
        start = true;
        leavers = 0;
        bot1 = new CpuPlayer();
        bot2 = new CpuPlayer();
        timer = new Timer();
        notifyStart();
        setRandomTurn();
        requestStrategies();
    }

    private void requestStrategies() {
        requestStrategy(playerOne);
        requestStrategy(playerTwo);
    }

    private void requestStrategy(final Player player) {
        message(player, "askStrategy", Messages.get(player.getLang(), "set.all.ships"));
        message(player, "timer", Messages.get(player.getLang(), "minute.left"));

        TimerTask strategyTimeOutReminder = new TimerTask() {
            int counter = 0;

            @Override
            public void run() {
                counter += 15;
                remindPlayer(player, counter);
            }
        };
        timer.scheduleAtFixedRate(strategyTimeOutReminder, 1000 * 15, 1000 * 15);
    }

    private void remindPlayer(Player player, int counter) {
        if (!player.isReady()) {
            int remainingTime = 60 - counter;
            if (remainingTime <= 0) {
                message(player, "timer", Messages.get(player.getLang(), "time.up"));
                timeOut(player);
            } else {
                message(player, "timer", Messages.get(player.getLang(), "hurry.up.remaining.time", remainingTime));
            }
        }
    }

    private void timeOut(Player player) {
        if (!player.isReady()) setRandomStrategy(player);
    }

    private void startTurns() {
        sendStrategies();
        notifyTurn();
    }

    private void sendStrategies() {
        sendStrategy(playerOne, playerOne.getStrategy());
        sendStrategy(playerTwo, playerTwo.getStrategy());
    }

    private void sendStrategy(Player playerTo, List<Ship> strategy) {
        final ObjectNode json = Json.newObject();
        json.put("type", "strategy");
        json.put("message", Messages.get(playerTo.getLang(), "placing.ships.board"));
        String strategyData = Json.toJson(strategy).toString();
        json.put("strategy", strategyData);
        playerTo.getChannel().write(json);
    }

    private void moveCalculation(BoardPoint point) {
        ShootResult result = shoot(point);
        String name = getCurrentPlayer().getUsername().toUpperCase();
        String x = point.getX() + "";
        String y = point.getY() + "";
        String shootPoint = x + y;
        updateBot(point, result);
        String msgResult = Messages.get(getCurrentPlayer().getLang(), result.toString());
        sendShot(getCurrentPlayer(), "my-shot", Messages.get(getCurrentPlayer().getLang(), "your.shoot.msg", x, y, msgResult), shootPoint, result.toString());
        sendShot(getAlternative(), "op-shot", Messages.get(getCurrentPlayer().getLang(), "shoot.msg", name, x, y, msgResult), shootPoint, result.toString());
    }

    private void updateBot(BoardPoint point, ShootResult result) {
        CpuPlayer currentBot = currentPlayer == playerOne ? bot1 : bot2;
        currentBot.update(point, result);
    }

    private void sendShot(Player player, String type, String message, String point, String result) {
        final ObjectNode json = Json.newObject();
        json.put("type", type);
        json.put("message", message);
        json.put("point", point);
        json.put("result", result);
        player.getChannel().write(json);
    }

    private void setRandomTurn() {
        Random turnRoller = new Random();
        int roll = turnRoller.nextInt(2) + 1;
        currentPlayer = roll == 1 ? playerOne : playerTwo;
    }

    public void setRandomStrategy(Player player) {
        Player actual = playerOne == player ? playerOne : playerTwo;

        List<Ship> strategy = StrategyFactory.createRandomStrategy();
        actual.setStrategy(strategy);

        notifyStrategyReceived(actual);

        if (playerOne.isReady() && playerTwo.isReady()) {
            startTurns();
        }
    }

    public void setStrategy(Player player, List<String> strategyString) {
        Player actual = playerOne == player ? playerOne : playerTwo;
        List<Ship> strategy = new LinkedList<Ship>();
        for (String shipString : strategyString) {
            String[] shipData = shipString.split("-");
            String type = shipData[0];
            String[] point = shipData[1].split(",");
            String position = shipData[2];
            int x = Integer.parseInt(point[0]);
            int y = Integer.parseInt(point[1]);
            strategy.add(new Ship(x, y, type, position));
        }
        if (StrategyFactory.isValidStrategy(strategy)) {
            actual.setStrategy(strategy);
            notifyStrategyReceived(player);
        } else {
            setRandomStrategy(actual);
            message(player, "strategyReceived", Messages.get(player.getLang(), "invalid.strategy"));
            notifyStrategyReceived(player);
        }

        if (playerOne.isReady() && playerTwo.isReady()) {
            startTurns();
        }
    }

    private void notifyStrategyReceived(Player player) {
        message(player, "strategyReceived", Messages.get(player.getLang(), "ready.wait.another.player"));
    }

    public void setPlayerA(Player playerOne) {
        this.playerOne = playerOne;
        message(playerOne, "wait", Messages.get(playerOne.getLang(), "waiting.players"));
    }

    private void notifyStart() {
        message(playerOne, "start", Messages.get(playerOne.getLang(), "battle.on.playing.against", playerTwo.getUsername()));
        message(playerTwo, "start", Messages.get(playerTwo.getLang(), "battle.on.playing.against", playerOne.getUsername()));
    }

    private void notifyTurn() {
        message(getCurrentPlayer(), "play", Messages.get(getCurrentPlayer().getLang(), "your.turn"));
        message(getAlternative(), "wait", Messages.get(getCurrentPlayer().getLang(), "other.player.move"));
    }

    private ShootResult shoot(BoardPoint point) {
        getCurrentPlayer().getShoots().add(point);
        List<Ship> ships = getAlternative().getStrategy();
        ShootResult result = null;
        for (Ship ship : ships) {
            result = ship.shoot(point);
            if (result != ShootResult.MISS) return result;
        }
        return result;
    }

    public void leave(Player player) {
        leavers++;
        if (playerOne != null && playerTwo != null) {
            Player notQuitter = isCurrent(player) ? getAlternative() : getCurrentPlayer();
            message(notQuitter, "leave", Messages.get(player.getLang(), "other.player.left"));
        }
    }

    public void chat(Player player, String talk) {
        if (start) {
            chatMessage(playerOne, "chat", player.getUsername(), talk);
            chatMessage(playerTwo, "chat", player.getUsername(), talk);
        } else {
            message(player, "wait", Messages.get(player.getLang(), "still.waiting"));
        }
    }

    public void move(Player player, String x, String yString) {
        if (end) notifyEnd(player);
        else if (getCurrentPlayer() == player) {
            int y;
            try {
                y = Integer.parseInt(yString);
            } catch (NumberFormatException e) {
                y = 0;
            }
            if ((x.equals("")) || y == 0 || x.length() != 1 || y < 0 || y > 10) {
                message(player, "mistake", Messages.get(player.getLang(), "cant.register.empty.shoot"));
            } else {
                moveCalculation(new BoardPoint(x.charAt(0), y));
                if (isTheWinner()) {
                    end = true;
                    notifyEnd();
                } else {
                    changeTurn();
                }
            }
        } else if (start) message(player, "wait", Messages.get(player.getLang(), "not.your.turn"));
        else message(player, "wait", Messages.get(player.getLang(), "still.waiting"));
    }

    private void notifyEnd(Player player) {
        String endMsg =   Messages.get(player.getLang(), "still.waiting", getCurrentPlayer().getUsername(), getCurrentPlayer().getShoots().size());
        message(player, "end", endMsg);
    }

    private void notifyEnd() {
        notifyEnd(playerOne);
        notifyEnd(playerTwo);
    }

    private boolean isTheWinner() {
        List<Ship> ships = getAlternative().getStrategy();
        int sunk = 0;
        for (Ship ship : ships) {
            if (ship.isSink()) sunk++;
        }
        return sunk == ships.size();
    }

    private void chatMessage(Player playerTo, String type, String playerFrom, String talk) {
        final ObjectNode json = Json.newObject();
        json.put("name", playerFrom);
        json.put("type", type);
        json.put("message", talk);
        playerTo.getChannel().write(json);
    }

    public static void message(Player player, String type, String message) {
        final ObjectNode json = Json.newObject();
        json.put("type", type);
        json.put("message", message);
        player.getChannel().write(json);
    }

    private boolean isCurrent(Player player) {
        return player == getCurrentPlayer();
    }

    private void changeTurn() {
        currentPlayer = currentPlayer == playerOne ? playerTwo : playerOne;
        notifyTurn();
        if (currentPlayer.isAutoPlay()) {
            autoPlay();
        }
    }

    public boolean isPlayerOneDefined() {
        return playerOne != null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getAlternative() {
        return currentPlayer == playerOne ? playerTwo : playerOne;
    }

    public boolean isPlayerTwoDefined() {
        return playerTwo != null;
    }

    public void setPlayerB(Player playerB) {
        this.playerTwo = playerB;
    }

    public String getGameId() {
        return gameId;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isEmpty() {
        return start ? (leavers == 2) : leavers == 1;
    }

    @Override
    public String toString() {
        return "Game{" +
                "playerOne=" + playerOne +
                ", playerTwo=" + playerTwo +
                ", start=" + start +
                ", leavers=" + leavers +
                '}';
    }
}
