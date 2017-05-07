package news.newsapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by chebet on 5/15/2017.
 */
public class Config {
    public static final String GET_ALLARTICLES= "https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=31237352f46f46abb33ad579d66bdba3";
    public static final String GET_TECHNOLOGYSOURCES= "https://newsapi.org/v1/sources?category=technology";
    public static final String GET_TECHNOLOGYARTICLES= "https://newsapi.org/v1/articles?source=techcrunch&sortBy=top&apiKey=31237352f46f46abb33ad579d66bdba3";
    public static final String SAVING_USER= "http://www.rentpalace.co.ke/newsapp/retrievee.php?sid=1";
    public static final String CONFIRM_USER= "http://www.rentpalace.co.ke/newsapp/retrievee.php?sid=2";
    public static final String LOGIN_USER= "http://www.rentpalace.co.ke/newsapp/retrievee.php?sid=3";
    public static final String RESET_PASS= "http://www.rentpalace.co.ke/newsapp/retrievee.php?sid=4";
    public static final String CHANGE_PASS= "http://www.rentpalace.co.ke/newsapp/retrievee.php?sid=5";

    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "news";

    //This would be used to store the email of current logged in user
    public static final String USERID_SHARED_PREF = "userid";
    public static final String USERNAME_SHARED_PREF = "username";
    public static final String USERNUMBER_SHARED_PREF = "usernumber";
    public static final String USEREMAIL_SHARED_PREF = "usermail";
    public static final String CODE_SHARED_PREF = "code";
    public static final String PASS_SHARED_PREF = "password";
    public static final String DATECREATED_SHARED_PREF = "datecreated";


    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
