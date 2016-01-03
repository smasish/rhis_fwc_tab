package org.sci.rhis.fwc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class LoginActivity extends FWCServiceActivity {

    private Button button;
    private int placeIndex=0;
    private String placeName="";
    TextView forgetPassword = null;
    private static boolean fileLoaded = false;


    class FileLoader extends AsyncTask<String, Integer, Integer> {
        Context context;
        FileLoader(Context c) {context = c;};
        protected Integer doInBackground(String... params) {
            loadLocations();
            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            allowNextActivity();
            //getSpinner(R.id.advSearchDistrict).setAdapter(zillaAdapter);
            //loadVillages.setVisibility(View.GONE);
        }

        protected void onProgressUpdate (Integer... progress) {
            Toast.makeText(context,"The Village list is Loading", Toast.LENGTH_LONG).show();
        }
    };

    private LoginActivity.FileLoader loader = null;
    private LocationHolder.JsonBuilder jsonBuilder = null;


    private void loadLocations() {

        try {
            //jsonBuilder = new LocationHolder.JsonBuilder();
            StringBuilder jsonBuilder = new StringBuilder();
            LocationHolder.loadJsonFile("zilla.json", jsonBuilder, getAssets());
            LocationHolder.setZillaUpazillaUnionString(jsonBuilder.toString());
            StringBuilder jsonBuilderVillage = new StringBuilder();
            LocationHolder.loadJsonFile("vill.json", jsonBuilderVillage, getAssets());
            LocationHolder.setVillageString(jsonBuilderVillage.toString());
            //LocationHolder.setBuilder(jsonBuilder);
            fileLoaded = true;
        } catch (IOException IO) {
            Utilities.printTrace(IO.getStackTrace());
        } catch(JSONException jse) {
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void allowNextActivity() {
        fileLoaded = true;
    }

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
        loader = new FileLoader(this);
        loader.execute();

        forgetPassword= getTextView(R.id.forgotPasswordLabel);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showBiggerToast(LoginActivity.this, R.string.passwordResetRequest);
                forgetPassword.setTextColor(getResources().getColor(R.color.test_color));
                EditText id = getEditText(R.id.providerId);
                //id.setBackgroundColor(Color.argb(50, 200, 4, 4));
            }
        });



    }

    public void startLogin(View view) {
        if(!fileLoaded) {
            Utilities.showBiggerToast(this, R.string.loginWaitRequest);
            return;
        }
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

        //dialog.getWindow().setLayout((int) (w * 0.7), (int) (h * 0.34));


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

                startSecondActivity(placeName);
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

    public void startSecondActivity(String satelliteCenter) {
        Intent intent = new Intent(this, SecondActivity.class);
        //intent.putExtra("satellite", satelliteCenter);
        ProviderInfo.getProvider().setSatelliteName(satelliteCenter);
        startActivity(intent);
    }
}


