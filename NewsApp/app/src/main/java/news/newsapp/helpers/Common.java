package news.newsapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Almodad on 3/6/2016.
 */
public class Common {

    private static ProgressDialog progressDialog;
    public static final String TAG_RESP_CODE= "respCode", TAG_USERID= "userid";

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.hide();
    }

}
