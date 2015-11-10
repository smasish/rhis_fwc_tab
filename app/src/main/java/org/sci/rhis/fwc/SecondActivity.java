
package org.sci.rhis.fwc;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class SecondActivity extends ClinicalServiceActivity implements ArrayIndexValues{

    private Button button;
    private PregWoman woman;
    private Vector<Pair<String, Integer>> deliveryHistoryMapping;
    private int providerCode;

    private View mClientIntroLayout;
    private View mClientInfoLayout;
    Boolean flag = false;

    AsyncClientInfoUpdate clientInfoQueryTask;
    AsyncClientInfoUpdate clientInfoUpdateTask;

    final private String SERVLET = "handlepregwomen";
    final private String ROOTKEY = "pregWomen";
    private  final String LOGTAG    = "FWC-INFO";

    private BigInteger responseID = BigInteger.valueOf(0);
    EditText lmpEditText;
    EditText eddEditText;
    JSONObject client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ProviderInfo provider = ProviderInfo.getProvider();

        TextView FWCName = (TextView) findViewById(R.id.fwc_heading);
        FWCName.setText(provider.getProviderFacility());

        providerCode = Integer.parseInt(String.valueOf(provider.getProviderCode()));
        Log.i("SecondActivity", "" + provider.getProviderFacility());

        initialize();//super class
        Spinner staticSpinner = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.Health_Id, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long row_id) {
                final Intent intent;
                if (position == ADV_SEARCH_INDEX) {
                    intent = new Intent(SecondActivity.this, ADVSearchActivity.class);
                   startActivityForResult(intent, ActivityResultCodes.ADV_SEARCH_ACTIVITY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        lmpEditText = (EditText) findViewById(R.id.lmpDate);
        eddEditText = (EditText) findViewById(R.id.edd);

        lmpEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date lmp = uiFormat.parse(lmpEditText.getText().toString());

                            Date edd = Utilities.addDateOffset(lmp, 240);
                            eddEditText.setText(uiFormat.format(edd));

                        } catch (ParseException PE) {

                        }

                    }
                }

        );

        addListenerOnButton();

        /*Intent intent = getIntent();
        String str = intent.getStringExtra("HealthId");
        if(intent.hasExtra("HealthId")) {
            str = intent.getStringExtra("HealthId");
            getSpinner(R.id.ClientsIdentityDropdown).setSelection(0);
            getEditText(R.id.searchableTextId).setText(str.substring(str.indexOf("||")+3));
            startSearch((ImageButton)findViewById(R.id.searchButton));
        }*/
    }

    public void startSearch(View view) {
        Spinner searchOptions = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        EditText searchableId = (EditText) findViewById(R.id.searchableTextId);
        //TODO - remove
        long index = (searchOptions.getSelectedItemId() + 1);
        String stringId = (String) searchOptions.getSelectedItem();
        long id;
        try {
            id = Long.valueOf(searchableId.getText().toString());
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, "Invalid ID \n\t" + nfe.toString());
            Toast.makeText(this, "Invalid ID typed. Please provide valid ID ...", Toast.LENGTH_LONG).show();
            return;
        }

        String queryString = "{" +
                "sOpt:" + String.valueOf(index) + "," +
                "sStr:" + String.valueOf(id) + "," +
                "providerid:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "client";
        String jsonRootkey = "sClient";
        AsyncClientInfoUpdate retrieveClient = new AsyncClientInfoUpdate(this);

        //TODO- setting woman to null before executing next search, but seems hacky
        //      need better alternative eventually, getting messy
        Utilities.Reset(this, R.id.clients_intro_layout);
        Utilities.Reset(this, R.id.clients_info_layout);
        retrieveClient.execute(queryString, servlet, jsonRootkey);

        TextView mHealthIdLayout = (TextView) findViewById(R.id.health_id);
        mHealthIdLayout.setVisibility(View.VISIBLE);

        TextView healthId = (TextView) findViewById(R.id.health_id);
        healthId.setText(String.valueOf(stringId) + ": " + String.valueOf(id));

    }

    private void populateClientDetails(JSONObject json, HashMap<String, Integer> fieldMapping) {
        Iterator<String> i = fieldMapping.keySet().iterator();
        String key;

        try {
            if(json.has("cMobileNo")){
                String mobileNumber = json.getString("cMobileNo");
                if(!mobileNumber.equals("") && mobileNumber.charAt(0)!='0') {
                    mobileNumber = "0" + mobileNumber;
                    json.put("cMobileNo", mobileNumber);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        while (i.hasNext()) {
            key = i.next();
            if (fieldMapping.get(key) != null) { //If the field exist in the mapping table
                try {
                    //((EditText) findViewById(fieldMapping.get(key))).setText(json.get(key).toString());
                    ((EditText) findViewById(fieldMapping.get(key))).setText(json.getString(key));
                } catch (JSONException jse) {
                    Log.e(LOGTAG, "JSON Exception Thrown(test):\n ");
                    jse.printStackTrace();
                }
            }
        }

        //manipulateJson(json);

        Utilities.setSpinners(jsonSpinnerMap, json);
        Utilities.setCheckboxes(jsonCheckboxMap, json);
        Utilities.setEditTextDates(jsonEditTextDateMap, json);

    }

    @Override
    public void callbackAsyncTask(String result) { //Get results back from healthId search
        Log.d("result:", result);

        try
        {
            JSONObject json = new JSONObject(result);
            String key;

            //DEBUG response from servlet
            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d(LOGTAG, "Key:" + key + " Value:\'" + json.get(key) + "\'");
            }


            if (!json.has("responseType")) //callback for client servlet
            {
                client = json;

                if (json.get("False").toString().equals("")) { //Client exists
                    populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                    woman = PregWoman.CreatePregWoman(json);
                    responseID = new BigInteger(json.get("cHealthID").toString());

                    if (woman != null) {

                        manipulateJson(json);
                        populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                        woman.UpdateUIField(this);
                        Utilities.Disable(this, R.id.clients_info_layout);
                        Utilities.DisableField(this, R.id.Clients_House_No);
                        Utilities.DisableField(this, R.id.Clients_Mobile_no);

                        Utilities.InVisibleButton(this, R.id.client_Save_Button);
                        Utilities.InVisibleButton(this, R.id.client_update_Button);
                        Utilities.VisibleButton(this, R.id.client_edit_Button);
                        Utilities.VisibleButton(this, R.id.client_New_preg_Button);
                    }

                    else {
                        Utilities.VisibleButton(this, R.id.client_Save_Button);
                        Utilities.InVisibleButton(this, R.id.client_update_Button);
                        Utilities.InVisibleButton(this, R.id.client_edit_Button);
                        Utilities.InVisibleButton(this, R.id.client_New_preg_Button);
                    }

                    Utilities.Disable(this, R.id.clients_intro_layout);
                    Utilities.VisibleLayout(this, R.id.clients_info_layout);

                }

                else {

                    Toast.makeText(this, "Provided information is not valid! Please try again....", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Provided information is not valid! Please try again....", Toast.LENGTH_LONG).show();
                    Utilities.InVisibleLayout(this, R.id.clients_info_layout);
                }
            }

            else

            {  //callback for PregInfo servlet
                client.put("regSerialNo", json.get("regSerialNo"));
                client.put("regDate", json.get("regDate"));
                client.put("highRiskPreg", json.get("highRiskPreg"));
                client.put("cPregNo", json.get("pregNo"));
                client.put("cNewMCHClient", "false");

                Log.d("json", client.toString());

                woman = PregWoman.CreatePregWoman(client);
                //populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                responseID = new BigInteger(client.get("cHealthID").toString());

                //manipulateJson(json);
                //populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                woman.UpdateUIField(this);

                Utilities.Disable(this, R.id.clients_info_layout);
                Utilities.DisableField(this, R.id.Clients_House_No);
                Utilities.DisableField(this, R.id.Clients_Mobile_no);


                Utilities.InVisibleButton(this, R.id.client_update_Button);
                Utilities.InVisibleButton(this, R.id.client_Save_Button);
                Utilities.VisibleButton(this, R.id.client_New_preg_Button);
                Utilities.VisibleButton(this, R.id.client_edit_Button);

            }
        }

        catch (JSONException jse) {
            System.out.println("JSON Exception Thrown( At callbackAsyncTask ):\n ");
            jse.printStackTrace();
        }

    }

    public void addListenerOnButton() {

        final Context context = this;

        Button button1 = (Button) findViewById(R.id.nonregiser);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                /*Intent intent = new Intent(context, NRCActivity.class);
                intent.putExtra("Provider", providerCode);
                startActivity(intent);*/
            }
        });
    }

    public void startNRC(View view) {
        Intent intent = new Intent(this, NRCActivity.class);
        intent.putExtra("PregWoman", woman);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        startActivityForResult(intent, ActivityResultCodes.REGISTRATION_ACTIVITY);
    }

    public void startANC(View view) {
        Intent intent = new Intent(this, ANCActivity.class);
        if (checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.ANC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for ANC, verify ...", Toast.LENGTH_LONG).show();
        }

    }

    public void startDelivery(View view) {
        Intent intent = new Intent(this, DeliveryActivity.class);

        if (checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.DELIVERY)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for Delivery, verify ...", Toast.LENGTH_LONG).show();
        }
    }

    public void startPNC(View view) {
        Intent intent = new Intent(this, PNCActivity.class);
        if (checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.PNC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for PNC, verify ...", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkClientInfo() {
        if (woman == null) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultCodes.REGISTRATION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                long healthId = data.getLongExtra("generatedId", -1);
                getSpinner(R.id.ClientsIdentityDropdown).setSelection(NRC_ID_INDEX);
                getEditText(R.id.searchableTextId).setText(String.valueOf(healthId));
                startSearch((ImageButton)findViewById(R.id.searchButton));
            }
        } else if (requestCode == ActivityResultCodes.ADV_SEARCH_ACTIVITY) {
            if (resultCode == RESULT_OK) {

                String str = data.getStringExtra("HealthId");
                getSpinner(R.id.ClientsIdentityDropdown).setSelection(data.getIntExtra("HealthIdType",0));
                getEditText(R.id.searchableTextId).setText(str);
                startSearch((ImageButton)findViewById(R.id.searchButton));
            }
        }
    }

    public void onClickSaveClient(View view) {
        saveClientToJson();
    }

    ////for complication history
    private void initializeJsonManipulation() {
        deliveryHistoryMapping = new Vector<Pair<String, Integer>>(9);
        //The order is important
        deliveryHistoryMapping.addElement(Pair.create("bleeding", R.id.previousDeliveryBleedingCheckBox)); //0
        deliveryHistoryMapping.addElement(Pair.create("delayedDelivery", R.id.delayedBirthCheckBox));//1
        deliveryHistoryMapping.addElement(Pair.create("blockedDelivery", R.id.blockedDeliveryCheckBox));//2
        deliveryHistoryMapping.addElement(Pair.create("blockedPlacenta", R.id.placentaInsideUterusCheckBox));//3
        deliveryHistoryMapping.addElement(Pair.create("deadBirth", R.id.giveBirthDeadCheckBox));//4
        deliveryHistoryMapping.addElement(Pair.create("lived48Hour", R.id.newbornDieWithin48hoursCheckBox));//5
        deliveryHistoryMapping.addElement(Pair.create("edemaSwelling", R.id.swellingLegsOrWholeBodyCheckBox));//6
        deliveryHistoryMapping.addElement(Pair.create("convulsion", R.id.withConvulsionSenselessCheckBox));//7
        deliveryHistoryMapping.addElement(Pair.create("caesar", R.id.caesarCheckBox));//8
    }

    private void manipulateJson(JSONObject json) {
        try {
            String history = json.getString("cHistoryComplicatedContent");
            String[] array = {};
            if(!history.equals("")) {
              array  = history.split(",");
            }

            for (int i = 0; i < array.length; i++) {
                json.put(deliveryHistoryMapping.get(Integer.valueOf(array[i]) - 1).first, 1);// 1- checked, 2 - unchecked
            }
        } catch (JSONException jse) {
            jse.getMessage();
            jse.printStackTrace();
        }
    }
/* May be used for building Json key complication note

    private void buildJson(JSONObject complicated) {
        try {

            for (int i = 1; i <= complicated.names().length(); i++) {
                if (complicated.get(complicated.names().getString(i))==1)
                // complicated.put(deliveryHistoryMapping.get(Integer.valueOf(array[i]) - 1).first, 1);
            }

        }catch (JSONException jse) {
                jse.getMessage();
                jse.printStackTrace();
        }
    }
*/

    //The following methods are all required for all the activities that updates information
    //from user interface
    @Override
    protected void initiateCheckboxes() {
        //TT
        jsonCheckboxMap.put("cTT1", getCheckbox(R.id.Clients_TT_Tika1));
        jsonCheckboxMap.put("cTT2", getCheckbox(R.id.Clients_TT_Tika2));
        jsonCheckboxMap.put("cTT3", getCheckbox(R.id.Clients_TT_Tika3));
        jsonCheckboxMap.put("cTT4", getCheckbox(R.id.Clients_TT_Tika4));
        jsonCheckboxMap.put("cTT5", getCheckbox(R.id.Clients_TT_Tika5));

        jsonCheckboxMapSave.put("tt1", getCheckbox(R.id.Clients_TT_Tika1));
        jsonCheckboxMapSave.put("tt2", getCheckbox(R.id.Clients_TT_Tika2));
        jsonCheckboxMapSave.put("tt3", getCheckbox(R.id.Clients_TT_Tika3));
        jsonCheckboxMapSave.put("tt4", getCheckbox(R.id.Clients_TT_Tika4));
        jsonCheckboxMapSave.put("tt5", getCheckbox(R.id.Clients_TT_Tika5));

        //Complicated Delivery History
        //NOTE: These JSON keys are not present in the Servlet response
        //The response will be manipulated to trick Checkbox handlers
        //so everything is handled in a general way.
        //manipulate json
        initializeJsonManipulation();
        for (Pair<String, Integer> pair : deliveryHistoryMapping) {
            jsonCheckboxMap.put(pair.first, getCheckbox(pair.second));
        }
    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("para", getEditText(R.id.para));
        jsonEditTextMap.put("gravida", getEditText(R.id.gravida));
        jsonEditTextMap.put("boy", getEditText(R.id.SonNum));
        jsonEditTextMap.put("girl", getEditText(R.id.DaughterNum));
        jsonEditTextMap.put("houseGRHoldingNo", getEditText(R.id.Clients_House_No));
        jsonEditTextMap.put("mobileNo", getEditText(R.id.Clients_Mobile_no));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("FacilityName", getTextView(R.id.fwc_heading));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMapSave.put("bloodGroup", getSpinner(R.id.Blood_Group_Dropdown));
        jsonSpinnerMap.put("cBloodGroup", getSpinner(R.id.Blood_Group_Dropdown));
    }

    @Override
    protected void initiateMultiSelectionSpinners() {
    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("cLMP", getEditText(R.id.lmpDate));
        jsonEditTextDateMap.put("cEDD", getEditText(R.id.edd));

        jsonEditTextDateMapSave.put("lmp", getEditText(R.id.lmpDate));
        jsonEditTextDateMapSave.put("edd", getEditText(R.id.edd));
    }

    @Override
    protected void initiateRadioGroups() {
    }

    private void saveClientToJson() {
        clientInfoUpdateTask = new AsyncClientInfoUpdate(this);
        JSONObject json;
        //JSONObject complicated = null;
        try {
            json = buildQueryHeader(false);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMapSave, json);
            Utilities.getCheckboxesBlank(jsonCheckboxMapSave, json);
            //buildJson(complicated);
            Utilities.getSpinners(jsonSpinnerMapSave, json);
            getSpecialCases(json);
            Log.d("Pregwomen", "***************In progress :" + json.toString());
            storeInfoToJsonfirst(json);
            clientInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        }
        catch (JSONException jse) {
            Log.e("Pregwomen", "JSON Exception: " + jse.getMessage());
        }

    }

    private void storeInfoToJsonfirst(JSONObject json) {
        try {
            client.put("cLMP", json.get("lmp"));
            client.put("cEDD", json.get("edd"));
            client.put("cLastChildAge", json.get("lastChildAge"));
            client.put("cHistoryComplicated", json.get("complicatedHistory"));
            client.put("cHistoryComplicatedContent", 0);
            client.put("cBloodGroup", json.get("bloodGroup"));
            client.put("cBoy", json.get("boy"));
            client.put("cGirl", json.get("girl"));
            client.put("cPara", json.get("para"));
            client.put("cGravida", json.get("gravida"));
            client.put("cHeight", json.get("height"));
            client.put("cMobileNo", json.get("mobileNo"));
            client.put("chGRHNo", json.get("houseGRHoldingNo"));
            client.put("hasAbortionInformation", "No");
            client.put("deathStatus", "");
            client.put("cLMPStatus", 0);

            for (int i = 1; i <= 5; i++) {
                client.put("cTT" + i, json.get("tt" + i));
                client.put("cTT" + i + "Date", json.get("tt" + "Date" + i));
            }

        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:\"" + responseID + "\"," +
                "providerId:\"" + ProviderInfo.getProvider().getProviderCode() + "\"," +
                "complicatedHistory:\"\"," +  //temp
                "complicatedHistoryNote:\"9\",";    //temp

        for (int i = 1; i <= 5; i++)
            queryString += "ttDate" + i + ":\"\",";

        queryString = queryString.substring(0, queryString.length() - 1);
        queryString += "}";
        return new JSONObject(queryString);
    }

    private void getSpecialCheckBoxes(JSONObject json) {
        String complicatedHistories = "";
        for(int i = 0; i < deliveryHistoryMapping.size(); i++) {
            if(getCheckbox(deliveryHistoryMapping.get(i).second).isChecked()) {
                complicatedHistories += String.valueOf(i+1)+",";
            }
        }
        if(!complicatedHistories.equals("")) {
            complicatedHistories = complicatedHistories.substring(0,complicatedHistories.length()-1 );
        }
        try{
            json.put("complicatedHistoryNote", complicatedHistories);
        } catch (JSONException JSE) {
            Log.e(LOGTAG, "Error:\n\t" + JSE.getStackTrace());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            if (woman != null) {
                json.put("pregNo", woman.getPregNo());
            }
            else
                json.put("pregNo","\"\"");

            //To enter 0 if ""
            int year = (getEditText(R.id.lastChildYear).getText().toString()).isEmpty()?0:Integer.parseInt(getEditText(R.id.lastChildYear).getText().toString());
            int month = (getEditText(R.id.lastChildMonth).getText().toString()).isEmpty()?0:Integer.parseInt(getEditText(R.id.lastChildMonth).getText().toString());
            int feet = (getEditText(R.id.heightFeet).getText().toString()).isEmpty()?0:Integer.parseInt(getEditText(R.id.heightFeet).getText().toString());
            int inch = (getEditText(R.id.heightInch).getText().toString()).isEmpty()?0:Integer.parseInt(getEditText(R.id.heightInch).getText().toString());

            month = year * 12 + month;
            feet = feet * 12 + inch;

            json.put("lastChildAge", month);
            json.put("height", feet);
            getSpecialCheckBoxes(json);

        } catch (JSONException jse) {

        }
    }

    public void resetFields(View view){
        Utilities.Reset(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "reset");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "reset");
        Utilities.InVisibleButton(this, R.id.client_edit_Button);
        Utilities.InVisibleButton(this, R.id.client_update_Button);
        Utilities.InVisibleButton(this, R.id.client_New_preg_Button);
        Utilities.VisibleButton(this, R.id.client_Save_Button);
    }

    public void editFields(View view){
        Utilities.Enable(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");
        Utilities.InVisibleButton(this, R.id.client_edit_Button);
        Utilities.VisibleButton(this, R.id.client_update_Button);
    }
}