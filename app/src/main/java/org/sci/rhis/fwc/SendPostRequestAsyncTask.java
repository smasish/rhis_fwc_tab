package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by jamil.zaman on 13/08/2015.
 */
public class SendPostRequestAsyncTask extends AsyncTask<String, Void, String> {

    private Activity activity;
    private AsyncCallback originalRequest;
    private  final String LOGTAG    = "FWC-ASYNC-TASK-NET";
    private final int READ_TIMEOUT = 15000;
    private final int CONNECTION_TIMEOUT = 15000;
    protected Activity getActivity() { return activity; }
    public void setActivity(Activity activity) {this.activity = activity;}

    protected Context getContext() { return activity; }
    public SendPostRequestAsyncTask() {
        this.activity = null;
    }

    public SendPostRequestAsyncTask(AsyncCallback origin) {
        originalRequest = origin;
    }

    @Override
    protected String doInBackground(String... params) {

        String queryString = params[0];
        String servlet = params[1];
        String jsonRootkey = params[2];

        Log.i(LOGTAG, "Sending JSON:\t" + queryString);

        Log.i(LOGTAG, "RootKey:\t"+jsonRootkey + "\tServlet:\t" + servlet);

        // In a POST request, we don't pass the values in the URL.
        //Therefore we use only the web page URL as the parameter of the HttpPost argument

        URL url;
        try {
            url = new URL//("http://119.148.6.215:8080/RHIS_DEV/"+servlet);
            //("http://119.148.6.215:8080/RHIS_BETA/"+servlet);
            ("http://10.12.6.138:8080/RHIS_WEB_Armaan/"+servlet);
            //("http://119.148.6.215:8080/RHISv2/"+servlet);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer =
                new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonRootkey + "="+  queryString);

            writer.flush();
            writer.close();
            os.close();
            //TODO - could have been oneline
            int responseCode=conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null; //if not OK then return null
            }

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                stringBuilder.append(bufferedStrChunk);
            }
            return stringBuilder.toString();

        } catch(MalformedURLException mul) {
            Log.e(LOGTAG, "URL Malformed Error");
            Utilities.printTrace(mul.getStackTrace());
        } catch (ProtocolException pe) {
            Log.e(LOGTAG, "Protocol Error ");
            Utilities.printTrace(pe.getStackTrace());
        } catch (IOException io) {
            Log.e(LOGTAG, "IO Error ");
            Utilities.printTrace(io.getStackTrace());

        } catch (Exception e) {
            Log.e(LOGTAG, "Unknown Error ");
            Utilities.printTrace(e.getStackTrace());
        }
        return null;
    }

    protected void onProgressUpdate(Integer... values) {
        Toast.makeText(activity, "Network Operation In Progress ...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if (result != null) {
                //Toast.makeText(activity, "HTTP POST is working...", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(activity, "Invalid POST req...", Toast.LENGTH_LONG).show();
            }
            originalRequest.callbackAsyncTask(result);
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            e.printStackTrace();
        }
    }
}
