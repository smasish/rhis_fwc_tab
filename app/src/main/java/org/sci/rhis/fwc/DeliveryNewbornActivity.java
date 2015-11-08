package org.sci.rhis.fwc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeliveryNewbornActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                                View.OnClickListener,
                                                                                CompoundButton.OnCheckedChangeListener{

    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;


    private PregWoman mother;
    private  JSONObject deliveryJsonObj;
    private ProviderInfo provider;
    private int flag =0;
    private int integerRecd;
    private int currentChildNo = 0;

    final private String SERVLET = "newborn";
    final private String ROOTKEY= "newbornInfo";
    private  final String LOGTAG    = "FWC-NEWBORN";

    AsyncNewbornInfoUpdate newbornInfoQueryTask;
    AsyncNewbornInfoUpdate newbornInfoUpdateTask;

    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_newborn);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initialize(); //initialize the inherited maps
        /**get the intent*/
        Intent intent = getIntent();
        System.out.print("Get Intent?" + "Under this");

        integerRecd = intent.getIntExtra("Layout", flag);

        switch(integerRecd) {
            case 1:
                Utilities.MakeInvisible(this, R.id.notDetected);
                jsonEditTextMap.get("birthStatus").setText("1");

                break;
            case 2:
                Utilities.MakeInvisible(this, R.id.notDetected);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);
                jsonEditTextMap.get("birthStatus").setText("2");
                break;

            case 3:
                Utilities.MakeInvisible(this, R.id.deliveryWipe);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);
                jsonEditTextMap.get("birthStatus").setText("3");
                break;
        }

        //Intent outComePlace = getIntent();
        String str = intent.getStringExtra("DeliveryJson");
        Log.d(LOGTAG, "Get Json As:\t"+ str);

        Spinner referSpinner= (Spinner)findViewById(R.id.deliveryChildReferCenterNameSpinner);

        final List<String> newbornReferReasonList = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornReferReasonList);
        multiSelectionSpinner.setSelection(new int[]{});

        getCheckbox(R.id.deliveryChildReferCheckBox).setOnCheckedChangeListener(this);

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        try {
            deliveryJsonObj = new JSONObject(str);
            if(isImmature(mother, deliveryJsonObj.get("dDate").toString())){
                jsonTextViewsMap.get("immature").setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            if(getIntent().hasExtra("NewbornJson")) { //check if new bron info is given
                //try{
                restoreNewbornFromJSON(new JSONObject(getIntent().getStringExtra("NewbornJson")));

            } else { //retrieve it from net
                //Get the existing information
                newbornInfoQueryTask = new AsyncNewbornInfoUpdate(this);

                JSONObject jso = buildQueryHeader(true);
                newbornInfoQueryTask.execute(jso.toString(), SERVLET, ROOTKEY);

            }
        } catch (JSONException JSE) {
            JSE.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery_newborn, menu);
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
        Log.d("Delivery-Newborn", result != null ? result: "NO RESPONSE");
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;
            if(json.has("result")) { //if result key is present
               if(json.getString("result").equals("true") &&
                  json.getString("hasNewbornInfo").equals("Yes")) {//then see if children exist i.e. result:true, hasNewbornInfo:Yes
                   currentChildNo = Integer.valueOf(json.getString("count"));
               } else { // no child information is available
                   currentChildNo = 0;
               }
                jsonEditTextMap.get("childno").setText(String.valueOf(currentChildNo+1));
                //Utilities.Disable(this, jsonEditTextMap.get("childno").getId());
            }

            //int size = json.names().length();


            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d("Delivery-Newborn", "1.Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            //currentChildNo = size2;
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }
        Utilities.Enable(this, R.id.DeliveryNewBornLayout);
    }

    @Override
    protected void initiateCheckboxes() {
// For New born
        jsonCheckboxMap.put("stimulation", getCheckbox(R.id.stimulation));
        jsonCheckboxMap.put("bagNMask", getCheckbox(R.id.bag_n_mask));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.deliveryChildReferCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        // for New born layout
        jsonEditTextMap.put("childno",  getEditText(R.id.deliveryNewBornNo));
        jsonEditTextMap.put("birthStatus", getEditText(R.id.deliveryNewBornConditionValue));
        jsonEditTextMap.put("weight",getEditText(R.id.deliveryNewBornWeightValue));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("immature",  getTextView(R.id.deliveryNewBornMaturity));
        jsonTextViewsMap.put("birthStatus",getTextView(R.id.deliveryNewBornConditionValue));
    }

    @Override
    protected void initiateSpinners() {
        // for New born Layout
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.deliveryChildReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.deliveryChildReferReasonSpinner));
    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {
        //for NewBorn
        jsonRadioGroupButtonMap.put("gender", Pair.create(
                        getRadioGroup(R.id.id_newBornSexRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryNewBornSon),
                                getRadioButton(R.id.deliveryNewBornDaughter)
                                //getRadioButton(R.id.deliveryNewBornNotDetected)
                        )
                )
        );

        jsonRadioGroupButtonMap.put("dryingAfterBirth", Pair.create(
                        getRadioGroup(R.id.id_newBornWipeRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryWipeYesButton),
                                getRadioButton(R.id.deliveryWipeNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("resassitation", Pair.create(
                        getRadioGroup(R.id.id_newBornResasscitationRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryResastationYesButton),
                                getRadioButton(R.id.deliveryResastationNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("chlorehexidin", Pair.create(
                        getRadioGroup(R.id.id_newBornChlorohexidineRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryPlacentaCuttingYesButton),
                                getRadioButton(R.id.deliveryPlacentaCuttingNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("skinTouch", Pair.create(
                        getRadioGroup(R.id.id_newBornChordCareRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryFittedWithMotherSkinYesButton),
                                getRadioButton(R.id.deliveryFittedWithMotherSkinNoButton))
                )
        );

        jsonRadioGroupButtonMap.put("breastFeed", Pair.create(
                        getRadioGroup(R.id.id_newBornBreastFeedingRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryBreastFeedingYesButton),
                                getRadioButton(R.id.deliveryBreastFeedingNoButton))
                )
        );
    }
    private HashMap<RadioGroup, Pair<RadioButton, RadioButton>> getRadioMap(int groupId, int yesId, int noId) {

        HashMap<RadioGroup, Pair<RadioButton, RadioButton>> temp
                = new HashMap<RadioGroup, Pair<RadioButton, RadioButton>>();
        temp.put(
                getRadioGroup(groupId),
                Pair.create(
                        getRadioButton(yesId),
                        getRadioButton(noId)
                )
        );
        return temp;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.deliveryChildReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.INVISIBLE;
            getTextView(R.id.deliveryChildReferCenterNameLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferCenterNameSpinner).setVisibility(visibility);
            getTextView(R.id.deliveryChildReferReasonLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferReasonSpinner).setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_saveNewbornButton) {
            newbornSaveToJson();
            Toast.makeText(this, "Newborn Saved Successfully", Toast.LENGTH_LONG).show();
            Log.e("Newborn", "Saved Newborn Successfully?");

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void newbornSaveToJson() {

        newbornInfoUpdateTask = new AsyncNewbornInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getSpecialCases(json);
            Log.d("DeliveryJsonFoundinSave", json.toString());
            Log.e("Servlet and Rootkey", SERVLET + " " + ROOTKEY);
            newbornInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {

            Log.d("Newborn", "JSON Exception: " + jse.getMessage());
        }
        finish();
    }

    private void restoreNewbornFromJSON(JSONObject json) {
        Utilities.setCheckboxes(jsonCheckboxMap, json);
        Utilities.setSpinners(jsonSpinnerMap, json);
        //updateRadioButtons(json);
        Utilities.setEditTexts(jsonEditTextMap, json);
        Utilities.setEditTextDates(jsonEditTextDateMap, json);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, json);
        //updateEditTextTimes(json);
        Log.d(LOGTAG, "Delivery Response Received:\n\t" + json.toString());
        //dJson = json;

        //TODO Make the fields non-modifiable
        Utilities.Disable(this, R.id.DeliveryNewBornLayout);
        //Utilities.MakeInvisible(this, R.id.btn_save_add_child);
    }

    public void getSpecialCases(JSONObject json) {
        try {
           json.put("immature", (jsonTextViewsMap.get("immature").getVisibility()==View.VISIBLE) ? "1" : "2");
           json.put("outcomeplace", deliveryJsonObj.getInt("dPlace"));
           json.put("outcomedate", deliveryJsonObj.getString("dDate"));
           json.put("outcometime", deliveryJsonObj.getString("dTime"));
           json.put("outcometype", deliveryJsonObj.getInt("dType"));
        } catch (JSONException jse) {

        }
    }


    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
               "providerid:"+ String.valueOf(provider.getProviderCode()) + "," +
                "pregno:" + mother.getPregNo() + "," +
                "newbornLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

          Log.e("Is there have Values?", queryString);

        return new JSONObject(queryString);
    }

    private boolean isImmature(PregWoman mother, String date) { // receive date in json format that is
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long days = 0;
        try {
            Date deliveryDate = sdf.parse(date);
            days = TimeUnit.DAYS.convert(mother.getLmp().getTime() - deliveryDate.getTime(), TimeUnit.DAYS);

        } catch (ParseException PE) {

        }
        return (days < (37 * 7) );
    }
}
