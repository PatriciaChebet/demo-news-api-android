package news.newsapp.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import news.newsapp.R;
import news.newsapp.adapters.ArticleAdapter;
import news.newsapp.helpers.AppSingleton;
import news.newsapp.helpers.Common;
import news.newsapp.helpers.Config;
import news.newsapp.models.ArticleItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllArticles extends Fragment {
    public static RecyclerView lstAllArticles;
    public static ArticleAdapter cAdapter;

    private ProgressDialog pDialog;
    private static String KEY_SUCCESS = "status";
    JSONArray dec= null;
    ArrayList<ArticleItem> alist;
    ArticleItem allArticleItem;
    private static final String TAG = "Freelancers";
    public static String author,title, description, url, urlToImage,publishedAt;
    private SwipeRefreshLayout refreshLayout;


    public static AllArticles newInstance() {
        return new AllArticles();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_all_articles, container, false);
        refreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeContainer);
        alist= new ArrayList<ArticleItem>();
        lstAllArticles= (RecyclerView) rootview.findViewById(R.id.lst_allarticles);
        lstAllArticles.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstAllArticles.setLayoutManager(mLayoutManager);
        cAdapter = new ArticleAdapter(getActivity(),alist);
        lstAllArticles.setAdapter(cAdapter);
        refreshLayout.setOnRefreshListener(onRefreshListener());
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorBlack);
        if (isNetworkAvailable()) {
            getAllArticles();
        }else{
            Snackbar.make(lstAllArticles, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }


        return rootview;
    }
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    clear();
                    addAll(alist);
                    getAllArticles();
                    lstAllArticles.setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(lstAllArticles, "No Internet Connection", Snackbar.LENGTH_LONG).show();
                }

            }
        };
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void getAllArticles(){
        Common.showProgressDialog(getActivity(), "Please wait . . .");
        volleyClearCache();
        String  REQUEST_TAG = "com.example.chebet.onsaleplots.volleyStringRequest";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.GET_ALLARTICLES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            String stat=responseObj.getString("status");
                            //If we are getting success from server

                            if(stat.equals("ok")) {
                                // parsing the details on each articles
                                JSONArray profileObj = responseObj.getJSONArray("articles");
                                if (profileObj.length() > 0) {

                                    for (int i = 0; i < profileObj.length(); i++) {

                                        JSONObject srevs = (JSONObject) profileObj
                                                .get(i);

                                        author = srevs.getString("author");
                                        title = srevs.getString("title");
                                        description = srevs.getString("description");
                                        url = srevs.getString("url");
                                        urlToImage = srevs.getString("urlToImage");
                                        publishedAt = srevs.getString("publishedAt");

                                        allArticleItem = new ArticleItem();
                                        allArticleItem.setAuthor(author);
                                        allArticleItem.setTitle(title);
                                        allArticleItem.setDescription(description);
                                        allArticleItem.setUrl(url);
                                        allArticleItem.setUrlToImage(urlToImage);
                                        allArticleItem.setPublishedAt(publishedAt);



                                        alist.add(allArticleItem);


                                    }

                                    cAdapter = new ArticleAdapter(getActivity(), alist);
                                    lstAllArticles.setAdapter(cAdapter);

                                    if (refreshLayout.isRefreshing()) {
                                        refreshLayout.setRefreshing(false);
                                    }
                                    lstAllArticles.setVisibility(View.VISIBLE);
                                }else{
                                    Snackbar.make(lstAllArticles, "No articles listed ", Snackbar.LENGTH_LONG).show();
                                }
                            }else{

                                Snackbar.make(lstAllArticles, "No articles listed ", Snackbar.LENGTH_LONG).show();
                            }



                        } catch (JSONException e) {
                            Log.e(TAG, "Error show: " + e.getMessage());
                            Snackbar.make(lstAllArticles, "Network error ", Snackbar.LENGTH_LONG).show();
                        }
                        Common.hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //You can handle error here if you want
                        Log.e(TAG, "Error: " + volleyError.getMessage());
                        Snackbar.make(lstAllArticles, "No Internet Connection ", Snackbar.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 3,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void clear() {
        alist.clear();
        cAdapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<ArticleItem> alist) {
        alist.addAll(alist);
        cAdapter.notifyDataSetChanged();
    }
    public void volleyCacheRequest(String url){
        Cache cache = AppSingleton.getInstance(getContext()).getRequestQueue().getCache();
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
        AppSingleton.getInstance(getContext()).getRequestQueue().getCache().invalidate(url, true);
    }

    public void volleyDeleteCache(String url){
        AppSingleton.getInstance(getContext()).getRequestQueue().getCache().remove(url);
    }

    public void volleyClearCache(){
        AppSingleton.getInstance(getContext()).getRequestQueue().getCache().clear();
    }

}


