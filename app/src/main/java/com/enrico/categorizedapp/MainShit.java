package com.enrico.categorizedapp;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainShit extends Activity {

    //https://stackoverflow.com/questions/10710442/how-to-get-category-for-each-app-on-device-on-android

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR = "Error: apps not on PlayStore";

    List<String> returned = new ArrayList<>();

    RecyclerView recyclerView;

    private List<ApplicationInfo> getInstalledApps() {

        //get a list of installed apps.
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> installedApps = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        List<ApplicationInfo> returnedLaunchers = new ArrayList<>();

        for (ResolveInfo ri : installedApps) {
            ApplicationInfo applicationInfo;

            try {
                applicationInfo = packageManager.getApplicationInfo(ri.activityInfo.packageName, 0);

                returnedLaunchers.add(applicationInfo);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return returnedLaunchers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        recyclerView = findViewById(R.id.categories_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new FetchCategoryTask().execute();
    }


    private List<String> categories(String category) {

        returned.add(category);

        Set<String> hs = new HashSet<>();

        hs.addAll(returned);
        returned.clear();
        returned.addAll(hs);

        Collections.sort(returned, new NameComparator());

        return returned;
    }

    //method to init recycler view
    private void setRecyclerView() {

        //set the recycler view adapter and pass arguments to the adapter to it
        recyclerView.setAdapter(new RecyclerViewAdapter(this, returned));
    }

    //check if there is an internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class NameComparator implements Comparator<String> {

        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    private class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();

        View loadingView;
        Animator anim;

        @Override
        protected void onPreExecute() {
            loadingView = findViewById(R.id.loading);
            anim = AnimatorInflater
                    .loadAnimator(MainShit.this, R.animator.flip);
            anim.setTarget(loadingView);
            anim.start();
        }

        @Override
        protected Void doInBackground(Void... errors) {

            List<ApplicationInfo> packages = getInstalledApps();

            for (int i = 0; i < packages.size(); i++) {

                ApplicationInfo ai = packages.get(i);

                String packageName = ai.packageName;

                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    categories("System");
                } else {
                    String query_url = GOOGLE_URL + packageName;
                    Log.i(TAG, query_url);

                    categories(getAppForCategory(query_url));

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            anim.cancel();
            loadingView.setVisibility(View.GONE);
            setRecyclerView();

        }

        private String getAppForCategory(String query_url) {
            boolean network = isNetworkAvailable();
            if (!network) {
                //manage connectivity lost
                return ERROR;
            } else {
                try {
                    Document doc = Jsoup.connect(query_url).get();
                    Element link = doc.select("span[itemprop=genre]").first();
                    return link.text();

                } catch (Exception e) {
                    return ERROR;
                }
            }
        }
    }
}
