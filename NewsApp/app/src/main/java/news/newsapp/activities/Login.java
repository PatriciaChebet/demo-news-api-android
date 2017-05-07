package news.newsapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import news.newsapp.R;
import news.newsapp.helpers.AppSingleton;
import news.newsapp.helpers.Common;
import news.newsapp.helpers.Config;

public class Login extends AppCompatActivity {
    TextView RegLink, ForgotPass;
    Button Login;
    EditText MobileNumber, Password, ResetMobile;
    String  mobilenumber, password, userid, username, usermail, usernumber, code, pass, status, datecreated, resetmobile;
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("News App Login");

        RegLink = (TextView) findViewById(R.id.link_register);
        ForgotPass =(TextView) findViewById(R.id.link_forgotpass);
        MobileNumber = (EditText) findViewById(R.id.edt_usernumber);
        Password = (EditText) findViewById(R.id.edt_password);
        Login = (Button) findViewById(R.id.btn_login);
        RegLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regintent = new Intent(Login.this, Register.class);
                startActivity(regintent);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobilenumber = MobileNumber.getText().toString();
                password = Password.getText().toString();
                if (!validdetails()) {
                    Snackbar.make(MobileNumber, "Invalid details", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (isNetworkAvailable()) {
                    loginUser();
                } else {
                    Snackbar.make(MobileNumber, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                final View foView = layoutInflaterAndroid.inflate(R.layout.reset_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                alertDialogBuilderUserInput.setView(foView);
                ResetMobile=(EditText) foView.findViewById(R.id.resetmobile);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                resetmobile=ResetMobile.getText().toString();
                                if (!validpass()) {
                                    Snackbar.make(ForgotPass, "Changing Password Failed", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if (isNetworkAvailable()) {
                                    ResetPassword();
                                } else {
                                    Snackbar.make(ForgotPass, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
    }
    public boolean validpass() {
        boolean valid = true;
        resetmobile=ResetMobile.getText().toString();
        if (resetmobile.isEmpty() || !Patterns.PHONE.matcher(resetmobile).matches()) {
            ResetMobile.setError("enter a valid mobile number");
            valid = false;
        } else {
            ResetMobile.setError(null);
        }
        return valid;
    }
    //method for checking the network state whether their is an Internet connection or not
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }
    //method to validating data entered
    public boolean validdetails() {
        boolean valid = true;
        mobilenumber = MobileNumber.getText().toString();
        password = Password.getText().toString();

        if (mobilenumber.isEmpty() || !Patterns.PHONE.matcher(mobilenumber).matches()) {
            MobileNumber.setError("enter a valid mobile number");
            valid = false;
        } else {
            MobileNumber.setError(null);
        }
        if (password.isEmpty()) {
            Password.setError("enter a valid password");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }
    //method to clear entered data on fields after it has been used
    private void clearFields(){
        MobileNumber.setText("");
        Password.setText("");
    }
    private void loginUser(){
        volleyClearCache();
        //Getting values from edit texts
        final String mobilenumber = MobileNumber.getText().toString().trim();
        final String password = Password.getText().toString().trim();

        String  REQUEST_TAG = "com.example.chebet.onsaleplots.volleyStringRequest";
        Common.showProgressDialog(Login.this, "Authenticating. Please wait...");
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            boolean error = responseObj.getBoolean("error");
                            String message = responseObj.getString("message");
                            //If we are getting success from server
                            if(!error){

                                JSONArray profileObj = responseObj.getJSONArray("user");

                                for (int i = 0; i < profileObj.length(); i++) {

                                    JSONObject person = (JSONObject) profileObj
                                            .get(i);
                                    userid=person.getString("userid");
                                    username = person.getString("username");
                                    usermail= person.getString("useremail");
                                    usernumber = person.getString("usernumber");
                                    code=person.getString("code");
                                    pass=person.getString("password");
                                    datecreated=person.getString("datecreated");
                                }
                                SharedPreferences sharedPreferences = Login.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                editor.putString(Config.USERID_SHARED_PREF, userid);
                                editor.putString(Config.USERNAME_SHARED_PREF, username);
                                editor.putString(Config.USERNUMBER_SHARED_PREF, usernumber);
                                editor.putString(Config.USEREMAIL_SHARED_PREF, usermail);
                                editor.putString(Config.CODE_SHARED_PREF, code);
                                editor.putString(Config.PASS_SHARED_PREF, pass);
                                editor.putString(Config.DATECREATED_SHARED_PREF, datecreated);
                                editor.commit();

                                clearFields();
                                Intent lointent = new Intent(Login.this, MainActivity.class);
                                startActivity(lointent);
                                finish();
                            }else{

                                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                                final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                                alertDialogBuilderUserInput.setView(foView);
                                TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                Success.setText("Mobile number and password do not match.");
                                alertDialogBuilderUserInput
                                        .setCancelable(false)
                                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.dismiss();
                                            }
                                        }).setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.dismiss();
                                            }
                                        });

                                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                alertDialogAndroid.show();
                                Button fbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_NEGATIVE);
                                fbutton.setTextColor(Color.BLACK);
                                Button pbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(Color.BLACK);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        Common.hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.e(TAG, "Error: " + error.getMessage());
                        Common.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("mobilenumber", mobilenumber);
                params.put("password", password);
                Log.e(TAG, "Posting params: " + params.toString());
                //returning parameter
                return params;
            }
        };

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);

    }
    public void volleyInvalidateCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().invalidate(url, true);
    }

    public void volleyDeleteCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().remove(url);
    }

    public void volleyClearCache(){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().clear();
    }
    private void ResetPassword(){
        //Getting values from edit texts
        volleyClearCache();
        final String resetemail=ResetMobile.getText().toString();

        String  REQUEST_TAG = "com.example.chebet.onsaleplots.volleyStringRequest";
        Common.showProgressDialog(Login.this, "Please wait...");
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RESET_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            boolean error = responseObj.getBoolean("error");
                            String message = responseObj.getString("message");
                            String status = responseObj.getString("success");
                            //If we are getting success from server
                            if(Integer.parseInt(status)== 2){
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("Your Password has been reset and has been sent to you.");
                                        alertDialogBuilderUserInput
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.dismiss();
                                                    }
                                                });

                                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                        alertDialogAndroid.show();
                                        Button pbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE);
                                        pbutton.setTextColor(Color.BLACK);
                                    }
                                });

                            }else if (Integer.parseInt(status)== 0){
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("Your Password could not be reset.");
                                        alertDialogBuilderUserInput
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.dismiss();
                                                    }
                                                });

                                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                        alertDialogAndroid.show();
                                        Button pbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE);
                                        pbutton.setTextColor(Color.BLACK);
                                    }
                                });
                            }else if (Integer.parseInt(status)== 3){
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("A user with that mobile number does not exist.");
                                        alertDialogBuilderUserInput
                                                .setCancelable(false)
                                                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        Intent goreg = new Intent(Login.this, Register.class);
                                                        startActivity(goreg);
                                                    }
                                                }).setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.dismiss();
                                                    }
                                                });

                                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                        alertDialogAndroid.show();
                                        Button fbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_NEGATIVE);
                                        fbutton.setTextColor(Color.BLACK);
                                        Button pbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE);
                                        pbutton.setTextColor(Color.BLACK);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            Snackbar.make(ForgotPass, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                        }
                        Common.hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.e(TAG, "Error: " + error.getMessage());
                        Snackbar.make(ForgotPass, "No internet connection.", Snackbar.LENGTH_SHORT).show();
                        Common.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("mobilenumber",resetmobile);
                Log.e(TAG, "Posting params: " + params.toString());
                //returning parameter
                return params;
            }
        };

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);

    }

}
