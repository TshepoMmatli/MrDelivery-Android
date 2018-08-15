package com.tshepommatli.mrdelivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Tshepo on 2018/01/31.
 */

public class BackgroundWorker extends AsyncTask <String,Void,String>{

    Context ctx;
    public String email=null;
    AlertDialog dialog;
    public BackgroundWorker(Context ctx){
        this.ctx=ctx;
    }

    protected String doInBackground(String... params)
    {
        String ip=IP.getIp();
        String type = params[0];
        String login_url=ip+"login.php";
        String registration_url=ip+"registration.php";


        // Run f Registration Operation
        if(type.equals("register"))
        {
            try{
                String email_addrs = params[1];
                String firstname  = params[2];
                String lastname  = params[3];
                String password = params[4];
                URL url = new URL(registration_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream =httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("email_address", "UTF-8")+ "=" + URLEncoder.encode(email_addrs, "UTF-8") + "&"
                        +  URLEncoder.encode("password", "UTF-8")+ "=" + URLEncoder.encode(password, "UTF-8") + "&"
                        +  URLEncoder.encode("fname", "UTF-8")+ "=" + URLEncoder.encode(firstname, "UTF-8") + "&"
                        +  URLEncoder.encode("lname", "UTF-8")+ "=" + URLEncoder.encode(lastname, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    result = result + line;
                }
                bufferedReader.close();
                return result;
            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else if(type.equals("login")){
            try {
                String user = params[1];
                String pass = params[2];
                email=user;
                URL url = new URL(login_url);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setDoInput(true);
                OutputStream output = http.getOutputStream();
                BufferedWriter bWritter = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&"
                        + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
                bWritter.write(post_data);
                bWritter.flush();
                output.close();
                InputStream read = http.getInputStream();
                BufferedReader bRead = new BufferedReader(new InputStreamReader(read, "iso-8859-1"));
                String result="";
                String line = null;
                while ((line = bRead.readLine()) != null) {
                    result += line;
                }
                bRead.close();
                read.close();
                http.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        dialog= new AlertDialog.Builder(ctx).create();
        dialog.setTitle("Status");
    }

    @Override
    protected void onPostExecute(String result) {
        // if(typ!="profile") {
        dialog.setMessage(result);
        Toast.makeText(ctx.getApplicationContext(), result, Toast.LENGTH_LONG).show();

        if(result.toString().contains("Successful")){
            Intent intent = new Intent(ctx,MainActivity.class);
            intent.putExtra("email",email);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.getApplicationContext().startActivity(intent);
        }
        else if(result.toString().contains("Account created successfully.")) {
            Intent intent = new Intent(ctx, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.getApplicationContext().startActivity(intent);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
