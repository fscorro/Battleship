package controllers;

import models.ConnectionHandler;
import org.codehaus.jackson.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;
import views.html.index;
import views.html.ranking;

import play.i18n.Lang;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    /**
     * Display the home page.
     */
    public static Result index() {
        return ok(index.render());
    }

    public static Result ranking(){
        return ok(ranking.render());
    }
  
    /**
     * Display the game room.
     */
    public static Result chatRoom(String username) {
        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        Language.getDefaultLang();
        return ok(chatRoom.render(username));
    }
    
    /**
     * Handle the game webSocket.
     */
    public static WebSocket<JsonNode> game(final String username) {
        final Lang lang = Language.getLang();
        return new WebSocket<JsonNode>() {
            // Called when the WebSocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                // Join the user to the Game.
                try {
                    ConnectionHandler.join(username, lang, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
  
}
