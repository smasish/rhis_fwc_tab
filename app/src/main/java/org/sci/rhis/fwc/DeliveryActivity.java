package org.sci.rhis.fwc;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTimePickerDialog;

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
    private PregWoman mother;
    private PregWoman newborn;
    private ProviderInfo provider;

    final private String SERVLET = "delivery";
    final private String ROOTKEY = "deliveryInfo";


    AsyncDeliveryInfoUpdate deliveryInfoQueryTask;
    AsyncDeliveryInfoUpdate deliveryInfoUpdateTask;

    private MultiSelectionSpinner multiSelectionSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

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

        final List<String> newbornreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornreferreasonlist);
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
        getCheckbox(R.id.deliveryChildReferCheckBox).setOnCheckedChangeListener(this);
        //hourField = (EditText)findViewById(R.id.delivery_time_hour);
        //minuteField = (EditText)findViewById(R.id.delivery_time_minute);


        //populateEditTextTimes();

        //custom date picker
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.imageViewDeliveryDate, (EditText)findViewById(R.id.id_delivery_date));
        datePickerPair.put(R.id.imageViewAdmissionDate, (EditText)findViewById(R.id.id_admissionDate));

        //custom time picker
        timePickerDialog = new CustomTimePickerDialog(this);

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");
        newborn = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        //get info from database
        String queryString = "";
                /* =
                "{" +
                "healthid:" + mother.getHealthId() + "," +
                "pregno:" + mother.getPregNo() + "," +
                "deliveryLoad:" + "retrieve" +
                "}"*/
                ;
        //String
        try {
            queryString = buildQueryHeader(true).toString();
        } catch (JSONException JSE) {
            Log.e("Delivery", "Could not build query String: " + JSE.getMessage());
        }
        deliveryInfoQueryTask = new AsyncDeliveryInfoUpdate(this);
        deliveryInfoQueryTask.execute(queryString, SERVLET, ROOTKEY);
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
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key) + "\'");
            }

            //populate the fields if the previous delivery information exist
            if(json.getString("dNew").equals("No")) {
                Utilities.setCheckboxes(jsonCheckboxMap, json);
                Utilities.setSpinners(jsonSpinnerMap, json);
                updateRadioButtons(json);
                Utilities.setEditTexts(jsonEditTextMap, json);
                Utilities.setEditTextDates(jsonEditTextDateMap, json);
                updateEditTextTimes(json);

                //TODO Make the fields non-modifiable
                Utilities.Disable(this, R.id.delivery_info_layout);

            }

        } catch (JSONException jse) {
            jse.printStackTrace();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        LinearLayout [] section;
        switch (parent.getId()) {
            case R.id.delivery_placeDropdown:
                System.out.println("Hit the Place Spinner");
                section = new LinearLayout[3];
                section [0] = (LinearLayout) findViewById(R.id.id_facililties_section_layout);
                section [1] = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                section [2] = (LinearLayout) findViewById(R.id.id_Epcotomi_section_layout);

                for(int i = 0; i < section.length; ++i) {
                    section[i].setVisibility( position == 0? View.GONE:View.VISIBLE); //0 - home
                }                
                break;
            case R.id.id_facility_name_Dropdown:
                System.out.println("Hit the Facility Spinner");
                LinearLayout  faclityAdmission = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                faclityAdmission.setVisibility((position == 4 || position == 5) ? View.GONE:View.VISIBLE);
                //4 - UH&FWC 5 - CC
                break;
            case R.id.delivery_typeDropdown:
                section = new LinearLayout[2];
                section [0] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);
                section [1] = (LinearLayout) findViewById(R.id.id_DeliveryManagementLayout);
                //section [] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);
                //section [] = (LinearLayout) findViewById(R.id.id_deliveryResultLayout);

                for(int i = 0; i < section.length; ++i) {
                    section[i].setVisibility( position == 2? View.GONE:View.VISIBLE); //0 - abortion
                }
                System.out.println("Hit the Delivery Type");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (buttonView.getId() == R.id.id_delivery_refer) {
            int visibility = isChecked? View.VISIBLE: View.INVISIBLE;
            getTextView(R.id.id_refer_facility_name).setVisibility(visibility);
            getSpinner(R.id.id_spinner_refer_facilities).setVisibility(visibility);
            getTextView(R.id.id_refer_delivery_cause).setVisibility(visibility);
            getSpinner(R.id.id_spinner_refer_delivery_cause).setVisibility(visibility);
        }
       if (buttonView.getId() == R.id.deliveryChildReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.INVISIBLE;
            getTextView(R.id.deliveryChildReferCenterNameLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferCenterNameSpinner).setVisibility(visibility);
            getTextView(R.id.deliveryChildReferReasonLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferReasonSpinner).setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() != null && view.getTag().equals("DateField")) {
            datePickerDialog.show(datePickerPair.get(view.getId()));
        }

        if(view.getId() == R.id.id_saveDeliveryButton) {
            saveToJson();
        }

        if(view.getId() == R.id.id_saveNewbornButton) {
            saveToJson();
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
// For New born
        jsonCheckboxMap.put("refer", getCheckbox(R.id.deliveryChildReferCheckBox));
    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("dPlace", getSpinner(R.id.delivery_placeDropdown)); //place
        jsonSpinnerMap.put("dType", getSpinner(R.id.delivery_typeDropdown)); //type
        //jsonSpinnerMap.put("dPlace", getSpinner(R.id.delivery_time_Dropdown)); //time
        jsonSpinnerMap.put("dCenterName", getSpinner(R.id.id_facility_name_Dropdown));
        jsonSpinnerMap.put("dAttendantDesignation", getSpinner(R.id.id_attendantTitleDropdown)); //deliveryAttendant
        jsonSpinnerMap.put("dTreatment", getSpinner(R.id.id_spinner_treatment)); //treatment
        jsonSpinnerMap.put("dAdvice", getSpinner(R.id.id_spinner_advice)); //advice
        jsonSpinnerMap.put("dReferCenter", getSpinner(R.id.id_spinner_refer_facilities)); //refercenter
        jsonSpinnerMap.put("dReferReason", getSpinner(R.id.id_spinner_refer_delivery_cause)); //refer reason
      // for New born Layout
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.deliveryChildReferCenterNameSpinner));

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

        // for New born layout
        jsonEditTextMap.put("weight",getEditText(R.id.deliveryNewBornWeightValue));
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
// fOR NEW BORN
            if (json.getString("gender").equals("1")) {
                getRadioGroup(R.id.id_newBornSexRadioGroup).check(R.id.deliveryNewBornSon);
            } else if (json.getString("gender").equals("2")) {
                getRadioGroup(R.id.id_newBornSexRadioGroup).check(R.id.deliveryNewBornDaughter);
            }
            if (json.getString("immatureBirth").equals("1")) {
                getRadioGroup(R.id.id_newBornImmaturRadioGroup).check(R.id.deliveryPrematureBirthYesButton);
            } else if (json.getString("immatureBirth").equals("2")) {
                getRadioGroup(R.id.id_newBornImmaturRadioGroup).check(R.id.deliveryPrematureBirthNoButton);
            }

        } catch (JSONException jse) {
            System.out.println("The JSON key:  does not exist");
        }
    }

    private void saveToJson() {
        deliveryInfoUpdateTask = new AsyncDeliveryInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getEditTextTime(json);
            getSpecialCases(json);
            deliveryInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
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
        //for NewBorn
        jsonRadioGroupButtonMap.put("gender", Pair.create(
                        getRadioGroup(R.id.id_newBornSexRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryNewBornSon),
                                getRadioButton(R.id.deliveryNewBornDaughter))
                )
        );
        jsonRadioGroupButtonMap.put("immatureBirth", Pair.create(
                        getRadioGroup(R.id.id_newBornImmaturRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryPrematureBirthYesButton),
                                getRadioButton(R.id.deliveryPrematureBirthNoButton))
                )
        );

        jsonRadioGroupButtonMap.put("drying", Pair.create(
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

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + mother.getPregNo() + "," +
                "deliveryLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        //SendPostRequestAsyncTask retrieveDelivery = new AsyncDeliveryInfoUpdate(this);
        //retrieveDelivery.execute(queryString, SERVLET, ROOTKEY);
        return new JSONObject(queryString);
    }
}
