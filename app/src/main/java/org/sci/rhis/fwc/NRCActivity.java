package org.sci.rhis.fwc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class NRCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    CompoundButton.OnCheckedChangeListener{

    AsyncNonRegisterClientInfoUpdate NRCInfoUpdateTask;
    AsyncNonRegisterClientInfoUpdate NRCInfoQueryTask;
    private String SERVLET = "nonRegisteredClient";
    private  String ROOTKEY = "nonRegisteredClientGeneralInfo";

    private EditText cName, cFatherName, cMotherName, cAge;
    public static int NO_OPTIONS=0;
    private String getString, md5Result, vilStringValue;
    private Button computeMD5;
    private HashMap<String, Pair<Integer, Integer>> districtCodeMap;
    private HashMap<String, Integer> upazilaCodeMap;
    private HashMap<String, Integer> unionCodeMap;
    private HashMap<String, Pair<Integer, Integer>> villageCodeMap;
    private long generatedId;
    private  int flag=0, provider;
    private String selectedDistName, selectedUpazilaName,selectedUnionName, selectedVillageName;
    private int divValue, distValue, upValue, unValue, vilValue, mouzaValue;

    public NRCActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrc);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
         provider = intent.getIntExtra("Provider", flag);

        Log.e("ProviderCode",String.valueOf(provider));

        initialize();
        Spinner sex = (Spinner)findViewById(R.id.ClientsSexSpinner);

        cName=(EditText)findViewById(R.id.Client_name);
        cFatherName = (EditText) findViewById(R.id.Clients_Father);
        cMotherName = (EditText) findViewById(R.id.Clients_Mother);
        computeMD5=(Button)findViewById(R.id.btn2);

        districtCodeMap = new HashMap<String,Pair<Integer, Integer>>();
        districtCodeMap.put("ব্রাক্ষ্মণবাড়িয়া", Pair.create(12, 20));
        districtCodeMap.put("হবিগঞ্জ",  Pair.create(36, 60));
        districtCodeMap.put("টাঙ্গাইল",Pair.create(93, 30));


        ArrayList<String> distLIst = new ArrayList<String>();
        distLIst.addAll(districtCodeMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, distLIst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spnDist = (Spinner) findViewById(R.id.Clients_District);
        spnDist.setAdapter(adapter);
        selectedDistName = spnDist.getSelectedItem().toString();
        distValue = districtCodeMap.get(selectedDistName).first;
        divValue = districtCodeMap.get(selectedDistName).second;
        Log.e("selected division Value", String.valueOf(divValue));
        Log.e("selected district Value", String.valueOf(distValue));

        upazilaCodeMap = new HashMap<String,Integer>();
        switch(distValue) {
            case 36:
                upazilaCodeMap.put("মাধবপুর ", 71);
                break;
            case 93:
                upazilaCodeMap.put("বাসাইল ", 36);
                break;
        }
        ArrayList<String> upLIst = new ArrayList<String>();
        upLIst.addAll(upazilaCodeMap.keySet());

        ArrayAdapter<String> upAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, upLIst);
        upAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spnUpz = (Spinner) findViewById(R.id.Clients_Upazila);
        spnUpz.setAdapter(upAdapter);
        selectedUpazilaName = spnUpz.getSelectedItem().toString();
        Log.e("selected Up Value", String.valueOf(selectedUpazilaName));
        upValue = upazilaCodeMap.get(selectedUpazilaName);
        Log.e("selected upazila Value", String.valueOf(upValue));


        unionCodeMap = new HashMap<String,Integer>();
        switch(upValue) {
            case 71:
                unionCodeMap.put("আদাঐর",16);
                unionCodeMap.put("শাহজাহানপুর",94);
                break;
            case 36:
                unionCodeMap.put("কাঞ্চনপুর",59);
                unionCodeMap.put("কাশিল",71);
                break;
        }
        ArrayList<String> unLIst = new ArrayList<String>();
        unLIst.addAll(unionCodeMap.keySet());
        ArrayAdapter<String> unAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, unLIst);
        unAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spnUN = (Spinner) findViewById(R.id.Clients_Union);
        spnUN.setAdapter(unAdapter);
        selectedUnionName = spnUN.getSelectedItem().toString();
        unValue = unionCodeMap.get(selectedUnionName);
        Log.e("selected Union Name", String.valueOf(selectedUnionName));
        Log.e("selected Union Value", String.valueOf(unValue));


        villageCodeMap = new HashMap<String,Pair<Integer, Integer>>();
        switch(unValue) {

            case 94:
                villageCodeMap.put("বান্দারিয়া",Pair.create(01, 164));
                villageCodeMap.put("ফারোদপুর",Pair.create(02, 324));
                break;
            case 59:
                villageCodeMap.put("যৌতুকী",Pair.create(01, 458));
                villageCodeMap.put("তারাবাড়ী",Pair.create(05, 547));
                break;
            case 71:
                villageCodeMap.put("বাংড়া",Pair.create(01, 167));
                villageCodeMap.put("পিচুরী",Pair.create(01, 816));
                break;
            case 16:
                villageCodeMap.put("দক্ষিণমোহাম্মদপুর",Pair.create(01, 276));
                villageCodeMap.put("মিঠাপুকুর",Pair.create(02, 368));
                break;
        }
        ArrayList<String> vilLIst = new ArrayList<String>();
        vilLIst.addAll(villageCodeMap.keySet());
        ArrayAdapter<String> vilAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, vilLIst);
        vilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spnVillage = (Spinner) findViewById(R.id.Clients_Village);
        spnVillage.setAdapter(vilAdapter);
        selectedVillageName = spnVillage.getSelectedItem().toString();
        vilValue = villageCodeMap.get(selectedVillageName).first;
        mouzaValue = villageCodeMap.get(selectedVillageName).second;
        Log.e("selected Village Name", String.valueOf(selectedVillageName));
        if(vilValue<10)
        {
          vilStringValue = "0" + String.valueOf(vilValue);
        }

        Log.e("selected Village Value", String.valueOf(vilStringValue));
        Log.e("selected mouza Value", String.valueOf(mouzaValue));


        addListenerOnButton();


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
    getString= cName.getText().toString() + cFatherName.getText().toString() + cMotherName.getText().toString() +
                distValue + upValue + unValue + mouzaValue + vilStringValue;
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
        Log.e("Selected District Value",String.valueOf(distValue));
        //get info from database
        String queryString =   "{" +
                "\"generatedId\":"  + computeMD5Hash(getString())  + "," +
                "\"providerid\":" + String.valueOf(provider)+ "," +
                "\"division\":" + String.valueOf(divValue)+ "," +
                "\"district\":" + String.valueOf(distValue) + "," +
                "\"upazila\":" + String.valueOf(upValue)+ "," +
                "\"union\":" + String.valueOf(unValue)+ "," +
                "\"mouza\":" + String.valueOf(mouzaValue)+ "," +
                "\"village\":" + String.valueOf(vilValue)+
                "}";
       // Log.e("selected Item's Value", String.valueOf(distValue));
        Log.e("QueryStrig",queryString);
        return new JSONObject(queryString);
    }

    private void nrcSaveToJson() {
        NRCInfoUpdateTask = new AsyncNonRegisterClientInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader();
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);

            NRCInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            Log.e("NRC JSON Sent2SERVLET", json.toString());

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
    protected void initiateMultiSelectionSpinners(){}

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
    public void addListenerOnButton() {

        Button  button1 = (Button) findViewById(R.id.nrcProceed);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

              nrcSaveToJson();
            }
        });

        computeMD5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call method to compute MD5 hash
                computeMD5Hash(getString());
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
