package rhis.sci.org.fwc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class main extends Activity {


    private boolean loginSuccess;

    public boolean isLoginSuccess() {
        return loginSuccess;
    }


    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginSuccess = false;
        final EditText passwdText = (EditText)findViewById(R.id.editTextPassword);
        final EditText providerText = (EditText)findViewById(R.id.editTextProviderId);
        Button loginButton = (Button)findViewById(R.id.buttonLogin);
        final TextView loginBanner = (TextView)findViewById(R.id.textViewBanner);

        //loginButton.setOnClickListener();
    }

    public void startLogin(View view) {
         /*new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String passwd = passwdText.getText().toString();
                 loginBanner.append(passwd);
                 System.out.println("Password is: " + passwd);
                 Intent geoIntent = new Intent(ServicesActivity.class);

                 // Use the Intent to start Google Maps application using Activity.startActivity()
                 startActivity(geoIntent);

             }
         }*/
        final EditText passwdText = (EditText)findViewById(R.id.editTextPassword);
        final EditText providerText = (EditText)findViewById(R.id.editTextProviderId);
        Button loginButton = (Button)findViewById(R.id.buttonLogin);
        final TextView loginBanner = (TextView)findViewById(R.id.textViewBanner);
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(providerText.getText().toString(), passwdText.getText().toString());

        if (passwdText.getText().toString().equalsIgnoreCase("123")) {
            Intent intent = new Intent(this, ServicesActivity.class);
            startActivity(intent);
        } else {
            ((TextView) findViewById(R.id.forgotPassLabel)).setText("Provider ID Password do not match");

            //return null;
        }

    }

     /*}*/

    private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String paramUserDetails = params[0];
            String paramPassword = params[1];
            String queryString = "{" +
                    "uid:" + params[0] + "," +
                    "upass:" + params[1] + "," +
                    "client:" + "android" +
                    "}";

            String queryString2 = "{sOpt:1,sStr:5833,providerid:6608}";

            System.out.println("*** doInBackground ** query: " + queryString);

            HttpClient httpClient = new DefaultHttpClient();

            // In a POST request, we don't pass the values in the URL.
            //Therefore we use only the web page URL as the parameter of the HttpPost argument
            HttpPost httpPost = new HttpPost("http://10.12.0.32:8080/RHIS/client");

            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
            //uniquely separate by the other end.
            //To achieve that we use BasicNameValuePair
            //Things we need to pass with the POST request
            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("sClient", queryString2);
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

            if (result != null ) {
                Toast.makeText(getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid POST req...", Toast.LENGTH_LONG).show();
            }

            System.out.println("Post Response: "+ result);
        }
    }

    private void sendPostRequest(String givenUsername, String givenPassword) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setContentView(R.layout.activity_main);
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
