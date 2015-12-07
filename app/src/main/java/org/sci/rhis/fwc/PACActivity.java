package org.sci.rhis.fwc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PACActivity extends ClinicalServiceActivity {

    final static String LOGTAG = "FWC_PAC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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

    protected void initiateCheckboxes(){};
    protected void initiateEditTexts(){};
    protected void initiateTextViews(){};
    protected void initiateSpinners(){};
    protected void initiateMultiSelectionSpinners(){};
    protected void initiateEditTextDates(){};
    ;
    protected void initiateRadioGroups(){};
}
