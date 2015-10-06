package org.sci.rhis.fwc;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class NRCActivity extends ClinicalServiceActivity {
    Connection C;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrc);
        //  C = new Connection(this);

        final Spinner spnDist = (Spinner) findViewById(R.id.Clients_District);
        SpinnerItem(spnDist, "Select ZILLAID+'-'+ZILLANAME from Zilla");
        final Spinner spnUpz = (Spinner) findViewById(R.id.Clients_Upazila);
        final Spinner spnUN = (Spinner) findViewById(R.id.Clients_Union);
        final Spinner spnVillage = (Spinner) findViewById(R.id.Clients_Village);

    }

    private void SpinnerItem(Spinner SpinnerName,String SQL)
    {
        List<String> listItem = new ArrayList<String>();
        //listItem = AreaList(SQL);
        ArrayAdapter<String> adptrList= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listItem);
        SpinnerName.setAdapter(adptrList);
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
