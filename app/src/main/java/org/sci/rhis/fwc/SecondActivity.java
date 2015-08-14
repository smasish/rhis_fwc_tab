import android.support.v7.app.AppCompatActivity;

package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.sci.rhis.fwc.LoginActivity;
import org.sci.rhis.fwc.R;

public class SecondActivity extends AppCompatActivity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Spinner staticSpinner = (Spinner) findViewById(R.id.spinner);
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

}
