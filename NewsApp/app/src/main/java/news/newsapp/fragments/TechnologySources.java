package news.newsapp.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import news.newsapp.adapters.SourceAdapter;
import news.newsapp.helpers.AppSingleton;
import news.newsapp.helpers.Common;
import news.newsapp.helpers.Config;
import news.newsapp.models.ArticleItem;
import news.newsapp.models.SourceItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnologySources extends Fragment {
    public static RecyclerView lstTechSources;
    public static SourceAdapter tAdapter;

    private ProgressDialog pDialog;
    private static String KEY_SUCCESS = "status";
    JSONArray dec= null;
    ArrayList<SourceItem> tlist;
    SourceItem techSourceItem;
    private static final String TAG = "Freelancers";
    public static String id,name, description, url, category,language,country;
    private SwipeRefreshLayout refreshLayout;

    public static TechnologySources newInstance() {
        return new TechnologySources();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_technology_sources, container, false);
        refreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeContainer);
        tlist= new ArrayList<SourceItem>();
        lstTechSources= (RecyclerView) rootview.findViewById(R.id.lst_techsources);
        lstTechSources.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstTechSources.setLayoutManager(mLayoutManager);
        tAdapter = new SourceAdapter(getActivity(),tlist);
        lstTechSources.setAdapter(tAdapter);
        refreshLayout.setOnRefreshListener(onRefreshListener());
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorBlack);
        if (isNetworkAvailable()) {
            getTechnologySources();
        }else{
            Snackbar.make(lstTechSources, "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }


        return rootview;
    }
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    clear();
                    addAll(tlist);
                    getTechnologySources();
                    lstTechSources.setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(lstTechSources, "No Internet Connection", Snackbar.LENGTH_LONG).show();
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

    private void getTechnologySources(){
        Common.showProgressDialog(getActivity(), "Please wait . . .");
        volleyClearCache();
        String  REQUEST_TAG = "com.example.chebet.onsaleplots.volleyStringRequest";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.GET_TECHNOLOGYSOURCES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            String stat=responseObj.getString("status");
                            //If we are getting success from server

                            if(stat.equals("ok")) {
                                // parsing the user profile information
                                JSONArray profileObj = responseObj.getJSONArray("sources");
                                if (profileObj.length() > 0) {

                                    for (int i = 0; i < profileObj.length(); i++) {

                                        JSONObject sources = (JSONObject) profileObj
                                                .get(i);

                                        id = sources.getString("id");
                                        name = sources.getString("name");
                                        description = sources.getString("description");
                                        url = sources.getString("url");
                                        category = sources.getString("category");
                                        language= sources.getString("language");
                                        country = sources.getString("country");

                                        techSourceItem = new SourceItem();
                                        techSourceItem.setId(id);
                                        techSourceItem.setName(name);
                                        techSourceItem.setDescription(description);
                                        techSourceItem.setUrl(url);
                                        techSourceItem.setCategory(category);
                                        techSourceItem.setLanguage(language);
                                        techSourceItem.setCountry(country);



                                        tlist.add(techSourceItem);


                                    }

                                    tAdapter = new SourceAdapter(getActivity(), tlist);
                                    lstTechSources.setAdapter(tAdapter);

                                    if (refreshLayout.isRefreshing()) {
                                        refreshLayout.setRefreshing(false);
                                    }
                                    lstTechSources.setVisibility(View.VISIBLE);
                                }else{

                                    Snackbar.make(lstTechSources, "No articles listed ", Snackbar.LENGTH_LONG).show();
                                }
                            }else{

                                Snackbar.make(lstTechSources, "No articles listed ", Snackbar.LENGTH_LONG).show();
                            }



                        } catch (JSONException e) {
                            Log.e(TAG, "Error show: " + e.getMessage());
                            Snackbar.make(lstTechSources, "Network error ", Snackbar.LENGTH_LONG).show();
                        }
                        Common.hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //You can handle error here if you want
                        Log.e(TAG, "Error: " + volleyError.getMessage());
                        Snackbar.make(lstTechSources, "No Internet Connection ", Snackbar.LENGTH_LONG).show();

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
        tlist.clear();
        tAdapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<SourceItem> tlist) {
        tlist.addAll(tlist);
        tAdapter.notifyDataSetChanged();
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
