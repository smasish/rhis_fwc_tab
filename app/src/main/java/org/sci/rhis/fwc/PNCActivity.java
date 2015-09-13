package org.sci.rhis.fwc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                     View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnc);
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
        jsonSpinnerMap.put("pncfpmethod", getSpinner(R.id.pncFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("pncDiseaseSpinner", getSpinner(R.id.pncDrawbackSpinner));
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

        jsonEditTextMap.put("pregNo", getEditText(R.id.pncVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncTemperatureValue));
        jsonEditTextMap.put("pncbpsys", getEditText(R.id.pncBloodPresserValueS));
        jsonEditTextMap.put("pncbpdias",getEditText(R.id.pncBloodPresserValueD));
        jsonEditTextMap.put("pnchemoglobin",getEditText(R.id.pncAnemiaHemoglobinValue));
        jsonEditTextMap.put("pncCervixInvolutionValue",getEditText(R.id.pncCervixInvolutionValue));

        //PNC Child visit
        jsonEditTextMap.put("pregNo", getEditText(R.id.pncNewBornValue));
        jsonEditTextMap.put("pregno", getEditText(R.id.pncChildVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMap.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMap.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));

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

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
