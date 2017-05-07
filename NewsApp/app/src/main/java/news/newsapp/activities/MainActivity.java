package news.newsapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import news.newsapp.R;
import news.newsapp.fragments.AllArticles;
import news.newsapp.fragments.SourceTechArticles;
import news.newsapp.fragments.TechnologySources;
import news.newsapp.helpers.AppSingleton;
import news.newsapp.helpers.Common;
import news.newsapp.helpers.Config;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private int mCurrentSelectedPosition, tabsLoaded;
    TextView Title;
    private Menu mOptionsMenu;
    EditText PassMobile, CurrentPassword, NewPassword, NewPasswordAgain;
    String passmobile, currenpass, newpass, newpassagain;
    private static final String TAG = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view) ;
        setUpNavigationDrawer();
        mNavigationView.setCheckedItem(R.id.navigation_allarticles);
        View header=getLayoutInflater().inflate(R.layout.drawer_header, null);
        Title= (TextView) header.findViewById(R.id.title);
        Title.setText(R.string.title);
        mNavigationView.addHeaderView(header);


    }
    private void setUpNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = getSupportActionBar();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        } catch (Exception ignored) {
        }

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_allarticles:
                        mCurrentSelectedPosition = 0;
                        getSupportActionBar().setTitle(menuItem.getTitle());
                        showAllArticles();
                        break;
                    case R.id.navigation_techsources:
                        mCurrentSelectedPosition = 1;
                        getSupportActionBar().setTitle(menuItem.getTitle());
                        showTechSources();
                        break;
                    case R.id.navigation_similarsourcetech:
                        mCurrentSelectedPosition = 2;
                        getSupportActionBar().setTitle(menuItem.getTitle());
                        showTechnologyArticles();
                        break;
                }

                mDrawerLayout.closeDrawer(mNavigationView);
                return true;
            }

        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        showAllArticles();
    }
    private void showAllArticles(){
        if (isNetworkAvailable()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new AllArticles().newInstance());
            fragmentTransaction.commit();
        } else {
            Snackbar.make(mNavigationView, "No internet Connection", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void showTechSources(){
        if (isNetworkAvailable()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new TechnologySources().newInstance());
            fragmentTransaction.commit();
        } else {
            Snackbar.make(mNavigationView, "No internet Connection", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void showTechnologyArticles(){
        if (isNetworkAvailable()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new SourceTechArticles().newInstance());
            fragmentTransaction.commit();
        } else {
            Snackbar.make(mNavigationView, "No internet Connection", Snackbar.LENGTH_SHORT).show();
        }
    }
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

    private final int timeinterval=2000; //time in milisecs
    private  long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            if (mBackPressed + timeinterval > System.currentTimeMillis()){
                super.onBackPressed();
            }else{
                Snackbar.make(mDrawerLayout, "Press back again to exit", Snackbar.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage, menu);
        mOptionsMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);
            } else {
                mDrawerLayout.openDrawer(mNavigationView);
            }
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_changepass:
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                final View foView = layoutInflaterAndroid.inflate(R.layout.changepass_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilderUserInput.setView(foView);
                PassMobile=(EditText) foView.findViewById(R.id.editmobile);
                CurrentPassword = (EditText) foView.findViewById(R.id.editoldpass);
                NewPassword = (EditText) foView.findViewById(R.id.editnewpass);
                NewPasswordAgain = (EditText) foView.findViewById(R.id.editnewpassagain);
                SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                passmobile=sharedPref.getString(Config.USERNUMBER_SHARED_PREF, "");
                PassMobile.setText(passmobile);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                passmobile=PassMobile.getText().toString();
                                currenpass = CurrentPassword.getText().toString();
                                newpass = NewPassword.getText().toString();
                                newpassagain = NewPasswordAgain.getText().toString();
                                if (!validpass()) {
                                    Snackbar.make(PassMobile, "Changing Password Failed", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if (isNetworkAvailable()) {
                                    if (newpass.equals(newpassagain)){
                                        ChangePassword();
                                    }else{
                                        Snackbar.make(PassMobile, "New Passwords do not match", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(PassMobile, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
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
                break;
            case R.id.action_logout:
                LayoutInflater layoutInflaterApp = LayoutInflater.from(MainActivity.this);
                final View mView = layoutInflaterApp.inflate(R.layout.statedialog, null);
                AlertDialog.Builder alertDialogBuilderUserInpu = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilderUserInpu.setView(mView);
                final TextView Success = (TextView) mView.findViewById(R.id.txt_success);
                Success.setText("Are you sure you want to log out.");
                alertDialogBuilderUserInpu
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                                editor.putString(Config.USERID_SHARED_PREF, "");
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

                AlertDialog alertDialogApp= alertDialogBuilderUserInpu.create();
                alertDialogApp.show();
                break;

        }
        return  super.onOptionsItemSelected(item);
    }


    public boolean validpass() {
        boolean valid = true;
        String passmobile=PassMobile.getText().toString();
        String currentpass = CurrentPassword.getText().toString();
        String newpass = NewPassword.getText().toString();
        String newpassagain = NewPasswordAgain.getText().toString();
        if (passmobile.isEmpty() || !Patterns.PHONE.matcher(passmobile).matches()) {
            PassMobile.setError("enter a valid email address");
            valid = false;
        } else {
            PassMobile.setError(null);
        }
        if (currentpass.isEmpty() ) {
            CurrentPassword.setError("enter your current password");
            valid = false;
        } else {
            CurrentPassword.setError(null);
        }
        if (newpass.isEmpty()) {
            NewPassword.setError("enter a new password");
            valid = false;
        } else {
            NewPassword.setError(null);
        }
        if (newpassagain.isEmpty()) {
            NewPasswordAgain.setError("confirm the new password");
            valid = false;
        } else {
            NewPasswordAgain.setError(null);
        }

        return valid;
    }
    private void ChangePassword(){
        //Getting values from edit texts
        volleyClearCache();
        final String passmobile=PassMobile.getText().toString();
        final String currentpass = CurrentPassword.getText().toString();
        final String newpass = NewPassword.getText().toString();

        String  REQUEST_TAG = "com.example.chebet.onsaleplots.volleyStringRequest";
        Common.showProgressDialog(MainActivity.this, "Please wait...");
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHANGE_PASS,
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
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("Your Password has been changed.");
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
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("Your Password could not be changed.");
                                        alertDialogBuilderUserInput
                                                .setCancelable(false)
                                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        if (!validpass()) {
                                                            Snackbar.make(PassMobile, "Changing Password Failed", Snackbar.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if (isNetworkAvailable()) {
                                                            if (newpass.equals(newpassagain)){
                                                                ChangePassword();
                                                            }else{
                                                                Snackbar.make(PassMobile, "New Passwords do not match", Snackbar.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Snackbar.make(PassMobile, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                                                        }

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
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                                        final View foView = layoutInflaterAndroid.inflate(R.layout.statedialog, null);
                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
                                        alertDialogBuilderUserInput.setView(foView);
                                        TextView Success=(TextView) foView.findViewById(R.id.txt_success);
                                        Success.setText("Your old password is incorect.");
                                        alertDialogBuilderUserInput
                                                .setCancelable(false)
                                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
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
                            }

                        } catch (JSONException e) {
                            Snackbar.make(PassMobile, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                        }
                        Common.hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Log.e(TAG, "Error: " + error.getMessage());
                        Snackbar.make(PassMobile, "No internet connection.", Snackbar.LENGTH_SHORT).show();
                        Common.hideProgressDialog();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("mobilenumber",passmobile);
                params.put("currentpass",currentpass );
                params.put("newpass", newpass);
                Log.e(TAG, "Posting params: " + params.toString());
                //returning parameter
                return params;
            }
        };

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);

    }
    public void volleyCacheRequest(String url){
        Cache cache = AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache();
        Cache.Entry reqEntry = cache.get(url);
        if(reqEntry != null){
            try {
                String data = new String(reqEntry.data, "UTF-8");
                //Handle the Data here.
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else{


        }
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
}
