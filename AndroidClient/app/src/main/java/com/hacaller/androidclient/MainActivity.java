package com.hacaller.androidclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends Activity {

    static WeakReference<TextView> textviewLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tLogger = findViewById(R.id.textviewLogger);
        textviewLogger = new WeakReference<>(tLogger);
        Button buttonGetJson = findViewById(R.id.buttonGetJson);
        Button buttonPostJson = findViewById(R.id.buttonPostJson);
        Button buttonGetXml = findViewById(R.id.buttonGetXml);
        Button buttonPostXml = findViewById(R.id.buttonPostXml);

        buttonGetJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestAsyncTask().execute(RequestAsyncTask.JSON_GET_REQUEST);
            }
        });

        buttonPostJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestAsyncTask().execute(RequestAsyncTask.JSON_POST_REQUEST);
            }
        });

        buttonGetXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestAsyncTask().execute(RequestAsyncTask.XML_GET_REQUEST);
            }
        });

        buttonPostXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestAsyncTask().execute(RequestAsyncTask.XML_POST_REQUEST);
            }
        });

    }


    public static class RequestAsyncTask extends AsyncTask<Integer,Void,String>{

        public static final int JSON_GET_REQUEST = 0;
        public static final int JSON_POST_REQUEST = 1;
        public static final int XML_GET_REQUEST = 2;
        public static final int XML_POST_REQUEST = 3;

        @Override
        protected String doInBackground(Integer... integers) {
            String message = "";
            try {
                String targetUrl = "http://localhost:3000";
                String request = "GET";
                String contentType = "application/json";
                switch (integers[0]) {
                    case JSON_GET_REQUEST:
                        targetUrl = "http://localhost:3000/data.json";
                        request = "GET";
                        contentType = "application/json";
                        break;
                    case JSON_POST_REQUEST:
                        targetUrl = "http://localhost:3000/data.json";
                        request = "POST";
                        contentType = "application/json";
                        break;
                    case XML_GET_REQUEST:
                        targetUrl = "http://localhost:3000/data.xml";
                        request = "GET";
                        contentType = "application/xml";
                        break;
                    case XML_POST_REQUEST:
                        targetUrl = "http://localhost:3000/data.xml";
                        request = "POST";
                        contentType = "application/xml";
                        break;
                }
                URL url = new URL(targetUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(request);
                //connection.addRequestProperty("Content-Type",contentType);
                //connection.addRequestProperty("Accept-Type",contentType);
                if (request.contains("POST")) {
                    //connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();
                    OutputStream out = connection.getOutputStream();
                    String body = "{\"date\":\"" + new Date().toString() + "\",\"user\":\"Guest\"}";
                    out.write(body.getBytes());
                    out.flush();
                    out.close();
                } else {
                    connection.connect();
                }
                // Assuming ASCII or ISO-8895-1: 1 byte per char
                byte[] bytes = new byte[2048];
                InputStream in = connection.getInputStream();
                in.read(bytes, 0, 2048);
                message = new String(bytes);
                Log.d(this.toString(),message);
                connection.disconnect();

            } catch (Exception e){}
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView textView = textviewLogger.get();
            if (textView != null)
                textView.setText(s);
        }
    }
}
