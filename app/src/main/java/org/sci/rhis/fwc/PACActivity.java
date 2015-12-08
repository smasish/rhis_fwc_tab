package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private  final String LOGTAG    = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac);
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

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void setMultiSelectSpinners() {
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PACProblemDropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Danger_Sign_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.ANC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Refer_Reason_DropDown));
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

    protected void initiateCheckboxes(){};
    protected void initiateEditTexts(){
        jsonEditTextMap.put("temperature", getEditText(R.id.pacTemperatureValue));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pacBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pacBloodPresserValueDiastolic));
        jsonEditTextMap.put("hemoglobin", getEditText(R.id.pacHemoglobinValue));
    };
    protected void initiateTextViews(){};
    protected void initiateSpinners(){
        jsonSpinnerMap.put("anemia",getSpinner(R.id.pacAnemiaSpinner));
        jsonSpinnerMap.put("ancjaundice", getSpinner(R.id.ancJaundiceSpinner));
        jsonSpinnerMap.put("ancsugar", getSpinner(R.id.ancUrineSugarSpinner));
        jsonSpinnerMap.put("ancalbumin", getSpinner(R.id.ancUrineAlbuminSpinner));
    };
    protected void initiateMultiSelectionSpinners(){};
    protected void initiateEditTextDates(){};
    ;
    protected void initiateRadioGroups(){};

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.pacServiceDateValue ||
                v.getId() == R.id.Date_Picker_Button ) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.pacReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pacReferCenterNameLayout, R.id.pacReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }
}
