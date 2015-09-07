package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends FWCServiceActivity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set deafult bang;a font
        //Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"Nikosh.ttf");
        //TextView textview = (TextView)findViewById(R.id.providerLabelId);
        //textview.setTypeface(tf);
        DatabaseFieldMapping.InitializeClientIntroduction();
        DatabaseFieldMapping.InitializeClientInformation();

    }

    public void startLogin(View view) {
        final EditText passwdText = (EditText)findViewById(R.id.providerPassword);
        final EditText providerText = (EditText)findViewById(R.id.providerId);

        //Button loginButton = (Button)findViewById(R.id.buttonLogin);
        //final TextView loginBanner = (TextView)findViewById(R.id.textViewBanner);
        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(this);
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(this);
        String queryString =   "{" +
                "uid:" + providerText.getText().toString() + "," +
                "upass:" + passwdText.getText().toString() + "," +
                "client:" + "android" +
                "}";
        String servlet = "login";
        String jsonRootkey = "loginInfo";
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);
    }

    @Override
    public void callbackAsyncTask(String result) {
        try {
            JSONObject json = new JSONObject(result);
            for ( Iterator<String> i = json.keys(); i.hasNext(); ) {
                System.out.println("" + i.next());
            }

            if(json.getBoolean("loginStatus")) { //if successful login
                //first create the provider object
                Log.d("++++++++++","-----"+json.getString("ProvName"));
                ProviderInfo provider = ProviderInfo.getProvider();
                provider.setProviderName(json.getString("ProvName"));
                provider.setProviderCode(json.getString("ProvCode"));
                provider.setProviderFacility(json.getString("FacilityName"));
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                System.out.println("Post Response: " + result);
            } else {
                //todo: displaya red colored text view that log in failed.
                Toast.makeText(this, "Login Failed ...", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }
}