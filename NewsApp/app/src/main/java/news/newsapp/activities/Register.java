package news.newsapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import news.newsapp.R;
import news.newsapp.helpers.Common;
import news.newsapp.helpers.Config;

public class Register extends AppCompatActivity {
    Button Signup;
    EditText Name, MobileNumber, Email, Password, ConfirmPassword;
    String name, mobilenumber, email, password,cpass;
    TextView LinkLogin;
    EditText Code;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("News App Registration");
        Name = (EditText) findViewById(R.id.input_username);
        MobileNumber = (EditText) findViewById(R.id.input_usernumber);
        Email = (EditText) findViewById(R.id.input_email);
        Password = (EditText) findViewById(R.id.input_password);
        ConfirmPassword = (EditText) findViewById(R.id.input_confirmpass);
        LinkLogin = (TextView) findViewById(R.id.link_register);
        Signup = (Button) findViewById(R.id.btn_signup);
        LinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prreg= new Intent(Register.this, Login.class);
                startActivity(prreg);
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                mobilenumber = MobileNumber.getText().toString();
                email = Email.getText().toString();
                password = Password.getText().toString();
                cpass = ConfirmPassword.getText().toString();
                if (!validdetails()) {
                    Snackbar.make(Name, "Invalid details", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (isNetworkAvailable()) {
                    if(password.equals(cpass)) {
                        addUser();
                    }else {
                        Snackbar.make(Name, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(Name, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }
    //method to validating data entered
    public boolean validdetails() {
        boolean valid = true;
        name = Name.getText().toString();
        mobilenumber = MobileNumber.getText().toString();
        email = Email.getText().toString();
        password = Password.getText().toString();
        if (name.isEmpty()) {
            Name.setError("enter your username");
            valid = false;
        } else {
            Name.setError(null);
        }
        if (mobilenumber.isEmpty() || !Patterns.PHONE.matcher(mobilenumber).matches()) {
            MobileNumber.setError("enter a valid mobile number");
            valid = false;
        } else {
            MobileNumber.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("enter a valid email");
            valid = false;
        } else {
            Email.setError(null);
        }
        if (password.isEmpty()) {
            Password.setError("enter a password");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }

    private void addUser() {
        //Showing the progress dialog
        Common.showProgressDialog(Register.this, "Saving details...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.SAVING_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
//                        loading.dismiss();
                        //Showing toast message of the response
                        Log.e("ADD USER", "Server Response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            //successful shopping
                            if (jObj.getString(Common.TAG_RESP_CODE).equals("0")) {
                                final String userid = jObj.getString(Common.TAG_USERID);

                                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Register.this);
                                final View foView = layoutInflaterAndroid.inflate(R.layout.confirm_dialog, null);
                                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Register.this);
                                alertDialogBuilderUserInput.setView(foView);

                                Code=(EditText) foView.findViewById(R.id.edt_code);


                                alertDialogBuilderUserInput
                                        .setCancelable(false)
                                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {

                                                code = Code.getText().toString();
                                                if (code.equals("")) {
                                                    Snackbar.make(Code, "Please enter the code we sent you.", Snackbar.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                if (isNetworkAvailable()) {
                                                    confirmUser(userid,code);
                                                } else {
                                                    Snackbar.make(Code, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.cancel();
                                                    }
                                                });

                                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                alertDialogAndroid.show();


                            }else if(jObj.getString(Common.TAG_RESP_CODE).equals("2")){

                                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Register.this);
                                final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Register.this);
                                alertDialogBuilderUserInput.setView(foView);
                                TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                Success.setText("Mobile number already registered with us.");
                                alertDialogBuilderUserInput
                                        .setCancelable(false)
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                               Intent gologin = new Intent(Register.this, Login.class);
                                                startActivity(gologin);
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
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setTitle("Failed to save details");
                                builder.setMessage("Your registration details was not saved.Please try again")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Common.hideProgressDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Common.hideProgressDialog();
                        //Showing toast
                        Snackbar.make(Name, "Sorry, network error! ", Snackbar.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = "";

                Map<String, String> params = new Hashtable<String, String>();

                params.put("username", name);
                params.put("mobilenumber", mobilenumber);
                params.put("email", email);
                params.put("password",password);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 3,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void confirmUser(final String userid, String confirmcode) {
        //Showing the progress dialog
        Common.showProgressDialog(Register.this, "Saving details...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONFIRM_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Showing  message of the response
                        Log.e("ADD USER", "Server Response: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            //check if successful registration
                            if (jObj.getString(Common.TAG_RESP_CODE).equals("0")) {
                                Intent go = new Intent(Register.this, Login.class);
                                startActivity(go);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setTitle("Failed to save details");
                                builder.setMessage("Your registration details was not saved.Please try again")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Common.hideProgressDialog();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Common.hideProgressDialog();
                        //Showing error
                        Snackbar.make(Name, "Sorry, network error! ", Snackbar.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = "";

                Map<String, String> params = new Hashtable<String, String>();

                params.put("userid", userid);
                params.put("confirmationcode", code);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 3,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
