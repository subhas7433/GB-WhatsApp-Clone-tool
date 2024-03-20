package com.affixstudio.whatsapptool.modelOur;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData {

    ApiResponse apiResponse;

    public GetData(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
    }

    @SuppressLint("StaticFieldLeak")
    public  void Start(String urlString)
    {


        task task=new task();
        task.urlString=urlString;
        task.apiResponse=apiResponse;
        task.execute();


    }



    static class task extends AsyncTask<String, String, String> {

        StringBuffer content = new StringBuffer();
        String urlString;
        ApiResponse apiResponse;


        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(urlString.replace(" ","%20"));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();


            } catch (Exception e) {
                e.printStackTrace();
                apiResponse.response("100");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            apiResponse.response(content.toString());
            super.onPostExecute(s);
        }
    }

    public  void StartPost(String urlString,String parameter)
    {


        taskPost task=new taskPost();
        task.parameter=parameter;
        task.urlString=urlString;
        task.apiResponse=apiResponse;
        task.execute();


    }
    static class taskPost extends AsyncTask<String, String, String>{

        StringBuffer content = new StringBuffer();
        String urlString;
        String parameter;
        ApiResponse apiResponse;


        @Override
        protected String doInBackground(String... strings) {
            try {

                // Set up the URL and the POST parameters
                URL url = new URL(urlString);


                // Open the connection and set up the request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Write the POST parameters to the request body
                OutputStream os = conn.getOutputStream();
                os.write(parameter.getBytes());
                os.flush();
                os.close();

                // Read the response from the server
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
                br.close();

                // Disconnect the connection
                conn.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            apiResponse.response(content.toString());
            super.onPostExecute(s);
        }
    }

}
