package com.hacaller.androidserver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class MainActivity extends Activity {

    ServerThread serverThread;
    TextView textviewLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.buttonStart);
        textviewLogger = findViewById(R.id.textviewLogger);

        serverThread = new ServerThread();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverThread.start();
            }
        });

    }

    public class ServerThread extends Thread {

        @Override
        public void run() {
            int port = 3000;
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverLog(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    InputStream in = clientSocket.getInputStream();

                    OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream());
                    BufferedWriter outBuffer = new BufferedWriter(out);

                    byte[] bytes = new byte[2048];
                    in.read(bytes, 0, 2048);
                    String request = new String(bytes);
                    request = request.trim();
                    String[] splitRequest = request.split("\r\n\r\n");
                    if (splitRequest.length > 1) {
                        readURLPath(splitRequest[0], splitRequest[1], outBuffer);
                    } else {
                        readURLPath(splitRequest[0], "", outBuffer);
                    }
                    out.write(new Date().toString());
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void serverLog(final int port){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textviewLogger.setText("Server at port " + String.valueOf(port));
                }
            });
        }


    }



    private void readURLPath(String header, String body, BufferedWriter out) {

        try {
            if (header.startsWith("GET /data.json")) {
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: application/json\r\n");
                out.write("\r\n");
                out.write("{ \"data\": \"Hello World\"}");
                out.flush();
            } else if (header.startsWith("POST /data.json")) {
                JSONObject json = new JSONObject(body);
                String date = json.get("date").toString();
                String user = json.get("user").toString();
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: application/json\r\n");
                out.write("\r\n");
                out.write("{ \"message\": \"Hello " + user + ", today is " + date + "\"}");
                out.flush();
            } else if (header.startsWith("GET /data.xml")) {
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: application/xml\r\n");
                out.write("\r\n");
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                "<data>Hello World</data>");
                out.flush();
            } else if (header.startsWith("POST /data.xml")) {
                JSONObject json = new JSONObject(body);
                String date = json.get("date").toString();
                String user = json.get("user").toString();
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: application/xml\r\n");
                out.write("\r\n");
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<message>Hello " + user + ", today is " + date + "</message>");
                out.flush();
            } else if (header.startsWith("GET")) {
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("\r\n");
                out.write(new Date().toString());
                out.flush();
            }
        } catch (IOException e) {
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
