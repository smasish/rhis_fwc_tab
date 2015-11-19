package org.sci.rhis.fwc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
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
import java.util.Iterator;
import java.util.Map;

public class NRCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                    View.OnClickListener,
                                                                    CompoundButton.OnCheckedChangeListener {

    AsyncNonRegisterClientInfoUpdate NRCInfoUpdateTask;
    AsyncNonRegisterClientInfoUpdate NRCInfoQueryTask;
    private String SERVLET = "nonRegisteredClient";
    private String ROOTKEY = "nonRegisteredClientGeneralInfo";
    private String LOGTAG = "FWC-REGISTRATION";

    private EditText cName, cFatherName, cMotherName, cAge;
    public static int NO_OPTIONS = 0;
    private String getString, md5Result, vilStringValue;
    private Button computeMD5;
    private HashMap<String, Pair<Integer, Integer>> districtCodeMap;
    private HashMap<String, Integer> upazilaCodeMap;
    private HashMap<String, Integer> unionCodeMap;
    private HashMap<String, Pair<Integer, Integer>> villageCodeMap;
    private long generatedId;

    private int flag = 0;
    ProviderInfo provider;
    private String selectedDistName, selectedUpazilaName, selectedUnionName, selectedVillageName;
    private int divValue, distValue, upValue, unValue, vilValue, mouzaValue;

    private String zillaString = "";
    private String villageString = "";

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    private StringBuilder jsonBuilder = null;
    private StringBuilder jsonBuilderVillage = null;

    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();

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
        provider = intent.getParcelableExtra("Provider");

        Log.d("ProviderCode", String.valueOf(provider.getProviderCode()));

        initialize();

        cName = (EditText) findViewById(R.id.Client_name);
        cFatherName = (EditText) findViewById(R.id.Clients_Father);
        cMotherName = (EditText) findViewById(R.id.Clients_Mother);
        computeMD5 = (Button) findViewById(R.id.btn2);

        /*districtCodeMap = new HashMap<String, Pair<Integer, Integer>>();
        districtCodeMap.put("ব্রাক্ষ্মণবাড়িয়া", Pair.create(12, 20));
        districtCodeMap.put("হবিগঞ্জ", Pair.create(36, 60));
        districtCodeMap.put("টাঙ্গাইল", Pair.create(93, 30));


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
        Log.d("selected division Value", String.valueOf(divValue));
        Log.d("selected district Value", String.valueOf(distValue));

        upazilaCodeMap = new HashMap<String, Integer>();
        switch (distValue) {
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
        Log.d("selected Up Value", String.valueOf(selectedUpazilaName));
        upValue = upazilaCodeMap.get(selectedUpazilaName);
        Log.d("selected upazila Value", String.valueOf(upValue));


        unionCodeMap = new HashMap<String, Integer>();
        switch (upValue) {
            case 71:
                unionCodeMap.put("আদাঐর", 16);
                unionCodeMap.put("শাহজাহানপুর", 94);
                break;
            case 36:
                unionCodeMap.put("কাঞ্চনপুর", 59);
                unionCodeMap.put("কাশিল", 71);
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
        Log.d("selected Union Name", String.valueOf(selectedUnionName));
        Log.d("selected Union Value", String.valueOf(unValue));


        villageCodeMap = new HashMap<String, Pair<Integer, Integer>>();
        switch (unValue) {

            case 94:
                villageCodeMap.put("বান্দারিয়া", Pair.create(01, 164));
                villageCodeMap.put("ফারোদপুর", Pair.create(02, 324));
                break;
            case 59:
                villageCodeMap.put("যৌতুকী", Pair.create(01, 458));
                villageCodeMap.put("তারাবাড়ী", Pair.create(05, 547));
                break;
            case 71:
                villageCodeMap.put("বাংড়া", Pair.create(01, 167));
                villageCodeMap.put("পিচুরী", Pair.create(01, 816));
                break;
            case 16:
                villageCodeMap.put("দক্ষিণমোহাম্মদপুর", Pair.create(01, 276));
                villageCodeMap.put("মিঠাপুকুর", Pair.create(02, 368));
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
        Log.d("selected Village Name", String.valueOf(selectedVillageName));
        if (vilValue < 10) {
            vilStringValue = "0" + String.valueOf(vilValue);
        }

        Log.d("selected Village Value", String.valueOf(vilStringValue));
        Log.d("selected mouza Value", String.valueOf(mouzaValue));



        //Get the JSON object from the data
        JSONObject parent = this.parseJSONData();

//THis will store all the values inside "Hydrogen" in a element string
        try {
            String element = parent.getString("79");            //Log.d("Get Json",element);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        generatedId = 0;


        // ---- JAMIL START---- //
        districtList    =  new ArrayList<>();
        upazillaList    =  new ArrayList<>();
        unionList       =  new ArrayList<>();
        villageList     =  new ArrayList<>();

        initialize();
        addListenerOnButton();
        addAndSetSpinners();



        jsonSpinnerMap.get("gender").setSelection(1); //select woman by default
        try {
            jsonBuilder = new StringBuilder();
            loadJsonFile("zilla.json", jsonBuilder);
            zillaString = jsonBuilder.toString();
            jsonBuilderVillage = new StringBuilder();
            loadJsonFile("vill.json", jsonBuilderVillage);
            villageString = jsonBuilderVillage.toString();
            LocationHolder.loadListFromJson(zillaString, "nameEnglish", "nameBangla", "Upazila", districtList);

            //set zilla spinner
            zillaAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, districtList);
            zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            getSpinner(R.id.Clients_District).setAdapter(zillaAdapter);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        // ---- JAMIL END----- //

    }

    private String getString() {
        Log.d("FoundSumOfStrings", cName.getText().toString() + "  " + cFatherName.getText().toString() + " " + cMotherName.getText().toString());
        //get username and password entered
        divValue  = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[1]);
        distValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[0]);
        upValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Upazila).getSelectedItem()).getCode());
        unValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Union).getSelectedItem()).getCode());
        mouzaValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Village).getSelectedItem()).getCode().split("_")[1]);
        vilValue = Integer.valueOf(((LocationHolder) getSpinner(R.id.Clients_Village).getSelectedItem()).getCode().split("_")[0]);
        vilStringValue  = ((LocationHolder) getSpinner(R.id.Clients_Union).getSelectedItem()).getCode().split("_")[0];
        getString = cName.getText().toString() + cFatherName.getText().toString() + cMotherName.getText().toString() +
                distValue + upValue + unValue + mouzaValue + vilStringValue;
        Log.d("FoundSumOfStrings!", getString);
        return getString;
    }

    /*
    private static String convertToHex(byte[] data) throws java.io.IOException
    {


        StringBuffer sb = new StringBuffer();
        String hex=null;

        hex= Base64.encodeToString(data, 0, data.length, NO_OPTIONS);

        sb.append(hex);

        return sb.toString();
    }
*/
    public String computeMD5Hash(String getString) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(getString.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            md5Result = MD5Hash.toString();
            Log.d("MD5Result B4taken14", md5Result);
            md5Result = md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfterTaken14", md5Result);
            generatedId = Long.parseLong(md5Result, 16);
            Log.d("MD5ResultAfterTaken16", String.valueOf(generatedId));
            md5Result = Long.toString(generatedId);
            Log.d("MD5ResultAftertoString", md5Result);
            md5Result = md5Result.toString().substring(0, 14);
            Log.d("MD5ResultAfter14again", md5Result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Result;
    }

    private JSONObject buildQueryHeader() throws JSONException {
        Log.d("Selected District Value", String.valueOf(distValue));
        //get info from database
        String queryString = "{" +
                "\"generatedId\":" + computeMD5Hash(getString()) + "," +
                "\"providerid\":" + String.valueOf(provider.getProviderCode()) + "," +
                "\"division\":" + String.valueOf(divValue) + "," +
                "\"district\":" + String.valueOf(distValue) + "," +
                "\"upazila\":" + String.valueOf(upValue) + "," +
                "\"union\":" + String.valueOf(unValue) + "," +
                "\"mouza\":" + String.valueOf(mouzaValue) + "," +
                "\"village\":" + String.valueOf(vilValue) +
                "}";
        // Log.d("selected Item's Value", String.valueOf(distValue));
        Log.d("QueryStrig", queryString);
        return new JSONObject(queryString);
    }

    private void nrcSaveToJson() {
        NRCInfoUpdateTask = new AsyncNonRegisterClientInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader();
            Utilities.getEditTexts(jsonEditTextMap, json);
            //Utilities.getSpinners(jsonSpinnerMap, json);
            getSpecialCases(json);

            Log.d("NRC JSON Sent2SERVLET**", json.toString());

            NRCInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            Log.d("NRC JSON Sent2SERVLET", json.toString());

        } catch (JSONException jse) {
            Log.e("NRC", "JSON Exception: " + jse.getMessage());
        }

    }

    private void getSpecialCases(JSONObject json) throws JSONException {
        String key = "gender";
        json.put(key, jsonSpinnerMap.get(key).getSelectedItemPosition() + 1);
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
        } catch (JSONException x) {
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
        Log.d("Found result", result);
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;

            //DEBUG
            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key) + "\'");
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
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
        jsonEditTextMap.put("cellno", getEditText(R.id.NrcClients_Mobile_no));

        for (Map.Entry<String, EditText> edit : jsonEditTextMap.entrySet()) {
            edit.getValue().setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        }
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("gender", getSpinner(R.id.ClientsSexSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
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

    public void addListenerOnButton() {

        Button proceedButton = (Button) findViewById(R.id.nrcProceed);

        proceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                nrcSaveToJson();
                Intent intent = new Intent();
                intent.putExtra("generatedId", computeMD5Hash(getString()));
                setResult(RESULT_OK, intent);
                finishActivity(ActivityResultCodes.REGISTRATION_ACTIVITY);
                finish();
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
        switch (parent.getId()) {
            case R.id.Clients_District:
                //LocationHolder location = (LocationHolder)parent.getSelectedItem();

                LocationHolder zilla = districtList.get(position);

                upazillaList.clear();
                upazilaAdapter.clear();
                upazillaList.add(blanc);
                LocationHolder.loadListFromJson(
                        zilla.getSublocation(),
                        "nameEnglishUpazila",
                        "nameBanglaUpazila",
                        "Union",
                        upazillaList);
                for (LocationHolder holder : upazillaList) {
                    Log.d(LOGTAG, "Upazila: -> " + holder.getBanglaName());
                }

                upazilaAdapter.addAll(upazillaList);
                upazilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getSpinner(R.id.Clients_Upazila).setAdapter(upazilaAdapter);

                break;
            case R.id.Clients_Upazila:

                LocationHolder upazila = upazillaList.get(position);
                unionList.clear();
                unionAdapter.clear();
                unionList.add(blanc);
                LocationHolder.loadListFromJson(
                        upazila.getSublocation(),
                        "nameEnglishUnion",
                        "nameBanglaUnion",
                        "",
                        unionList);
                for (LocationHolder holder : unionList) {
                    Log.d(LOGTAG, "Union: -> " + holder.getBanglaName());
                }
                unionAdapter.addAll(unionList);
                getSpinner(R.id.Clients_Union).setAdapter(unionAdapter);
                break;
            case R.id.Clients_Union:
                LocationHolder union = unionList.get(position);
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                /*Thread t = new Thread(new Runnable() {
                    public void run() {

                    }
                });

                t.start();*/

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.Clients_District).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.Clients_Upazila).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.Clients_Union).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }


                villageAdapter.addAll(villageList);
                getSpinner(R.id.Clients_Village).setAdapter(villageAdapter);

                Log.d(LOGTAG, "Union Case: -> ");

                break;
            case R.id.Clients_Village:
                //Utilities.MakeInvisible(this, R.id.loadingPanel);
                Log.d(LOGTAG, "Village Case: -> ");
                break;
            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadVillageFromJson(
            String zilla,
            String upazila,
            String union,
            ArrayList<LocationHolder> holderList) {


        try {
            if(villJson == null) {
                villJson = new JSONObject(villageString);
            }

            if( union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
                    union.equals("") || upazila.equals("") || zilla.equals("")) {
                return;
            }

            JSONObject unionJson =
                    villJson.getJSONObject(zilla).getJSONObject(upazila).getJSONObject(union);

            Log.d(LOGTAG, "Union deatails:\n\t" + union.toString());

            for (Iterator<String> mouzaKey = unionJson.keys(); mouzaKey.hasNext(); ) {
                String mouza = mouzaKey.next();
                JSONObject mouzaJson = unionJson.getJSONObject(mouza);

                for (Iterator<String> villageCode = mouzaJson.keys(); villageCode.hasNext(); ) {
                    String code = villageCode.next();
                    holderList.add(
                            new LocationHolder(
                                    code + "_" + mouza,
                                    mouzaJson.getString(code),
                                    mouzaJson.getString(code),
                                    //subobject.getJSONObject(keySublocation),
                                    ""));

                }

                Log.d(LOGTAG, "Mouja - Village: " + mouza + " -> " + unionJson.getString(mouza));

            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }


    private void loadJsonFile(String fileName, StringBuilder jsonBuilder) throws IOException {
        InputStream is = getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        jsonBuilder.append(new String(buffer, "UTF-8"));

    }

    private void addAndSetSpinners() {
        //getSpinner(R.id.Clients_District).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_District).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Upazila).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Union).setOnItemSelectedListener(this);
        getSpinner(R.id.Clients_Village).setOnItemSelectedListener(this);


        zillaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item); //check
        upazilaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
}