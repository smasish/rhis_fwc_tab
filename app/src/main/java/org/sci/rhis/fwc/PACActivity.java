package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity  implements MinimumDeliveryInfoFragment.DeliverySavedListener{


    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private  final String LOGTAG    = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;

    private PregWoman woman = null;
    private ProviderInfo provider = null;

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

        Intent intent = getIntent();

        woman = intent.getParcelableExtra("PregWoman");
        provider = intent.getParcelableExtra("Provider");

        if( woman.getAbortionInfo() == 0) {
            getAbortionInformation();
        }
    }

    private void getAbortionInformation() {
        //Disable PAC and History Layout first
        // Utilities.Disable(this, R.id.pacEntryMasterLayout);
        Utilities.MakeInvisible(this, R.id.historyFragmentLayout);
        Utilities.MakeVisible(this, R.id.idPacAbortionInfo);
        Utilities.Disable(this, R.id.pacEntryMasterLayout);
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

    public void onClick(View v) {
        Utilities.Reset(this, R.id.pacEntryMasterLayout);
    }

    private Activity getActivity() {
        return this;
    }

    //Callback method of delivery Saved even notification
    @Override
    public void onDeliverySaved(String result) {
        Log.d(LOGTAG, "Abortion Info saved response:\n\t\t" + result);
        Utilities.MakeInvisible(this, R.id.idMinDeliveryFragmentHolder);
        Utilities.MakeVisible(this, R.id.pacEntryMasterLayout);
        Utilities.MakeVisible(this,R.id.historyFragmentLayout);
        Utilities.Enable(this, R.id.historyFragmentLayout);
        Utilities.Enable(this, R.id.pacEntryMasterLayout);
    }
}
