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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeliveryActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                               View.OnClickListener,
                                                                               CompoundButton.OnCheckedChangeListener{

    //UI References
    //private DatePickerDialog fromDatePickerDialog;
    private ImageView deliveryDateButton;
    private ImageView admissionDateButton;
    private ImageView anyDateButton;
    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    private int deliveryHour;
    private int deliveryMinute;
    private int currentChildCount = 0;
    private JSONObject dJson;
    private PregWoman mother;
    private ProviderInfo provider;
    private ArrayAdapter<String> childAdapter;
    private ArrayList<String> childList;
    private Intent passJson;
    private String existingChildInfo;
    final private String SERVLET = "delivery";
    final private String ROOTKEY = "deliveryInfo";
    private  final String LOGTAG = "FWC-DELIVERY";



    AsyncDeliveryInfoUpdate deliveryInfoQueryTask;
    AsyncDeliveryInfoUpdate deliveryInfoUpdateTask;



    private MultiSelectionSpinner multiSelectionSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // For Multi Select Spinner
        final List<String> dtreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_treatment);
        multiSelectionSpinner.setItems(dtreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dadvicelist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Advice_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_advice);
        multiSelectionSpinner.setItems(dadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.id_spinner_refer_delivery_cause);
        multiSelectionSpinner.setItems(dreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});


        initialize(); //super class
        Spinner spinners[] = new Spinner[3];
        spinners[0] = (Spinner) findViewById(R.id.delivery_placeDropdown);
        spinners[1] = (Spinner) findViewById(R.id.id_facility_name_Dropdown);
        spinners[2] = (Spinner) findViewById(R.id.delivery_typeDropdown);

        for(int i = 0; i < spinners.length; ++i) {
            spinners[i].setOnItemSelectedListener(this);
        }

        getEditText(R.id.id_delivery_date).setOnClickListener(this);
        getEditText(R.id.id_admissionDate).setOnClickListener(this);
        getCheckbox(R.id.id_delivery_refer).setOnCheckedChangeListener(this);

        //custom date picker
        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.imageViewDeliveryDate, (EditText)findViewById(R.id.id_delivery_date));
        datePickerPair.put(R.id.imageViewAdmissionDate, (EditText)findViewById(R.id.id_admissionDate));

        //custom time picker
        timePickerDialog = new CustomTimePickerDialog(this);

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");

        provider = getIntent().getParcelableExtra("Provider");

       // Log.e("Is there Found mother dPlace?", "" + mother.getHealthId());
        //get info from database
        String queryString = "";
                /* =
                "{" +
                "healthid:" + mother.getHealthId() + "," +
                "pregno:" + mother.getPregNo() + "," +
                "deliveryLoad:" + "retrieve" +
                "}"*/
        //String
        try {
            boolean isMother = true; //just being verbose for better readability
            boolean retireve = true; //just being verbose for better readability
            queryString = buildQueryHeader(isMother, retireve).toString();
            Log.d(LOGTAG, "Sending JSON:\n\t" + queryString);
        } catch (JSONException JSE) {
            Log.e("Delivery", "Could not build query String: " + JSE.getMessage());
        }
        deliveryInfoQueryTask = new AsyncDeliveryInfoUpdate(this);
        deliveryInfoQueryTask.execute(queryString, SERVLET, ROOTKEY); //Get Mother Info
        getExistingChild();  //get child Info

        LinearLayout mNewbornLayout = (LinearLayout) findViewById(R.id.newborn_Tabla_Layout);
        mNewbornLayout.setVisibility(View.VISIBLE);
        passJson = new Intent(this, DeliveryNewbornActivity.class);

        //disable delivery result
        Utilities.Disable(this,R.id.id_deliveryResultLayout);
    }

    @Override
    public void callbackAsyncTask(String result) {
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;

            //DEBUG
            for (Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                Log.d(LOGTAG, "Rcvd Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            //populate the fields if the previous delivery information exist
            if(json.getString("dNew").equals("No")) {
                Utilities.setCheckboxes(jsonCheckboxMap, json);
                Utilities.setSpinners(jsonSpinnerMap, json);
                Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, json);
                updateRadioButtons(json);
                Utilities.setEditTexts(jsonEditTextMap, json);
                Utilities.setEditTextDates(jsonEditTextDateMap, json);
                updateEditTextTimes(json);
                Log.d(LOGTAG, "Delivery Response Received:\n\t" + json.toString());
                dJson = json;

                //TODO Make the fields non-modifiable
                Utilities.Disable(this, R.id.delivery_info_layout);
                Utilities.MakeInvisible(this, R.id.btn_save_add_child);
            }

        } catch (JSONException jse) {
            jse.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delevery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // autom-atically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        LinearLayout [] section;
        switch (parent.getId()) {
            case R.id.delivery_placeDropdown:

                section = new LinearLayout[3];
                section [0] = (LinearLayout) findViewById(R.id.id_facililties_section_layout);
                section [1] = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                section [2] = (LinearLayout) findViewById(R.id.id_Epcotomi_section_layout);

                for(int i = 0; i < section.length; ++i) {
                    section[i].setVisibility( position == 1? View.GONE:View.VISIBLE); //0 - home
                }
                break;
            case R.id.id_facility_name_Dropdown:

                LinearLayout  faclityAdmission = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                faclityAdmission.setVisibility((position == 5 || position == 6) ? View.GONE:View.VISIBLE);
                //4 - UH&FWC 5 - CC
                break;
            case R.id.delivery_typeDropdown:
                section = new LinearLayout[2];
                section [0] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);
                section [1] = (LinearLayout) findViewById(R.id.id_DeliveryManagementLayout);
                //section [] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);
                //section [] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);

                for(int i = 0; i < section.length; ++i) {
                    //do nothing now
                    //section[i].setVisibility( position != 2 ? View.GONE:View.VISIBLE); //0 - abortion
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.id_delivery_refer) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            getTextView(R.id.id_refer_facility_name).setVisibility(visibility);
            getSpinner(R.id.id_spinner_refer_facilities).setVisibility(visibility);
            getTextView(R.id.id_refer_delivery_cause).setVisibility(visibility);
            getSpinner(R.id.id_spinner_refer_delivery_cause).setVisibility(visibility);

            if(!isChecked)
            getTextView(R.id.id_refer_facility_name).setText("");
            getSpinner(R.id.id_spinner_refer_facilities).setSelection(0);
            getTextView(R.id.id_refer_delivery_cause).setText("");
            getSpinner(R.id.id_spinner_refer_delivery_cause).setSelection(0);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() != null && view.getTag().equals("DateField")) {
            datePickerDialog.show(datePickerPair.get(view.getId()));
        } else

        if(view.getId() == R.id.id_saveDeliveryButton) {
            saveToJson();

            LinearLayout mNewbornLayout = (LinearLayout) findViewById(R.id.newborn_Tabla_Layout);
            mNewbornLayout.setVisibility(View.VISIBLE);

        }

        if(passJson.hasExtra("NewbornJson")) { //when adding new child remove the reference of the old
            passJson.removeExtra("NewbornJson");
        } else {
            passJson.putExtra("childno", currentChildCount);
        }

        if(view.getId()==R.id.newbornAddButton){
        //Intent intent = new Intent(this, DeliveryNewbornActivity.class);
        passJson.putExtra("Layout", 1);
            passJson.putExtra("DeliveryJson",dJson.toString());

            if(checkClientInfo() && mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)) {
                passJson.putExtra("PregWoman", mother);
                passJson.putExtra("Provider", ProviderInfo.getProvider());
                Log.d(LOGTAG, dJson.toString());
                startActivity(passJson);
            } else {
                Toast.makeText(this, "Newborn cannot be added, verify ...", Toast.LENGTH_LONG).show();
            }

        }
        else  if(view.getId()==R.id.deathFreshButton){

            passJson.putExtra("Layout", 2);
            passJson.putExtra("DeliveryJson", dJson.toString());
            if(checkClientInfo() && mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)) {
                passJson.putExtra("PregWoman", mother);
                passJson.putExtra("Provider", ProviderInfo.getProvider());
                Log.d(LOGTAG, dJson.toString());
                startActivity(passJson);
            } else {
                Toast.makeText(this, "Too Late for PNC, verify ...", Toast.LENGTH_LONG).show();
            }
        }

        else if(view.getId()==R.id.deathmaceratedButton){

            passJson.putExtra("Layout", 3);
            passJson.putExtra("DeliveryJson", dJson.toString());
            if(checkClientInfo() && mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)) {
                passJson.putExtra("PregWoman", mother);
                passJson.putExtra("Provider", ProviderInfo.getProvider());
                Log.d(LOGTAG, dJson.toString());
                startActivity(passJson);
            } else {
                Toast.makeText(this, "Too Late for PNC, verify ...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    public void pickTime(View view) {

        timePickerDialog.show(
                (EditText) findViewById(R.id.delivery_time_hour),
                (EditText) findViewById(R.id.delivery_time_minute),
                (Spinner) findViewById(R.id.delivery_time_Dropdown)
        );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void initiateCheckboxes() {
        //AMTSL
        jsonCheckboxMap.put("dOxytocin", getCheckbox(R.id.oxytocin));
        jsonCheckboxMap.put("dTraction", getCheckbox(R.id.controlChordTraction));
        jsonCheckboxMap.put("dUMassage", getCheckbox(R.id.uterusMassage));

        //Complicacy
        jsonCheckboxMap.put("dLateDelivery", getCheckbox(R.id.id_delayedDelivery));
        jsonCheckboxMap.put("dBloodLoss", getCheckbox(R.id.id_BloodLoss));
        jsonCheckboxMap.put("dBlockedDelivery", getCheckbox(R.id.id_blockedDelivery));
        jsonCheckboxMap.put("dPlacenta", getCheckbox(R.id.id_blockedPlacenta));
        jsonCheckboxMap.put("dHeadache", getCheckbox(R.id.id_heavyHedache));
        jsonCheckboxMap.put("dBVision", getCheckbox(R.id.id_blurryVision));
        jsonCheckboxMap.put("dOBodyPart", getCheckbox(R.id.id_onlyHead));
        jsonCheckboxMap.put("dConvulsions", getCheckbox(R.id.id_convulsion));
        jsonCheckboxMap.put("dOthers", getCheckbox(R.id.id_deleiveryExtra));
        //refer
        jsonCheckboxMap.put("dRefer", getCheckbox(R.id.id_delivery_refer));

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("dPlace", getSpinner(R.id.delivery_placeDropdown)); //place
        jsonSpinnerMap.put("dType", getSpinner(R.id.delivery_typeDropdown)); //type
        //jsonSpinnerMap.put("dPlace", getSpinner(R.id.delivery_time_Dropdown)); //time
        jsonSpinnerMap.put("dCenterName", getSpinner(R.id.id_facility_name_Dropdown));
        jsonSpinnerMap.put("dAttendantDesignation", getSpinner(R.id.id_attendantTitleDropdown)); //deliveryAttendant
        jsonSpinnerMap.put("dReferCenter", getSpinner(R.id.id_spinner_refer_facilities)); //refercenter
    }

    @Override
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("dTreatment", getMultiSelectionSpinner(R.id.id_spinner_treatment)); //treatment
        jsonMultiSpinnerMap.put("dAdvice", getMultiSelectionSpinner(R.id.id_spinner_advice)); //advice
        jsonMultiSpinnerMap.put("dReferReason", getMultiSelectionSpinner(R.id.id_spinner_refer_delivery_cause)); //refer reason
    }

    @Override
    protected void initiateEditTexts() {
        //admission details
        jsonEditTextMap.put("dWard", getEditText(R.id.id_ward));
        jsonEditTextMap.put("dBed", getEditText(R.id.id_bed));

        //new born details
        jsonEditTextMap.put("dNoLiveBirth", getEditText(R.id.Live_born));
        jsonEditTextMap.put("dStillFresh",getEditText(R.id.Dead_born_fresh));
        jsonEditTextMap.put("dStillMacerated",getEditText(R.id.Dead_born_macerated));
        jsonEditTextMap.put("dNewBornBoy",getEditText(R.id.son));
        jsonEditTextMap.put("dNewBornGirl",getEditText(R.id.daughter));
        jsonEditTextMap.put("dNewBornUnidentified",getEditText(R.id.notDetected));

        //attendant name
        jsonEditTextMap.put("dAttendantName",getEditText(R.id.id_attendantName));
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("dDate", getEditText(R.id.id_delivery_date));
        jsonEditTextDateMap.put("dAdmissionDate", getEditText(R.id.id_admissionDate));
    }

    private void updateEditTextTimes(JSONObject jso) {

        //Too much internal knowledge assumed

        try {
            String time = jso.getString("dTime");
            if(!time.equals("")) {
                getEditText(R.id.delivery_time_hour).setText(time.substring(0,time.indexOf(':')));
                getEditText(R.id.delivery_time_minute).setText(time.substring(time.indexOf(':') + 1, time.indexOf(' ')));
                String ampm = time.substring(time.indexOf(' ') + 1);
                getSpinner(R.id.delivery_time_Dropdown).setSelection(time.substring(time.indexOf(' ')+1).equals("am")? 0:1);
            }
        } catch (JSONException jse) {

        }
    }

    private void getEditTextTime(JSONObject json) {

        String time = getEditText(R.id.delivery_time_hour).getText().toString();
        time += ":" + getEditText(R.id.delivery_time_minute).getText().toString();
        time += " " + getSpinner(R.id.delivery_time_Dropdown).getSelectedItem().toString();
        try {
            json.put("dTime", time);
        } catch (JSONException jse) {

        }
    }

    private void updateRadioButtons(JSONObject json) {

        RadioGroup radioGroup;
        try {
            if (json.getString("dEpisiotomy").equals("1")) {
                getRadioGroup(R.id.id_radioGroupEpctiomi).check(R.id.radioEpc_yes);

            } else if (json.getString("dEpisiotomy").equals("2")) {
                getRadioGroup(R.id.id_radioGroupEpctiomi).check(R.id.radioEpc_no);
            }

            if (json.getString("dMisoprostol").equals("1")) {
                getRadioGroup(R.id.id_radioGroupMisoprostol).check(R.id.radioMisoprostol_yes);

            } else if (json.getString("dMisoprostol").equals("2")) {
                getRadioGroup(R.id.id_radioGroupMisoprostol).check(R.id.radioMisoprostol_no);
            }
        } catch (JSONException jse) {
            System.out.println("The JSON key:  does not exist");
        }
    }

    private void saveToJson() {
        deliveryInfoUpdateTask = new AsyncDeliveryInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(true, false); //mother, no retireve
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getEditTextTime(json);
            getSpecialCases(json);
            deliveryInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
            Log.d(LOGTAG, "In Save, Delivery Json in Query:" + json.toString());
            passJson.putExtra("DeliveryJson", json.toString());

        } catch (JSONException jse) {
            Log.e("Delivery", "JSON Exception: " + jse.getMessage());
        }

    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("dOther", ""); //other delivery complicacies
            json.put("dOtherReason", ""); //other delivery complicacies
            int id[] = {R.id.Dead_born_fresh, R.id.Dead_born_macerated};
            int stillBirth = 0;
            String temp = "";
            for(int i = 0; i < 2; i++) {
                temp = getEditText(id[i]).getText().toString();
                if(!temp.equals("")) {
                    stillBirth += Integer.valueOf(temp);
                }
            }
            json.put("dNoStillBirth", stillBirth);
        } catch (JSONException jse) {

        }
    }

    @Override
    protected void initiateRadioGroups() {

        jsonRadioGroupButtonMap.put("dEpisiotomy", Pair.create(
                        getRadioGroup(R.id.id_radioGroupEpctiomi), Pair.create(
                                getRadioButton(R.id.radioEpc_yes),
                                getRadioButton(R.id.radioEpc_no))
                )
        );

        jsonRadioGroupButtonMap.put("dMisoprostol", Pair.create(
                        getRadioGroup(R.id.id_radioGroupMisoprostol), Pair.create(
                                getRadioButton(R.id.radioMisoprostol_yes),
                                getRadioButton(R.id.radioMisoprostol_no))
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

    private JSONObject buildQueryHeader(boolean isMother, boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + mother.getPregNo() + "," +
                (isMother ?"deliveryLoad:":"newbornLoad:") + (isRetrieval? "retrieve":"\"\"") +
                "}";

        //SendPostRequestAsyncTask retrieveDelivery = new AsyncDeliveryInfoUpdate(this);
        //retrieveDelivery.execute(queryString, SERVLET, ROOTKEY);
        return new JSONObject(queryString);
    }
    private boolean checkClientInfo() {
        if(mother== null ) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void getExistingChild() {
        boolean isMother = false; //just being verbose for better readability
        boolean retireve = true; //just being verbose for better readability
        try {
            JSONObject childJson = buildQueryHeader(isMother, retireve);
            AsyncDeliveryInfoUpdate childInfoUpdate = new AsyncDeliveryInfoUpdate(new AsyncCallback() {
                @Override
                public void callbackAsyncTask(String result) {
                    Log.d(LOGTAG, "NEWBORN Response Received:\n\t" + result);
                    handleExistingChild(result);
                }
            });

            childInfoUpdate.execute(childJson.toString(), "newborn", "newbornInfo");
            //queryString, SERVLET, ROOTKEY
        } catch (JSONException JSE) {
            Log.e(LOGTAG, "Building child query\n\t:" + JSE.getStackTrace().toString());
        }
    }

    private void startChildActivity(int index, JSONObject child) throws JSONException{
        child.put("childno", index);
        passJson.putExtra("Layout", child.has("birthStatus") ? child.getInt("birthStatus"): 3);
        passJson.putExtra("DeliveryJson", dJson.toString());
        passJson.putExtra("NewbornJson", child.toString());

        if(checkClientInfo() && mother.isEligibleFor(PregWoman.PREG_SERVICE.NEWBORN)) {
            passJson.putExtra("PregWoman", mother);
            passJson.putExtra("Provider", ProviderInfo.getProvider());
            Log.d(LOGTAG, dJson.toString());

            startActivity(passJson);
        } else {
            Toast.makeText(this, "Newborn cannot be added, verify ...", Toast.LENGTH_LONG).show();
        }
    }

    private void handleExistingChild(String result) {
        existingChildInfo = result;
        try {
            final JSONObject childJson = new JSONObject(result);
            if (childJson.has("hasNewbornInfo") &&
                childJson.getString("hasNewbornInfo").equals("Yes") ) {
                Spinner childDropdown = getSpinner(R.id.id_childListDropdown);
                childList  = new ArrayList<>();
                childDropdown.setVisibility(View.VISIBLE);
                //childDropdown.setAdapter();
                currentChildCount = childJson.getInt("count");
                childList.add("");
                for (int i  = 0; i < currentChildCount; i++) {
                    childList.add("Child:" + String.valueOf(i+1));
                }

                childAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,childList);
                childAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                childDropdown.setAdapter(childAdapter);
                childDropdown.setSelection(0);

                childDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if(position != 0)
                            startChildActivity(position, childJson.getJSONObject(String.valueOf(position)));
                        } catch (JSONException JSE) {
                            JSE.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        } catch (JSONException JSE) {
            JSE.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
