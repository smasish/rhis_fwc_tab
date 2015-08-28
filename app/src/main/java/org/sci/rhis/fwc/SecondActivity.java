
package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SecondActivity extends ClinicalServiceActivity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Spinner staticSpinner = (Spinner) findViewById(R.id.ClientsIdendityDropdown);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.Health_Id,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
/*
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);

        String[] items = new String[] { "Chai Latte", "Green Tea", "Black Tea" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        }); */

        addListenerOnButton();
    }

    public void startSearch(View view) {
        Spinner searchOptions = (Spinner)findViewById(R.id.ClientsIdendityDropdown);
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

    @Override
    public void callbackAsyncTask(String result) {

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

    }
}
