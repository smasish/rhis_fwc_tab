package org.sci.rhis.fwc;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class DeliveryActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    //UI References

    private DatePickerDialog fromDatePickerDialog;
    private ImageView deliveryDateButton;
    private ImageView admissionDateButton;
    private ImageView anyDateButton;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Spinner spinners[] = new Spinner[2];
        spinners[0] = (Spinner) findViewById(R.id.delivery_placeDropdown);
        spinners[1] = (Spinner) findViewById(R.id.id_facility_name_Dropdown);
        for(int i = 0; i < spinners.length; ++i) {
            spinners[i].setOnItemSelectedListener(this);
        }

        ((EditText) findViewById(R.id.id_delivery_date)).setOnClickListener(this);
        ((EditText) findViewById(R.id.id_admissionDate)).setOnClickListener(this);

        //custom date picker
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.imageViewDeliveryDate, (EditText)findViewById(R.id.id_delivery_date));
        datePickerPair.put(R.id.imageViewAdmissionDate, (EditText)findViewById(R.id.id_admissionDate));
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

        switch (parent.getId()) {
            case R.id.delivery_placeDropdown:
                System.out.println("Hit the Place Spinner");
                LinearLayout [] section = new LinearLayout[2];
                section [0] = (LinearLayout) findViewById(R.id.id_facililties_section_layout);
                section [1] = (LinearLayout) findViewById(R.id.id_Epcotomi_section_layout);

                for(int i = 0; i < section.length; ++i) {
                    section[i].setVisibility( position == 0? View.INVISIBLE:View.VISIBLE); //0 - home
                }                
                break;
            case R.id.id_facility_name_Dropdown:
                System.out.println("Hit the Facility Spinner");
                LinearLayout  faclityAdmission = (LinearLayout) findViewById(R.id.id_facililties_admission_layout);
                faclityAdmission.setVisibility((position == 0 || position == 1) ? View.INVISIBLE:View.VISIBLE);
                //0 - UH&FWC 1 - CC
                break;

        }
    }

    @Override
    public void onClick(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void callbackAsyncTask(String result) {

    }
}
