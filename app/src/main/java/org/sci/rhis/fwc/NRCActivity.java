package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    class FileLoader extends AsyncTask<String, Integer, Integer> {
        Context context;
        FileLoader(Context c) {context = c;};
        protected Integer doInBackground(String... params) {
            loadLocations();
            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            getSpinner(R.id.Clients_District).setAdapter(zillaAdapter);
            loadVillages.setVisibility(View.GONE);
        }

        protected void onProgressUpdate (Integer... progress) {
            Toast.makeText(context,"The Village list is Loading", Toast.LENGTH_LONG).show();
        }
    };

    FileLoader loader = null;
    AsyncNonRegisterClientInfoUpdate NRCInfoUpdateTask;
    private String SERVLET = "nonRegisteredClient";
    private String ROOTKEY = "nonRegisteredClientGeneralInfo";
    private String LOGTAG = "FWC-REGISTRATION";

    private EditText cName, cFatherName, cMotherName, cAge,cMobileNo;

    private String getString, md5Result, vilStringValue;
    private Button computeMD5;
    private long generatedId;

    private int flag = 0;
    ProviderInfo provider;

    private int divValue, distValue, upValue, unValue, vilValue, mouzaValue;
    private int countSaveClick=0;

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
    private ProgressBar loadVillages = null;

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


        cMobileNo = (EditText) findViewById(R.id.NrcClients_Mobile_no);
        cMobileNo.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});

        cName = (EditText) findViewById(R.id.Client_name);
        cFatherName = (EditText) findViewById(R.id.Clients_Father);
        cMotherName = (EditText) findViewById(R.id.Clients_Mother);
        computeMD5 = (Button) findViewById(R.id.btn2);

        generatedId = 0;

        // ---- JAMIL START---- //
        districtList    =  new ArrayList<>();
        upazillaList    =  new ArrayList<>();
        unionList       =  new ArrayList<>();
        villageList     =  new ArrayList<>();

        loader = new FileLoader(this);

        initialize();
        addListenerOnButton();
        addAndSetSpinners();
        Utilities.MakeVisible(this, R.id.loadingPanelNrc);
        loadVillages = (ProgressBar)findViewById(R.id.nrcProgressBar);
        loadVillages.setVisibility(View.VISIBLE);
        //loadLocations();

        // ---- JAMIL END----- //

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(!loader.isCancelled()) {
            loader.execute();
        }
    }

    private void loadLocations() {
        jsonSpinnerMap.get("gender").setSelection(1); //select woman by default
        try {
            jsonBuilder = new StringBuilder();
            loadJsonFile("zilla.json", jsonBuilder);
            zillaString = jsonBuilder.toString();

            districtList.add(blanc);
            LocationHolder.loadListFromJson(zillaString, "nameEnglish", "nameBangla", "Upazila", districtList);

            //set zilla spinner
            zillaAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, districtList);
            zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //getSpinner(R.id.Clients_District).setAdapter(zillaAdapter);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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

        hasTheRequiredFileds();
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

    private boolean hasTheRequiredFileds() {
        String textFileds [] = {"name", "age", "fathername", "mothername"};
        String fields = "";
        boolean isEmpty = false;

        for( int i = 0;i< textFileds.length && !isEmpty; ++i) {
            fields = textFileds[i];
            if(jsonEditTextMap.get(fields).getText().toString().equals("")) {
                isEmpty = true;
            }
        }

        boolean allSelected =  getSpinner(R.id.Clients_District).getSelectedItemPosition() != 0 &&
                               getSpinner(R.id.Clients_Upazila).getSelectedItemPosition() != 0 &&
                               getSpinner(R.id.Clients_Union).getSelectedItemPosition() != 0 &&
                               getSpinner(R.id.Clients_Village).getSelectedItemPosition() != 0;

        //TODO - there may not exist a village
        if(isEmpty || !allSelected) {
            Toast toast = Toast.makeText(this, R.string.NRCSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        }

        return true;
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

       /* Old Save button function
       Button proceedButton = (Button) findViewById(R.id.nrcProceed);

        proceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!hasTheRequiredFileds())
                    return;
                nrcSaveToJson();
                Intent intent = new Intent();
                intent.putExtra("generatedId", computeMD5Hash(getString()));
                setResult(RESULT_OK, intent);
                finishActivity(ActivityResultCodes.REGISTRATION_ACTIVITY);
                finish();
            }
        });*/

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
                //villJson = new JSONObject(villageString);
                villJson = LocationHolder.getVillageJson();
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

    public void onClickSaveNRC(View view) {
        countSaveClick++;
        if( countSaveClick == 2 ) {
            nrcSaveToJson();
            getButton(R.id.nrcProceed).setText("Save");
            countSaveClick = 0;
            Intent intent = new Intent();
            intent.putExtra("generatedId", computeMD5Hash(getString()));
            setResult(RESULT_OK, intent);
            finishActivity(ActivityResultCodes.REGISTRATION_ACTIVITY);
            finish();
        } else if(countSaveClick == 1) {
            if(!hasTheRequiredFileds())
                return;
            Utilities.Disable(this, R.id.clients_intro_layout);
            Utilities.Disable(this, R.id.Clients_House_No);
            Utilities.Enable(this, R.id.nrcProceed);
            Utilities.Enable(this, R.id.nrcCancel);


            getButton( R.id.nrcProceed).setText("Confirm");
            Utilities.MakeVisible(this, R.id.nrcCancel);

            Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
        }
    }

    public void onClickCancelNRC(View view) {
        if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.clients_intro_layout);
            Utilities.Enable(this, R.id.Clients_House_No);

            getButton(R.id.nrcProceed).setText("Save");
            //TODO - Review
            Utilities.MakeInvisible(this, R.id.nrcCancel);
        }
    }



}