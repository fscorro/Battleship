package models;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

import play.i18n.Lang;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mart0
 * Date: 4/24/12
 */
public class Player {
    private String username;
    private WebSocket.Out<JsonNode> channel;
    private String gameId;
    private List<Ship> strategy;
    private List<BoardPoint> shoots;
    private boolean autoPlay;
    private boolean ready;
    private Lang language;

    public Player(String name, Lang lang, WebSocket.Out<JsonNode> out, String id) {
        username = name;
        channel = out;
        gameId = id;
        language = lang;
        shoots = new LinkedList<BoardPoint>();
        autoPlay = false;
        ready = false;
    }

    public List<Ship> getStrategy() {
        return strategy;
    }

    public void setStrategy(List<Ship> strategy) {
        this.strategy = strategy;
        ready = true;
    }

    public Lang getLang() {
        return language;
    }

    public String getUsername() {
        return username;
    }

    public WebSocket.Out<JsonNode> getChannel() {
        return channel;
    }

    public String getGameId() {
        return gameId;
    }

    public List<BoardPoint> getShoots() {
        return shoots;
    }

    @Override
    public String toString() {
        return "Player{" + username + "}";
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isReady() {
        return ready;
    }
}
