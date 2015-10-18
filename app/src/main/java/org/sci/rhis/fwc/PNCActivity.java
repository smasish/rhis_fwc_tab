package org.sci.rhis.fwc;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                     View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener {
    private MultiSelectionSpinner multiSelectionSpinner;



    // For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    ExpandableListAdapterforPNC listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    LinearLayout ll;


    private static final String TAG_VISIT_NO = "ancVisit01";
    private static final String TAG_DATE = "2015-07-02";
    private static final String TAG_BLOOD_PRESSURE = "120";
    private static final String TAG_WEIGHT = "22";
    private static final String TAG_EDIMA = "22";
    private static final String TAG_J_HEIGHT = "5.6";
    private static final String TAG_FITNESS_PM = "FIT";
    private static final String TAG_PHITAL_PRESENTATION = "GOOD";
    private static final String TAG_HIMOGLOBIN = "2";
    private static final String TAG_JONDIS = "3";
    private static final String TAG_URIN_SUGAR = "3";
    private static final String TAG_URIN_TEST = "3";
    private static final String TAG_DANGER_SIGN = "OFF";
    private static final String TAG_DISADVANTAGE = "NA";
    private static final String TAG_DISEASE = "FEVER";
    private static final String TAG_TREATMENT = "OK";
    private static final String TAG_REFER = "DR";
    private static final String TAG_CENTER_NAME = "DHAKA";
    private static final String TAG_CAUSE = "MAN";

    ArrayList<HashMap<String, String>> contactList;
    JSONArray contacts = null;

    private View mPNCLayout;
    Boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnc);

      // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mPNCLayout = findViewById(R.id.pncScroll);
        // Find our buttons
        Button visibleButton = (Button) findViewById(R.id.pncLabel);

        View.OnClickListener mVisibleListener = new View.OnClickListener() {
            public void onClick(View v) {
                if(flag==false) {
                    mPNCLayout.setVisibility(View.VISIBLE);
                    flag=true;
                }
                else
                {
                    mPNCLayout.setVisibility(View.INVISIBLE);
                    flag=false;
                }
            }
        };

        // Wire each button to a click listener
        visibleButton.setOnClickListener(mVisibleListener);
// Added By Al Amin
        initialize(); //super class
        Spinner spinners[] = new Spinner[6];
        spinners[0] = (Spinner) findViewById(R.id.pncBreastConditionSpinner);
        spinners[1] = (Spinner) findViewById(R.id.pncDischargeBleedingSpinner);
        spinners[2] = (Spinner) findViewById(R.id.pncPerineumSpinner);
        spinners[3] = (Spinner) findViewById(R.id.pncFamilyPlanningMethodsSpinner);
        spinners[4] = (Spinner) findViewById(R.id.pncReferCenterNameSpinner);
        //spinners[5] = (Spinner) findViewById(R.id.pncAnemiaSpinner);
        for(int i = 0; i < spinners.length; ++i) {
          //  spinners[i].setOnItemSelectedListener(this);
        }

        // Multi Select Spinner Initialisation
        final List<String> pncmdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Danger_Sign_DropDown));
        final List<String> pnccdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Danger_Sign_DropDown));
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Drawback_DropDown));
        final List<String> pnccdrawbackblist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Drawback_DropDown));
        final List<String> pncmdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Disease_DropDown));
        final List<String> pnccdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Disease_DropDown));
        final List<String> pncmtreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncctreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncmadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Advice_DropDown));
        final List<String> pnccadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Advice_DropDown));
        final List<String> pncmreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Refer_Reason_DropDown));
        final List<String> pnccreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Refer_Reason_DropDown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDangerSignsSpinner);
        multiSelectionSpinner.setItems(pncmdangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDangerSignsSpinner);
        multiSelectionSpinner.setItems(pnccdangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDangerSignsSpinner);
        multiSelectionSpinner.setItems(pnccdrawbackblist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDiseaseSpinner);
        multiSelectionSpinner.setItems(pncmdiseaselist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDiseaseSpinner);
        multiSelectionSpinner.setItems(pnccdiseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncTreatmentSpinner);
        multiSelectionSpinner.setItems(pncmtreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildTreatmentSpinner);
        multiSelectionSpinner.setItems(pncctreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncAdviceSpinner);
        multiSelectionSpinner.setItems(pncmadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildAdviceSpinner);
        multiSelectionSpinner.setItems(pnccadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncReasonSpinner);
        multiSelectionSpinner.setItems(pncmreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildReasonSpinner);
        multiSelectionSpinner.setItems(pnccreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});



        ll = (LinearLayout)findViewById(R.id.llay);
        contactList = new ArrayList<HashMap<String, String>>();

        expListView = new ExpandableListView(this);
        ll.addView(expListView);


        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(PNCActivity.this);
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(PNCActivity.this);
//        String queryString =   "{" +
//                "pregNo:" + 3 + "," +
//                "healthid:" + "43366275025436" + "," +
//                "pncMLoad:" + ProviderInfo.getProvider().getProviderCode() +
//                "}";

        String queryString =   "{" +
                "pregno:" + 3 + "," +
                "healthid:" + "43366275025436" + "," +
                "pncMLoad:" + "retrieve" +
                "}";

        String servlet = "pncmother";
        String jsonRootkey = "PNCMotherInfo";
        Log.d("-->","---=====>"+queryString);
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);

        getEditText(R.id.pncServiceDateValue).setOnClickListener(this);
        getEditText(R.id.pncChildServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.pncReferCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncChildReferCheckBox).setOnCheckedChangeListener(this);

//custom date picker Added By Al Amin
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pncServiceDateValue));
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pnc, menu);
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


        try {
            JSONObject jsonStr = new JSONObject(result);
            String key;

            // woman = PregWoman.CreatePregWoman(json);
            //Log.d("--:::>", "---complicationsign=====>"+result);
            //DEBUG
            Resources res = getResources();
            String[] mainlist = res.getStringArray(R.array.list_item);
            //Log.d("-->","---=jsonStr.keys()====>" +jsonStr.keys());
            for ( Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                key = ii.next();


                System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key) + "\'");

                JSONObject jsonRootObject = jsonStr.getJSONObject(key);
                Log.d("--:::>", "---serviceSource=====>" + jsonRootObject.getString("serviceSource"));

                String  complicationsign = jsonRootObject.getString("complicationsign");
                String  serviceSource = jsonRootObject.getString("serviceSource");
                String  anemia = jsonRootObject.getString("anemia");
                String  referCenterName = jsonRootObject.getString("referCenterName");
                String  treatment = jsonRootObject.getString("treatment");
                String  perineum = jsonRootObject.getString("perineum");
                String  uterusInvolution= jsonRootObject.getString("uterusInvolution");
                String  visitDate = jsonRootObject.getString("visitDate");
                String  bpDiastolic = jsonRootObject.getString("bpDiastolic");
                String  disease = jsonRootObject.getString("disease");
                String  bpSystolic = jsonRootObject.getString("bpSystolic");
                String  hematuria = jsonRootObject.getString("hematuria");
                String  temperature= jsonRootObject.getString("temperature");
                String  referReason = jsonRootObject.getString("referReason");
                String  refer = jsonRootObject.getString("refer");
                String  edema = jsonRootObject.getString("edema");
                String  serviceID = jsonRootObject.getString("serviceID");
                String  hemoglobin = jsonRootObject.getString("hemoglobin");
                String  FPMethod = jsonRootObject.getString("FPMethod");
                String  breastCondition = jsonRootObject.getString("breastCondition");
                String  advice = jsonRootObject.getString("advice");
                String  symptom = jsonRootObject.getString("symptom");
                // String  pncStatus= jsonRootObject.getString("pncStatus");
                //Log.d("--:::>", "---complicationsign=====>"+jsonStr.get(key));

                ArrayList<String> list = new ArrayList<String>();
                list.add(""+getString(R.string.visitDate)+" "+visitDate);
                list.add(""+getString(R.string.temperature)+" "+temperature);
                list.add(""+getString(R.string.bpSystolic)+" "+bpSystolic);
                list.add(""+getString(R.string.anemia)+" "+anemia);
                list.add(""+getString(R.string.hemoglobin)+" "+hemoglobin);
                list.add(""+getString(R.string.edema)+" "+edema);
                list.add(""+getString(R.string.breastCondition)+" "+breastCondition);
                list.add(""+getString(R.string.uterusInvolution)+" "+uterusInvolution);
                list.add(""+getString(R.string.hematuria)+" "+hematuria);
                list.add(""+getString(R.string.perineum)+" "+perineum);
                list.add(""+getString(R.string.family_planning_methods)+" "+FPMethod);
                list.add(""+getString(R.string.danger_signs)+" "+complicationsign);
                list.add(""+getString(R.string.disease)+" "+disease);
                list.add(""+getString(R.string.treatment)+" "+treatment);
                list.add(""+getString(R.string.advice)+" "+advice);
                list.add(""+getString(R.string.referCenterName)+" "+referCenterName);
                list.add(""+getString(R.string.referReason)+" "+referReason);


                try {
                    // JSONArray jsonArray = jsonStr.getJSONArray(key);


                    listDataHeader = new ArrayList<String>();
                    listDataChild = new HashMap<String, List<String>>();


                    // listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");
                    listDataHeader.add("Visit "+key + ":");//jsonArray.get(0).toString()
                    listDataChild.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapterforPNC(this, listDataHeader, listDataChild);



                    initPage();

                    ll.addView(expListView);
                    expListView.setScrollingCacheEnabled(true);
                    expListView.setAdapter(listAdapter);
                    ll.invalidate();
                    expListView.setAdapter(listAdapter);


                } catch (Exception e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        }
    }

    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);


        //  expListView.smoothScrollToPosition(expListView.getCount() - 1);




    }

    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncReferCheckBox));
        jsonCheckboxMap.put("pncbreastfeedingonly", getCheckbox(R.id.pncChildOnlyBreastFeedingCheckBox));
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncChildReferCheckBox));
    };

    @Override
    protected void initiateRadioGroups(){};


    @Override
    protected void initiateSpinners() {
        // PNC Mother Info
        jsonSpinnerMap.put("pncbreastcondition", getSpinner(R.id.pncBreastConditionSpinner));
        jsonSpinnerMap.put("pnchematuria", getSpinner(R.id.pncDischargeBleedingSpinner));
        jsonSpinnerMap.put("pncperineum", getSpinner(R.id.pncPerineumSpinner));
        //jsonSpinnerMap.put("pncanemia", getSpinner(R.id.pncAnemiaSpinner));
        jsonSpinnerMap.put("pncfpmethod", getSpinner(R.id.pncFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("pncsymptom", getSpinner(R.id.pncDrawbackSpinner));
        jsonSpinnerMap.put("pncdisease", getSpinner(R.id.pncDiseaseSpinner));
        jsonSpinnerMap.put("pnctreatment", getSpinner(R.id.pncTreatmentSpinner));
        jsonSpinnerMap.put("pncadvice", getSpinner(R.id.pncAdviceSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));
        jsonSpinnerMap.put("pncreferreason", getSpinner(R.id.pncReasonSpinner));

        // PNC Child Info
        jsonSpinnerMap.put("pncdangersign", getSpinner(R.id.pncChildDangerSignsSpinner));
        jsonSpinnerMap.put("pncsymptom", getSpinner(R.id.pncChildDrawbackSpinner));
        jsonSpinnerMap.put("pncdisease", getSpinner(R.id.pncChildDiseaseSpinner));
        jsonSpinnerMap.put("pnctreatment", getSpinner(R.id.pncChildTreatmentSpinner));
        jsonSpinnerMap.put("pncadvice", getSpinner(R.id.pncChildAdviceSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));;
        jsonSpinnerMap.put("pncreferreason", getSpinner(R.id.pncChildReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //PNC Mother visit

        jsonEditTextMap.put("serviceId", getEditText(R.id.pncVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncTemperatureValue));
        jsonEditTextMap.put("pncbpsys", getEditText(R.id.pncBloodPresserValueS));
        jsonEditTextMap.put("pncbpdias",getEditText(R.id.pncBloodPresserValueD));
        //jsonEditTextMap.put("pnchemoglobin",getEditText(R.id.pncAnemiaValue));
       // jsonEditTextMap.put("pnchemoglobin",getEditText(R.id.pncHemoglobinValue));
        jsonEditTextMap.put("pncCervixInvolutionValue",getEditText(R.id.pncCervixInvolutionValue));

        //PNC Child visit
        jsonEditTextMap.put("childNo", getEditText(R.id.pncNewBornNumber));
        jsonEditTextMap.put("serviceCount", getEditText(R.id.pncChildVisitValue));
        jsonEditTextMap.put("pncchildno", getEditText(R.id.pncNewBornNumber));
        // jsonEditTextMap.put("pregno", getEditText(R.id.pncChildVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMap.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMap.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));

    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateEditTextDates() {
        // PNC Mother Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncServiceDateValue));
        // PNC Child Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncChildServiceDateValue));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.pncReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            getTextView(R.id.pncReferCenterNameLabel).setVisibility(visibility);
            getSpinner(R.id.pncReferCenterNameSpinner).setVisibility(visibility);
            getTextView(R.id.pncReasonLabel).setVisibility(visibility);
            getSpinner(R.id.pncReasonSpinner).setVisibility(visibility);
        }
        if (buttonView.getId() == R.id.pncChildReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            getTextView(R.id.pncChildReferCenterNameLabel).setVisibility(visibility);
            getSpinner(R.id.pncChildReferCenterNameSpinner).setVisibility(visibility);
            getTextView(R.id.pncChildReasonLabel).setVisibility(visibility);
            getSpinner(R.id.pncChildReasonSpinner).setVisibility(visibility);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pncServiceDateValue || v.getId() == R.id.Date_Picker_Button) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
