package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class NRCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    CompoundButton.OnCheckedChangeListener{

    AsyncNonRegisterClientInfoUpdate NRCInfoUpdateTask;
    AsyncNonRegisterClientInfoUpdate NRCInfoQueryTask;
    private ProviderInfo provider;
    private String SERVLET = "nonRegisteredClient";
    private  String ROOTKEY = "nonRegisteredClientGeneralInfo";

    private EditText cName, cFatherName, cMotherName, cAge;
    public static int NO_OPTIONS=0;
    private String getString, md5Result;
    private Button computeMD5;
    private HashMap<Integer,String> spinnerPair;
    private long generatedId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrc);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initialize();
        Spinner sex = (Spinner)findViewById(R.id.ClientsSexSpinner);
        final Spinner spnDist = (Spinner) findViewById(R.id.Clients_District);
        final Spinner spnUpz = (Spinner) findViewById(R.id.Clients_Upazila);
        final Spinner spnUN = (Spinner) findViewById(R.id.Clients_Union);
        final Spinner spnVillage = (Spinner) findViewById(R.id.Clients_Village);

        cName=(EditText)findViewById(R.id.Client_name);
        cFatherName = (EditText) findViewById(R.id.Clients_Father);
        cMotherName = (EditText) findViewById(R.id.Clients_Mother);
        computeMD5=(Button)findViewById(R.id.btn2);

        spinnerPair = new HashMap<Integer, String>();
        //spinnerPair.put("zilla", (EditText)findViewById(R.id.id_delivery_date));

        computeMD5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //call method to compute MD5 hash
                computeMD5Hash(getString());
            }
        });

        provider = getIntent().getParcelableExtra("Provider");

        //Get the JSON object from the data
        JSONObject parent = this.parseJSONData();

//THis will store all the values inside "Hydrogen" in a element string
        try {
            String element = parent.getString("79");

            //Log.e("Get Json",element);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String getString(){
    Log.e("FoundSumOfStrings",cName.getText().toString()+"  " +cFatherName.getText().toString() + " " + cMotherName.getText().toString());
    //get username and password entered
    getString= cName.getText().toString() + cFatherName.getText().toString() + cMotherName.getText().toString();
    Log.e("FoundSumOfStrings!",getString);
        return getString;
    }
    private static String convertToHex(byte[] data) throws java.io.IOException
    {


        StringBuffer sb = new StringBuffer();
        String hex=null;

        hex= Base64.encodeToString(data, 0, data.length, NO_OPTIONS);

        sb.append(hex);

        return sb.toString();
    }

    public String computeMD5Hash(String getString)
    {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update( getString.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            md5Result= MD5Hash.toString();
            Log.d("MD5Result B4taken14",md5Result);
            md5Result= md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfterTaken14",md5Result);
            generatedId = Long.parseLong(md5Result,16);
            Log.d("MD5ResultAfterTaken16", String.valueOf(generatedId));
            md5Result = Long.toString(generatedId);
            Log.d("MD5ResultAftertoString",md5Result);
            md5Result= md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfter14again",md5Result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return md5Result;
    }

    private JSONObject buildQueryHeader() throws JSONException {
        //get info from database
        String queryString =   "{" +
                "generatedId:" + computeMD5Hash(getString()) + "," +
                "providerid:\"" + String.valueOf(provider.getProviderCode())+"\"," +

                "}";

        Log.e("QueryStrig",queryString);
        return new JSONObject(queryString);
    }

    private void saveToJson() {
        NRCInfoUpdateTask = new AsyncNonRegisterClientInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader();

            Log.e("NRC JSON", json.toString());

        } catch (JSONException jse) {
            Log.e("NRC", "JSON Exception: " + jse.getMessage());
        }

    }
    //Method that will parse the JSON file and will return a JSONObject
    public JSONObject parseJSONData() {
        String JSONString = null;
        JSONObject JSONObject = null;
        try {

            //open the inputStream to the file
            InputStream inputStream = getAssets().open("zilla.json");

            int sizeOfJSONFile = inputStream.available();

            //array that will store all the data
            byte[] bytes = new byte[sizeOfJSONFile];

            //reading data into the array from the file
            inputStream.read(bytes);

            //close the input stream
            inputStream.close();

            JSONString = new String(bytes, "UTF-8");
            JSONObject = new JSONObject(JSONString);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException x) {
            x.printStackTrace();
            return null;
        }
        return JSONObject;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nrc, menu);
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

    @Override
    public void callbackAsyncTask(String result) {

    }

    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("name", getEditText(R.id.Client_name));
        jsonEditTextMap.put("age", getEditText(R.id.Clients_Age));
        jsonEditTextMap.put("husbandname", getEditText(R.id.Clients_Husband));
        jsonEditTextMap.put("fathername", getEditText(R.id.Clients_Father));
        jsonEditTextMap.put("mothername", getEditText(R.id.Clients_Mother));
        jsonEditTextMap.put("hhgrholdingno", getEditText(R.id.Clients_House_No));
        jsonEditTextMap.put("cellno", getEditText(R.id.Clients_Mobile_no));
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("gender", getSpinner(R.id.ClientsSexSpinner));


    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.nrcProceed ) {
        saveToJson();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
