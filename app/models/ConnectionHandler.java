package models;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;
import play.i18n.Lang;
import play.i18n.Messages;

import java.util.*;

/**
 * Created by Mart0
 * Date: 4/24/12
 */
public class ConnectionHandler {
    private static List<Game> gameList = new ArrayList<Game>();
    private static int gamesPlayed = 0;
    private static int activeGames = 0;

    public static void join(String username, Lang lang, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
        Game lastGame = getLastGame();
        if (!lastGame.isPlayerOneDefined()) {
            final Player player = new Player(username, lang, out, lastGame.getGameId());
            lastGame.setPlayerA(player);
            bingInWebSocket(in, player);
        } else if (!lastGame.isPlayerTwoDefined()) {
            final Player player = new Player(username, lang, out, lastGame.getGameId());
            lastGame.setPlayerB(player);
            bingInWebSocket(in, player);
            lastGame.startGame();
        } else {
            createNewGame();
            join(username, lang, in, out);
//            out.write(createServerFullMsg());
        }
    }

    private static Game getLastGame() {
        Game last;
        if (gameList.isEmpty()) {
            createNewGame();
            last = gameList.get(0);
        } else last = gameList.get(gameList.size() - 1);
        return last;
    }

    private static void createNewGame() {
        gamesPlayed++;
        activeGames++;
        gameList.add(new Game());
    }

    private static JsonNode createServerFullMsg() {
        final ObjectNode json = Json.newObject();
        json.put("error", "The server is full, try again later.");
        return json;
    }

    private static void bingInWebSocket(WebSocket.In<JsonNode> in, final Player player) {
        in.onMessage(new F.Callback<JsonNode>() {
            public void invoke(JsonNode jsonNode) throws Throwable {
                Game game = getGameById(player.getGameId());
                String messageType = jsonNode.get("type").asText();

                System.out.println("Event Received: " + messageType);

                if (game.isStart()) {
                    if (messageType.equals("chat")) {
//                        Chat behavior
                        final String talk = jsonNode.get("text").asText();
                        game.chat(player, talk);
                    } else if (messageType.equals("shot")) {
//                        Shoot behavior
                        final String x = jsonNode.get("x").asText().toUpperCase();
                        final String y = jsonNode.get("y").asText().toUpperCase();
                        game.move(player, x, y);
                    } else if (messageType.equals("strategy")) {
//                        Strategy behavior
                        List<String> strategyString = new LinkedList<String>();
                        strategyString.add(jsonNode.get("AIRCRAFT_CARRIER").asText());
                        strategyString.add(jsonNode.get("BATTLESHIP").asText());
                        strategyString.add(jsonNode.get("CRUISER").asText());
                        strategyString.add(jsonNode.get("SUBMARINE").asText());
                        strategyString.add(jsonNode.get("DESTROYER").asText());
                        game.setStrategy(player, strategyString);
                    } else if (messageType.equals("autoPlay")) {
//                        AutoPlay behavior
                        game.toggleAutoPlay(player);
                    } else if (messageType.equals("randomStrategy")) {
//                        Random Strategy behavior
                        game.setRandomStrategy(player);
                    }
                } else {
//                    Waiting for another player behavior
                    game.chat(player, "");
                }
                if (messageType.equals("serverInfo")) {
                    Game.message(player, "info", "  " + activeGames + " " + Messages.get(player.getLang(), "active.games") + " - " +
                            gamesPlayed + " " + Messages.get(player.getLang(), "total.games.played"));
                }
            }
        });

        in.onClose(new F.Callback0() {
            public void invoke() throws Throwable {
                Game game = getGameById(player.getGameId());
                game.leave(player);
                if (game.isEmpty()) {
                    gameList.remove(gameList.indexOf(game));
                    activeGames--;
                }
            }
        });
    }

    private static Game getGameById(String gameId) {
        for (Game game : gameList) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }
        return null;
    }

}
