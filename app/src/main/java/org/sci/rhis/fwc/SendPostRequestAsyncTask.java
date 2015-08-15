package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jamil.zaman on 13/08/2015.
 */
public class SendPostRequestAsyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        public SendPostRequestAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {

            String paramUserDetails = params[0];
            String paramPassword = params[1];
            /*String queryString = "{" +
                    "uid:" + params[0] + "," +
                    "upass:" + params[1] + "," +
                    "client:" + "android" +
                    "}";
            */
            String queryString = params[0];
            String servlet = params[1];
            String jsonRootkey = params[2];

            String queryString2 = "{sOpt:1,sStr:5833,providerid:6608}";

            System.out.println("*** doInBackground ** query: " + queryString);

            HttpClient httpClient = new DefaultHttpClient();

            // In a POST request, we don't pass the values in the URL.
            //Therefore we use only the web page URL as the parameter of the HttpPost argument
            HttpPost httpPost = new HttpPost("http://119.148.6.215:8080/RHIS/"+servlet);

            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
            //uniquely separate by the other end.
            //To achieve that we use BasicNameValuePair
            //Things we need to pass with the POST request
            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair(jsonRootkey, queryString);
            //BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("paramPassword", paramPassword);

            // We add the content that we want to pass with the POST request to as name-value pairs
            //Now we put those sending details to an ArrayList with type safe of NameValuePair
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(usernameBasicNameValuePair);
            //nameValuePairList.add(passwordBasicNameValuePAir);

            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                //This is typically useful while sending an HTTP POST request.
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                httpPost.setEntity(urlEncodedFormEntity);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    // According to the JAVA API, InputStream constructor do nothing.
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject json = new JSONObject(result);
                for ( Iterator<String> i = json.keys(); i.hasNext(); ) {

                    System.out.println("" + i.next());
                }
            } catch (JSONException jse) {
                System.out.println("JSON Exception Thrown:\n " );
                jse.printStackTrace();
            }

            if (result != null ) {
                Toast.makeText(context, "HTTP POST is working...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Invalid POST req...", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(context, SecondActivity.class);

            context.startActivity(intent);

            System.out.println("Post Response: " + result);
        }


}
