package controllers;

import play.i18n.Messages;
import play.i18n.Lang;

import java.io.*;

import play.mvc.Controller;
import play.mvc.Result;

public class JavaScriptController extends Controller {
    public static Result i18n(){
        Lang l = request().acceptLanguages().get(0);
        String properties = "";
        // you can also use commons-io
        try{
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("messages." + l.code().substring(0,2));
            properties = new java.util.Scanner(in).useDelimiter("\\A").next();
        } catch (Exception e) {
            
        }

        return ok(properties).as("text/plain");
    }
}