package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    private PregWoman mother;
    private ProviderInfo provider;
    AsyncPACInfoUpdate pacInfoUpdateTask;

    private int lastPacVisit = 0;
    private int pacSaveClick = 0;

    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private  final String LOGTAG    = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //initiate multiselect spinners
        setMultiSelectSpinners();


        getCheckbox(R.id.pacOtherCheckBox).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.pacOtherCheckBox:
                        setItemVisible(R.id.pacOtherCenterNameSpinner, isChecked);
                        break;
                }
            }
        });

        getEditText(R.id.pacServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.pacReferCheckBox).setOnCheckedChangeListener(this);

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pacServiceDateValue));

        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        //pacvisit
        lastPacVisit = 0;

        initialize();
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void setMultiSelectSpinners() {
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PACProblemDropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> pncmhematurialist = Arrays.asList(getResources().getStringArray(R.array.PACHematuriaDropdown));
        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Danger_Sign_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.ANC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Refer_Reason_DropDown));
        final List<String> pacabdomenlist = Arrays.asList(getResources().getStringArray(R.array.PACAbdomenDropdown));
        final List<String> pacuteruslist = Arrays.asList(getResources().getStringArray(R.array.PACUterusDropdown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDangerSignsSpinner);
        if(multiSelectionSpinner == null){
            Log.d("------"+ dangersignlist.get(1),".........");
        }
        multiSelectionSpinner.setItems(dangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDiseaseSpinner);
        multiSelectionSpinner.setItems(diseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAbdomenSpinner);
        multiSelectionSpinner.setItems(pacabdomenlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacHematuriaSpinner);
        multiSelectionSpinner.setItems(pncmhematurialist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacCervixSpinner);
        multiSelectionSpinner.setItems(pacuteruslist);
        multiSelectionSpinner.setSelection(new int[]{});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_death, menu);
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
        Log.d(LOGTAG, "PAC Response Received:\n\t" + result);
        //handleExistingChild(result);
    }

    void setItemVisible(int ItemId, boolean isChecked) {
        Spinner Item=(Spinner)findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("refer", getCheckbox(R.id.pacReferCheckBox));
    };
    protected void initiateEditTexts(){
        jsonEditTextMap.put("temperature", getEditText(R.id.pacTemperatureValue));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pacBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pacBloodPresserValueDiastolic));
        jsonEditTextMap.put("hemoglobin", getEditText(R.id.pacHemoglobinValue));
    };
    protected void initiateTextViews(){};
    protected void initiateSpinners(){
        jsonSpinnerMap.put("anemia",getSpinner(R.id.pacAnemiaSpinner));
        jsonSpinnerMap.put("uterusInvolution", getSpinner(R.id.pacCervixSpinner));
        jsonSpinnerMap.put("perineum", getSpinner(R.id.pacPerineumSpinner));
        jsonSpinnerMap.put("FPMethod", getSpinner(R.id.pacFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.pacReferCenterNameSpinner));
        jsonSpinnerMap.put("serviceSource", getSpinner(R.id.pacOtherCenterNameSpinner));

    };
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("symptom",  getMultiSelectionSpinner(R.id.pacDrawbackSpinner));
        jsonMultiSpinnerMap.put("complicationSign",  getMultiSelectionSpinner(R.id.pacDangerSignsSpinner));
        jsonMultiSpinnerMap.put("disease",  getMultiSelectionSpinner(R.id.pacDiseaseSpinner));
        jsonMultiSpinnerMap.put("treatment",  getMultiSelectionSpinner(R.id.pacTreatmentSpinner));
        jsonMultiSpinnerMap.put("advice",  getMultiSelectionSpinner(R.id.pacAdviceSpinner));
        jsonMultiSpinnerMap.put("referReason",  getMultiSelectionSpinner(R.id.pacReasonSpinner));
        jsonMultiSpinnerMap.put("cervix",  getMultiSelectionSpinner(R.id.pacCervixSpinner));
        jsonMultiSpinnerMap.put("hematuria",  getMultiSelectionSpinner(R.id.pacHematuriaSpinner));
        jsonMultiSpinnerMap.put("abdomen",  getMultiSelectionSpinner(R.id.pacAbdomenSpinner));
    };
    protected void initiateEditTextDates(){
        jsonEditTextDateMap.put("visitDate", getEditText(R.id.pacServiceDateValue));
    };

    protected void initiateRadioGroups(){};

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.pacServiceDateValue ||
                v.getId() == R.id.Date_Picker_Button ) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }

        if( v.getId() == R.id.pacSaveButton){
            pacSaveToJson();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.pacOtherCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;

            if(!isChecked)
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);
            else
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);

            getSpinner(R.id.pacOtherCenterNameSpinner).setVisibility(visibility);

        }

        if (buttonView.getId() == R.id.pacReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pacReferCenterNameLayout, R.id.pacReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    private void pacSaveToJson() {
        pacInfoUpdateTask = new AsyncPACInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);

            pacInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            System.out.print("PAC Save Json in Servlet:" + ROOTKEY + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pacText);

        } catch (JSONException jse) {
            Log.e("PAC JSON Exception: ", jse.getMessage());
        }

    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthId:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregNo:" + mother.getPregNo() + "," +
                "pacLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        return new JSONObject(queryString);
    }
}
