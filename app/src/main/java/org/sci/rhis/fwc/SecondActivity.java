
package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

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

    private int countSaveClick=0;

    private AsyncClientInfoUpdate clientInfoQueryTask;
    private AsyncClientInfoUpdate clientInfoUpdateTask;

    final private String SERVLET = "handlepregwomen";
    final private String ROOTKEY = "pregWomen";
    private  final String LOGTAG    = "FWC-INFO";
    private HashMap<String, String> lmp_edd = null;

    private BigInteger responseID = BigInteger.valueOf(0);
    private EditText lmpEditText;
    private EditText eddEditText;
    private JSONObject client;
    private CustomDatePickerDialog datePicker = null;
    private String deliveryDate = null;

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

                            Date edd = Utilities.addDateOffset(lmp, PregWoman.PREG_PERIOD);
                            eddEditText.setText(uiFormat.format(edd));

                        } catch (ParseException PE) {

                        }
                    }
                }
        );

        addListenerOnButton();
    }

    public void startSearch(View view) {
        Spinner searchOptions = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        EditText searchableId = (EditText) findViewById(R.id.searchableTextId);
        //TODO - review - is there a better way to send delivery date
        deliveryDate = "";

        long index = (searchOptions.getSelectedItemId() + 1);
        String stringId = (String) searchOptions.getSelectedItem();
        long id;
        try {
            id = Long.valueOf(searchableId.getText().toString());
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, "Invalid ID \n\t" + nfe.toString());
            Toast.makeText(this, "Invalid ID typed. Please provide valid ID ...", Toast.LENGTH_LONG).show();
            getView_NoClient();
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

        /*TextView mHealthIdLayout = (TextView) findViewById(R.id.health_id);
        mHealthIdLayout.setVisibility(View.VISIBLE);*/

        Utilities.MakeVisible(this, R.id.health_id);

        /*TextView healthId = (TextView) findViewById(R.id.health_id);
        healthId.setText(String.valueOf(stringId) + ": " + String.valueOf(id));*/
        getTextView(R.id.health_id).setText(String.valueOf(stringId) + ": " + String.valueOf(id));

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
        setSpecialCase(json);

    }

    private void setSpecialCase(JSONObject json){
        try {
            if(json.has("regDate") && json.has("regSerialNo")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String regDate = json.getString("regDate");
                if(regDate.equals(""))
                    return;
                Date regD = sdf.parse(regDate);
                String regSerial = json.getString("regSerialNo") + "/"
                        + json.getString("regDate").split("-")[0].substring(2);
                regSerial += " \t " + new SimpleDateFormat("dd/MM/yyyy").format(regD);
                getTextView(R.id.reg_NO).setText(Utilities.ConvertNumberToBangla(regSerial));
            }

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception Thrown(test):\n ");
            Utilities.printTrace(jse.getStackTrace());
        } catch (ParseException pe) {

        }
    }

    @Override
    public void callbackAsyncTask(String result) { //Get results back from healthId search
        Log.d("result:", result);

        try {
            JSONObject json = new JSONObject(result);
            String key;

            //DEBUG response from servlet
            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d(LOGTAG, "Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            ///////////////////////callback for client servlet////////////////////////////////////////////////////

            if (!json.has("responseType"))
            {
                client = json;
                if (json.getString("False").equals("")) { //Client exists
                    populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                    //Utilities.VisibleLayout(this, R.id.client_intro_layout);
                    Utilities.MakeVisible(this, R.id.client_intro_layout);
                    Utilities.Disable(this, R.id.client_intro_layout);

                    if (json.getString("cSex").equals("2") && Integer.parseInt(json.getString("cAge")) >= 15 && Integer.parseInt(json.getString("cAge")) <= 49) {
                        //Elco Women
                        Log.d(LOGTAG, "CREATING PREGNANCY REMOTE" + client.toString());
                        woman = PregWoman.CreatePregWoman(json);
                        responseID = new BigInteger(json.get("cHealthID").toString());
                        if (woman != null) {//Elco Women with pregInfo
                            manipulateJson(json);
                            populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                            getView_WomenWithPregInfo();
                        } else { //Elco Women without pregInfo
                            getView_WomenWithOutPregInfo();
                        }
                    } else {//Men & Not-Elco Women
                        Toast.makeText(this, "Male or Not Eligible Women for Pregnancy", Toast.LENGTH_LONG).show();
                        getView_NoClient();
                        //Utilities.VisibleLayout(this, R.id.client_intro_layout);
                        Utilities.MakeVisible(this, R.id.client_intro_layout);
                    }
                } else {//Client doesn't exist
                    Toast.makeText(this, "Provided information is not valid! Please try again....", Toast.LENGTH_LONG).show();
                    getView_NoClient();
                }
            }

            //////////////////callback for PregInfo servlet//////////////////////////////////////////////////////

            else {
                if(!json.getString("pregNo").equals("")) {
                    client.put("regSerialNo", json.get("regSerialNo"));
                    client.put("regDate", json.get("regDate"));
                    client.put("highRiskPreg", json.get("highRiskPreg"));
                    client.put("cPregNo", json.get("pregNo"));
                    client.put("cNewMCHClient", "false");

                    Log.d(LOGTAG, "CREATING PREGNANCY LOCAL" + client.toString());

                    woman = PregWoman.CreatePregWoman(client);
                    responseID = new BigInteger(client.get("cHealthID").toString());

                    getView_WomenWithPregInfo();
                }
                else{
                    Toast.makeText(this, "Provided information is not valid! Please try again", Toast.LENGTH_LONG).show();
                    getView_WomenWithOutPregInfo();
                }
            }
        }
        catch (JSONException jse) {
            Log.d(LOGTAG, "JSON Exception Thrown( At callbackAsyncTask ):\n ");
            jse.printStackTrace();
        }

    }

    public void addListenerOnButton() {

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

        /*if(!checkClientInfo()) {
            return;
        }*/

        if ( woman != null && woman.isEligibleFor(PregWoman.PREG_SERVICE.DELIVERY)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivityForResult(intent, ActivityResultCodes.DELIVERY_ACTIVITY);
        } else if(woman != null && woman.getAbortionInfo() == 1) {
            askToStartPAC();
        } else {
            deliveryWithoutPregInfo();
        }
    }

    public void startDeath(View view) {
        Intent intent = new Intent(this, DeathActivity.class);

            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);

    }

    public void startPAC(View view) {
        Intent intent = new Intent(this, PACActivity.class);
        //TODO - Remove
        if(true) {
            Utilities.showBiggerToast(this, R.string.PACWarning);
            return;
        }
        //


        if(woman != null && woman.isEligibleFor(PregWoman.PREG_SERVICE.PAC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Utilities.showBiggerToast(this, R.string.PACWarning);
        }
    }


    public void startPNC(View view) {
        Intent intent = new Intent(this, PNCActivity.class);
        if (checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.PNC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {

            /*Toast toast = Toast.makeText(this, R.string.PNCWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();*/
            Utilities.showBiggerToast(this, R.string.PNCWarning);
        }
    }

    private boolean checkClientInfo() {
        if (woman == null) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void askToStartPAC() {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
        alertDialog.setTitle("LOGOUT CONFIRMATION");
        alertDialog.setMessage(getString(R.string.StartPACfromDeliveryMessage));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startPAC(findViewById(R.id.pacButton));
                    }
                });

        alertDialog.show();
    }

    private void deliveryWithoutPregInfo() {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
        alertDialog.setTitle("DELICVERY CONFIRMATION");
        alertDialog.setMessage(getString(R.string.DeliveryWithoutPregnancyPrompt));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handleDeliveryDatePopUp();
                    }
                });

        alertDialog.show();
    }

    private void startDeliveryWithoutPregInfo(String result) {
        Log.d(LOGTAG, "PSUDO PREG INFO RETURNED:\n\t" + result);
        callbackAsyncTask(result); //will create PregWoman
        woman.setActualDelivery(lmp_edd.get("edd"), "dd/MM/yyyyy");
        woman.setDeliveryInfo(1);
        //in this case actual delivery date was the edd

        Intent intent = new Intent(this, DeliveryActivity.class);
        intent.putExtra("PregWoman", woman);
        intent.putExtra("Provider", ProviderInfo.getProvider());
        intent.putExtra("actualDeliveryDateAvailable", true);
        startActivityForResult(intent, ActivityResultCodes.DELIVERY_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
        alertDialog.setIcon(R.drawable.logout);
        alertDialog.setTitle("LOGOUT CONFIRMATION");
        alertDialog.setMessage("আপনি কি বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultCodes.REGISTRATION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String healthId = data.getStringExtra("generatedId");
                getSpinner(R.id.ClientsIdentityDropdown).setSelection(NRC_ID_INDEX);
                getEditText(R.id.searchableTextId).setText(healthId);
                startSearch((ImageButton)findViewById(R.id.searchButton));
            }
        } else if (requestCode == ActivityResultCodes.ADV_SEARCH_ACTIVITY) {
            if (resultCode == RESULT_OK) {

                String str = data.getStringExtra("HealthId");
                getSpinner(R.id.ClientsIdentityDropdown).setSelection(data.getIntExtra("HealthIdType",0));
                getEditText(R.id.searchableTextId).setText(str);
                startSearch((ImageButton)findViewById(R.id.searchButton));
            }
        } else if (requestCode == ActivityResultCodes.DELIVERY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean hasDeliveryInformation = data.getBooleanExtra("hasDeliveryInformation", false);
                woman.setDeliveryInfo(hasDeliveryInformation ? 1 : 0);
            }
        }
    }

    public void onClickSaveClient(View view) {
        countSaveClick++;
        if( countSaveClick == 2 ) {
            saveClientToJson();
            getButton(R.id.client_Save_Button).setText("Save");
            countSaveClick = 0;

        } else if(countSaveClick == 1) {
            if(!hasTheRequiredFileds()) {
                countSaveClick = 0;
                return;
            }

            Utilities.Disable(this, R.id.clients_info_layout);
            Utilities.DisableField(this, R.id.Clients_House_No);
            Utilities.DisableField(this, R.id.Clients_Mobile_no);

            getButton( R.id.client_Save_Button).setText("Confirm");
            Utilities.Enable(this, R.id.client_Cancel_Button);
            Utilities.Enable(this, R.id.client_Save_Button);
            Utilities.MakeVisible(this, R.id.client_Save_Button);
            Utilities.MakeVisible(this, R.id.client_Cancel_Button);

            Utilities.showBiggerToast(this, R.string.DeliverySavePrompt);

            /*Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();*/
        }
    }

    public void onClickCancelClient(View view) {
        if(countSaveClick == 1) {
            countSaveClick = 0;
            Utilities.Enable(this, R.id.clients_info_layout);
            Utilities.EnableField(this, R.id.Clients_House_No,"edit");
            Utilities.EnableField(this, R.id.Clients_Mobile_no,"edit");

            getButton(R.id.client_Save_Button).setText("Save");
            //TODO - Review
            Utilities.MakeInvisible(this, R.id.client_Cancel_Button);
        }
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
        //NOTE: The following JSON keys are not present in the Servlet response
        //The response will be manipulated to trick Checkbox handlers
        //so everything is handled in a general way.
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

        jsonEditTextDateMapSave.put("ttDate1", getEditText(R.id.ttDate1));
        jsonEditTextDateMapSave.put("ttDate2", getEditText(R.id.ttDate2));
        jsonEditTextDateMapSave.put("ttDate3", getEditText(R.id.ttDate3));
        jsonEditTextDateMapSave.put("ttDate4", getEditText(R.id.ttDate4));
        jsonEditTextDateMapSave.put("ttDate5", getEditText(R.id.ttDate5));
    }

    @Override
    protected void initiateRadioGroups() {
    }

    private void saveClientToJson() {
        saveClientToJson(this, true);
    }

    private void saveClientToJson(AsyncCallback callback, boolean storeLocalJson) {
        clientInfoUpdateTask = new AsyncClientInfoUpdate(callback);
        JSONObject json;
        //hasTheRequiredFileds();
        try {
            json = buildQueryHeader(false);

            if(!storeLocalJson) {

                json.put("lmp", new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(lmp_edd.get("lmp"))));
                json.put("edd", new SimpleDateFormat("yyyy-MM-dd").format(Utilities.addDateOffset(new SimpleDateFormat("dd/MM/yyyy").parse(lmp_edd.get("lmp")), PregWoman.PREG_PERIOD )));
                //edd - always calculated from lmp , actual delivery date is saved inside pregwoman object through setActualDelivery
            }
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMapSave, json);
            Utilities.getCheckboxesBlank(jsonCheckboxMapSave, json);
            Utilities.getSpinners(jsonSpinnerMapSave, json);
            getSpecialCases(json);
            Log.d(LOGTAG, "PREPARE PREGNANCY JSON:\n\t" + json.toString());
            //if(storeLocalJson) {
                storeInfoToJsonfirst(json);
            //}
            clientInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        } catch (ParseException pe) {
            Log.e (LOGTAG, "Date parsing Exception");
            Utilities.printTrace(pe.getStackTrace());
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
            Log.e(LOGTAG, "JSON Exception:");
            Utilities.printTrace(JSE.getStackTrace());
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
        if(!complicatedHistories.equals("")) { //Get rid of trailing ,
            complicatedHistories = complicatedHistories.substring(0,complicatedHistories.length()-1 );
        }
        try{
            json.put("complicatedHistoryNote", complicatedHistories);
        } catch (JSONException JSE) {
            Log.e(LOGTAG, "Error:\n\t");
            Utilities.printTrace(JSE.getStackTrace());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("pregNo", woman != null ? woman.getPregNo():"");
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

    public void handleAddNewPregnancy(View view) {
        if(woman != null) {
            woman = null;
            resetFields(view);
        }
    }

    private HashMap<String, String> handleDeliveryDatePopUp() {
        final Dialog dialog = new Dialog(this);

        if(lmp_edd == null ) {
            lmp_edd = new HashMap<>();
        }

        //Remove title bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.date_selector);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //Guide lmp backcalculation

        final EditText dDate = (EditText) dialog.findViewById(R.id.id_delivery_date);

        dDate.addTextChangedListener(
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
                            EditText estLmp = (EditText)dialog.findViewById(R.id.estimatedLmpDate);
                            if(!dDate.getText().toString().equals("")) {
                                deliveryDate = dDate.getText().toString();
                                Date d_date = Utilities.addDateOffset(uiFormat.parse(dDate.getText().toString()), -280);
                                estLmp.setText(uiFormat.format(d_date));
                            }

                        } catch (NumberFormatException NFE) {
                            Log.e(LOGTAG, NFE.getMessage());
                            Utilities.printTrace(NFE.getStackTrace());
                        } catch (ParseException pe) {
                            Log.e(LOGTAG, pe.getMessage());
                            Utilities.printTrace(pe.getStackTrace());
                        }
                    }
                }
        );


        ((Button) dialog.findViewById(R.id.saveEstimatedDeliveryOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryDate = ((EditText) dialog.findViewById(R.id.id_delivery_date)).getText().toString();
                String estimatedLmp = ((EditText) dialog.findViewById(R.id.estimatedLmpDate)).getText().toString();
                //if()
                lmp_edd.put("edd", deliveryDate);
                lmp_edd.put("lmp", estimatedLmp);
                handleDialogButtonClick(dialog, v);
            }
        });

        ((Button)dialog.findViewById(R.id.saveEstimatedDeliveryCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDialogButtonClick(dialog, v);
            }
        });


        int listenables [] = {R.id.Date_Picker_Button, R.id.imageViewDeliveryDate};

        for (int i : listenables) {

            dialog.findViewById(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickDate(v, dialog);
                }
            });
        }

        return lmp_edd;
    }

    private void handleDialogButtonClick(Dialog dialog, View view) {
        if(dialog != null) {
            dialog.dismiss();
        }
        if(view.getId() == R.id.saveEstimatedDeliveryOk) {
            saveClientToJson(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    startDeliveryWithoutPregInfo(result);
                }
            }, false);
        }
    }

    void pickDate(View view, Dialog dialog) {
        if(datePicker == null) {
            datePicker = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        }
        switch(view.getId()) {
            case R.id.imageViewDeliveryDate:
                datePicker.show((EditText)dialog.findViewById(R.id.id_delivery_date));
                break;
            case R.id.Date_Picker_Button:
                datePicker.show((EditText)dialog.findViewById(R.id.estimatedLmpDate));
                break;
        }
    }

    public void resetFields(View view){//OnClick Method
        Utilities.Reset(this, R.id.clients_info_layout);
        Utilities.Enable(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");

        Utilities.MakeInvisible(this, R.id.client_edit_Button);
        Utilities.MakeInvisible(this, R.id.client_update_Button);
        Utilities.MakeInvisible(this, R.id.client_New_preg_Button);
        Utilities.MakeVisible(this, R.id.client_Save_Button);
    }

    public void editFields(View view){//OnClick Method
        Utilities.Enable(this, R.id.clients_info_layout);
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");

        Utilities.MakeInvisible(this, R.id.client_edit_Button);
        Utilities.MakeVisible(this, R.id.client_update_Button);
        Utilities.Enable(this, R.id.client_Save_Button);
    }

    private void getView_WomenWithPregInfo() {
        woman.UpdateUIField(this);

        Utilities.Disable(this, R.id.clients_info_layout);
        Utilities.DisableField(this, R.id.Clients_House_No);
        Utilities.DisableField(this, R.id.Clients_Mobile_no);

        Utilities.MakeInvisible(this, R.id.client_Save_Button);
        Utilities.MakeInvisible(this, R.id.client_update_Button);
        Utilities.MakeVisible(this, R.id.client_Cancel_Button);
        Utilities.MakeVisible(this, R.id.client_New_preg_Button);

        Utilities.Enable(this, R.id.client_edit_Button);
        Utilities.Enable(this, R.id.client_New_preg_Button);

        Utilities.VisibleLayout(this, R.id.table_Layout);
        Utilities.VisibleLayout(this, R.id.client_intro_layout);
        Utilities.VisibleLayout(this, R.id.clients_info_layout);
    }

    private void getView_WomenWithOutPregInfo(){
        Utilities.EnableField(this, R.id.Clients_House_No, "edit");
        Utilities.EnableField(this, R.id.Clients_Mobile_no, "edit");
        Utilities.Enable(this, R.id.client_Save_Button);

        Utilities.MakeVisible(this, R.id.client_Save_Button);
        Utilities.MakeInvisible(this, R.id.client_Cancel_Button);
        Utilities.MakeInvisible(this, R.id.client_edit_Button);
        Utilities.MakeInvisible(this, R.id.client_New_preg_Button);

        Utilities.VisibleLayout(this, R.id.table_Layout);
        Utilities.VisibleLayout(this, R.id.clients_info_layout);
        Utilities.VisibleLayout(this, R.id.client_intro_layout);
    }
    private void getView_NoClient(){
        Utilities.InVisibleLayout(this, R.id.client_intro_layout);
        Utilities.InVisibleLayout(this, R.id.clients_info_layout);
        Utilities.InVisibleLayout(this, R.id.table_Layout);
    }

    private boolean hasTheRequiredFileds() {
        String textFileds [] = {"para", "gravida", "boy", "girl",};
        String minTextFields [] = {"para", "gravida"};

        String fields = "";
        boolean isEmpty = false;
        boolean isParaZero = false;

        try {
            isParaZero = (Integer.valueOf(jsonEditTextMap.get("para").getText().toString()) == 0);
        } catch (NumberFormatException nfe) {
            isParaZero = false;
        }

        String searchableFields [] = isParaZero ? minTextFields : textFileds;

        for( int i = 0;i< searchableFields.length && !isEmpty; ++i) {
            fields = searchableFields[i];
            if(jsonEditTextMap.get(fields).getText().toString().equals("")) {
                isEmpty = true;
            }
        }

        String textFiledsDate [] = {"lmp", "edd"};
        String fieldsDate = "";
        boolean isEmptyFields = false;

        for( int i = 0;i< textFiledsDate.length && !isEmptyFields; ++i) {
            fieldsDate = textFiledsDate[i];
            if(jsonEditTextDateMapSave.get(fieldsDate).getText().toString().equals("")) {
                isEmptyFields = true;
            }
        }

        boolean lastchild =  (getEditText(R.id.lastChildYear).getText().toString().equals("") ||
                getEditText(R.id.lastChildMonth).getText().toString().equals("")) & !isParaZero;


        //TODO - there may not exist a village
        if(isEmpty || lastchild || isEmptyFields) {

            Utilities.showBiggerToast(this, R.string.NRCSaveWarning);
            return false;
        }

        return true;
    }
}