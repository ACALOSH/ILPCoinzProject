package com.alosh.anna.coinz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    /*
    public class DownloadCompleteRunner {
        static String result;

        public static void downloadComplete(String result) {
            DownloadCompleteRunner.result = result;
        }
    }

    public class DownloadFileTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String...urls){
            try{
                return loadFileFromNetwork(urls[0]);
            } catch(IOException e) {
                return "Unable to load content. Check your network connection.";
            }
        }
        private  String loadFileFromNetwork(String urlString) throws IOException{
            return readStream(downloadUrl(new URL(urlString)));
        }
        private InputStream downloadURL(URL url) throws IOException{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }
        @NonNull
        private String readStream(InputStream stream)
            throws IOException{

        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            DownloadCompleteRunner.downloadComplete(result);
        }

    }
    */


