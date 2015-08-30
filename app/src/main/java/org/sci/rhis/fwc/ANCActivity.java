package org.sci.rhis.fwc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

public class ANCActivity extends ClinicalServiceActivity {

    private PregWoman woman;
    private Date today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        woman = getIntent().getParcelableExtra("PregWoman");

        today = new Date();
        if( woman.getAncThreshold().after(today)) { // add ANC threshold
            Toast.makeText(this, "Too Late for ANC, ask delivery status ...", Toast.LENGTH_LONG).show();
        } else {
            setContentView(R.layout.activity_anc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anc, menu);
        return true;
    }

    @Override
    public void callbackAsyncTask(String result) {

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
}
