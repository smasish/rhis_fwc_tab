package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import java.sql.Connection;

public class NRCActivity extends ClinicalServiceActivity {
    Connection C;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrc);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Spinner sex = (Spinner)findViewById(R.id.ClientsSexSpinner);
        final Spinner spnDist = (Spinner) findViewById(R.id.Clients_District);
        final Spinner spnUpz = (Spinner) findViewById(R.id.Clients_Upazila);
        final Spinner spnUN = (Spinner) findViewById(R.id.Clients_Union);
        final Spinner spnVillage = (Spinner) findViewById(R.id.Clients_Village);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nrc, menu);
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
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {

    }

    @Override
    protected void initiateSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

    }
}
