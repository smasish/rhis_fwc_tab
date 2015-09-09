
package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class SecondActivity extends ClinicalServiceActivity {

    Button button;
    PregWoman woman;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Spinner staticSpinner = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.Health_Id,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        addListenerOnButton();
    }

    public void startSearch(View view) {
        Spinner searchOptions = (Spinner)findViewById(R.id.ClientsIdentityDropdown);
        EditText searchableId = (EditText)findViewById(R.id.searchableTextId);
        //TODO - remove
        long index = (searchOptions.getSelectedItemId() + 1);
        long id = Long.valueOf(searchableId.getText().toString());

        String queryString =   "{" +
                "sOpt:" + index + "," +
                "sStr:" + id + "," +
                "providerid:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "client";
        String jsonRootkey = "sClient";
        AsyncClientInfoUpdate retrieveClient = new AsyncClientInfoUpdate(this);

        retrieveClient.execute(queryString, servlet, jsonRootkey);

        System.out.println("sOpt: " + index
                + /*Adding 1 to match HTML index where healthID starts from 1*/
                " text: " + id);
    }
    private void populateClientDetails(JSONObject json, HashMap<String, Integer> fieldMapping) {
        Iterator<String> i = fieldMapping.keySet().iterator();
        String key;

        while(i.hasNext()) {
            key = i.next();
            if (fieldMapping.get(key) != null) { //If the field exist in the mapping table
                try {
                    ((EditText) findViewById(fieldMapping.get(key))).setText(json.get(key).toString());
                } catch (JSONException jse) {
                    System.out.println("JSON Exception Thrown:\n " );
                    jse.printStackTrace();
                }
            }
        }
    }

    @Override
    public void callbackAsyncTask(String result) { //Get results back from healthId search

        try {
            JSONObject json = new JSONObject(result);
            String key;
            woman = PregWoman.CreatePregWoman(json);

            //DEBUG
            for ( Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key)+"\'");
            }

            if(json.get("False").toString().equals("")) {
                populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                woman.UpdateUIField(this);
                //populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                Utilities.DisableTextFields(this, R.id.fragment_client_intro_scroll);
                Utilities.DisableTextFields(this, R.id.fragment_client_info_scroll);
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }

    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.nonregiser);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void startANC(View view) {
        Intent intent = new Intent(this, ANCActivity.class);

        if( checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.ANC)) {
            intent.putExtra("PregWoman", woman);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for ANC, ask delivery status ...", Toast.LENGTH_LONG).show();
        }
    }

    public void startDelivery(View view) {
        Intent intent = new Intent(this, DeliveryActivity.class);

        if(checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.DELIVERY)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for Delivery, verify ...", Toast.LENGTH_LONG).show();
        }
    }

    public void startPNC(View view) {
        Intent intent = new Intent(this, PNCActivity.class);
        startActivity(intent);
    }

    private boolean checkClientInfo() {
        if(woman == null ) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
