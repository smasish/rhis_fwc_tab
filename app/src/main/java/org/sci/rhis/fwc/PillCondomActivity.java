package org.sci.rhis.fwc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by armaan-ul.islam on 28-Dec-15.
 */
public class PillCondomActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener {

    private ProviderInfo provider;
    private Date today;
    private int lastVisit;
    private Context con;

    private Spinner spinners[] = new Spinner[4];
    private LinearLayout layouts[] = new LinearLayout[5];

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        today = new Date();
        lastVisit = 0;

        setContentView(R.layout.activity_pill_condom);

        con = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        provider = getIntent().getParcelableExtra("Provider");

        //   AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(ANCActivity.this);

        // Initialize Spinner added By
        //initialize(); //super class
        initiateSpinners();
        initiateLinearLayouts();

        for(int i = 0; i < spinners.length; i++) {
            spinners[i].setOnItemSelectedListener(this);
        }

        /*
        //  getEditText(R.id.ancServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.ancReferCheckBox).setOnCheckedChangeListener(this);
        */

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<>();
        datePickerPair.put(R.id.pac_Date_Picker_Button, (EditText)findViewById(R.id.pcServiceDateValue));

    }

    @Override
    public void callbackAsyncTask(String result) {

    }

    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {

    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    public void initiateSpinners() {
        spinners[0] = (Spinner) findViewById(R.id.pcMethodSpinner);
        spinners[1] = (Spinner) findViewById(R.id.pillSpinner);
        spinners[2] = (Spinner) findViewById(R.id.leftReasonSpinner);
        spinners[3] = (Spinner) findViewById(R.id.pcOtherSpinner);
    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

    }

    protected void initiateLinearLayouts() {
        layouts[0]=(LinearLayout) findViewById(R.id.pillName);
        layouts[1]=(LinearLayout) findViewById(R.id.pillAmount);
        layouts[2]=(LinearLayout) findViewById(R.id.condomLayout);
        layouts[3]=(LinearLayout) findViewById(R.id.leftLayout);
        layouts[4]=(LinearLayout) findViewById(R.id.pcOtherLayout);
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void hideAllLayouts(){
        for(int i=0;i<layouts.length;i++){
            Utilities.MakeInvisible(this,layouts[i],View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == spinners[0].getId()) {
            hideAllLayouts();
            switch (spinner.getSelectedItemPosition()){
                case 1:
                    Utilities.MakeVisible(this,layouts[0]);
                    Utilities.MakeVisible(this,layouts[1]);
                    break;
                case 2:
                    Utilities.MakeVisible(this,layouts[2]);
                    break;
                case 3:
                    Utilities.MakeVisible(this,layouts[3]);
                    break;
                case 4:
                    Utilities.MakeVisible(this,layouts[4]);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
