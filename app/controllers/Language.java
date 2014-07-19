package controllers;

import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

public class Language extends Controller {
    /**
* Tries to determine default language based on users accept-languages
*
* @return Integer: Number of language allowed in application's conf
*/
    public static int getDefaultLang() {
        if (session("play_user_lang") == null) {

            String[] allowedList = Play.application().configuration().getString("application.langs").split(",");
            List<Lang> accepted = request().acceptLanguages();

            String langCode = "";
            int langNumber = 0;
            boolean searchNext = true;
            for (Lang testLang : accepted) {
                if (searchNext) {
                    int testNumber = 0;
                    for (String s : allowedList) {
                        if (s.equals(testLang.code())) {
                            langCode = testLang.code();
                            langNumber = testNumber;
                            searchNext = false;
                        }
                        testNumber++;
                    }
                }
            }
            session("play_user_lang", langNumber + "");
            session("play_user_lang_code", langCode);
        }
        return Integer.parseInt(session("play_user_lang"));
    }

    public static String getDefaultLangCode(){
        if (session("play_user_lang_code") != null) {
            return session("play_user_lang_code");
        }
        return "en";
    }

    public static Lang getLang()
    {
        return Lang.forCode(getDefaultLangCode());
    }
}