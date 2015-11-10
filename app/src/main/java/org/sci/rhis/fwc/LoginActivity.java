package org.sci.rhis.fwc;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends FWCServiceActivity {

    Button button;
    int placeIndex=0;
    String placeName="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

     // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //Set deafult bangla font
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
        //ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarLogin);

       AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(this);
        //SendPostRequestAsyncTask
        //AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(this);
        //sendPostReqAsyncTask.setP
        String queryString =   "{" +
                "uid:" + providerText.getText().toString() + "," +
                "upass:" + passwdText.getText().toString() + "," +
                "client:" + "android" +
                "}";
        String servlet = "login";
        String jsonRootkey = "loginInfo";
        client.execute(queryString, servlet, jsonRootkey);

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
                Log.d("++++++++++", "-----" + json.getString("ProvName"));
                ProviderInfo provider = ProviderInfo.getProvider();
                provider.setProviderName(json.getString("ProvName"));
                provider.setProviderCode(json.getString("ProvCode"));
                provider.setProviderFacility(json.getString("FacilityName"));

                popUpDialog();

                Log.d("aaf",""+provider.getProviderFacility());
                System.out.println("Post Response: " + result);
            } else {
                //todo: display a red colored text view that log in failed.
                Toast.makeText(this, "Login Failed ...", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }

    public void popUpDialog() {

        final Dialog dialog = new Dialog(this);

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.place_pop);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w=dm.widthPixels;
        int h=dm.heightPixels;

        dialog.getWindow().setLayout((int) (w * 0.7), (int) (h * 0.34));


        final Button ok=(Button)dialog.findViewById(R.id.buttonPlacePopUpOK);
        final Button cancel=(Button)dialog.findViewById(R.id.buttonPlacePopUpCancel);
        final RadioGroup RadioPlaceGroupListener = (RadioGroup) dialog.findViewById(R.id.radioGroupPlace);
        final EditText SatelliteName=(EditText)dialog.findViewById(R.id.SatelliteName);


            RadioPlaceGroupListener.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.radioButtonSatellite) {
                        placeIndex =1;
                        SatelliteName.setVisibility(EditText.VISIBLE);

                    }

                    else {
                        placeIndex =0;
                        SatelliteName.setVisibility(EditText.GONE);
                    }
                }
            });

        ok.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                //to get the place of service
                //  0 facility
                //  1 satellite

                Log.d("placeIndex", String.valueOf(placeIndex));
                if (placeIndex ==1) {
                    placeName = SatelliteName.getText().toString();
                }

                else if (placeIndex ==0){
                    placeName = "";

                }

                Log.d("placeName", placeName);

                startSecondActivity();
                dialog.dismiss();

            }
        });


        cancel.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    public void startSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}


