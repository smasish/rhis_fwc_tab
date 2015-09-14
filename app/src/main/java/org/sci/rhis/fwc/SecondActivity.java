
package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class SecondActivity extends ClinicalServiceActivity {

    private Button button;
    private PregWoman woman;
    private Vector<Pair<String, Integer>>  deliveryHistoryMapping;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initialize();//super class

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
                "sOpt:" + String.valueOf(index) + "," +
                "sStr:" + String.valueOf(id) + "," +
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

        HashMap<String, Pair<Spinner, Integer>> clientSpinnerMap= new HashMap<>(1); //fixed capacity ??
        clientSpinnerMap.put("cBloodGroup", Pair.create((Spinner) findViewById(R.id.Blood_Group_Dropdown), R.array.Blood_Group_Dropdown));

        manipulateJson(json);
        Utilities.setSpinners(clientSpinnerMap, json, this);
        Utilities.setCheckboxes(jsonCheckboxMap, json);
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

            if(json.get("False").toString().equals("")) { //Client exists
                populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                woman.UpdateUIField(this);

            // To Make disable desired fields
                Utilities.Disable(this, R.id.clients_intro_layout);
                Utilities.Disable(this, R.id.clients_info_layout);

            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown:\n " );
            jse.printStackTrace();
        }
    }

    public void addListenerOnButton() {

        final Context context = this;

       Button  button1 = (Button) findViewById(R.id.nonregiser);
       Button button2 = (Button) findViewById(R.id.pncButton);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, PNCActivity.class);
                startActivity(intent);
            }
        });
    }

    public void startANC(View view) {
        Intent intent = new Intent(this, ANCActivity.class);

        startActivity(intent);
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
/*
    public void startPNC(View view) {
        Intent intent = new Intent(this, PNCActivity.class);
        startActivity(intent);
    }
*/
    private boolean checkClientInfo() {
        if(woman == null ) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void initializeJsonManipulation() {
        deliveryHistoryMapping = new Vector<Pair<String, Integer>>(8);
        //The prder is important
        deliveryHistoryMapping.addElement(Pair.create("bleeding",        R.id.previousDeliveryBleedingCheckBox)); //0
        deliveryHistoryMapping.addElement(Pair.create("delayedDelivery", R.id.delayedBirthCheckBox));//1
        deliveryHistoryMapping.addElement(Pair.create("blockedDelivery", R.id.blockedDeliveryCheckBox));//2
        deliveryHistoryMapping.addElement(Pair.create("blockedPlacenta", R.id.placentaInsideUterusCheckBox));//3
        deliveryHistoryMapping.addElement(Pair.create("deadBirth",       R.id.giveBirthDeadCheckBox));//4
        deliveryHistoryMapping.addElement(Pair.create("lived48Hour",     R.id.newbornDieWithin48hoursCheckBox));//5
        deliveryHistoryMapping.addElement(Pair.create("edemaSwelling",   R.id.swellingLegsOrWholeBodyCheckBox));//6
        deliveryHistoryMapping.addElement(Pair.create("convulsion",      R.id.withConvulsionSenselessCheckBox));//7
    }

    private void manipulateJson(JSONObject json) {
        try {
            String [] array = json.getString("cHistoryComplicatedContent").split(",");
            int length = array.length;

            for(int i = 0; i < array.length; i++) {
                json.put(deliveryHistoryMapping.get(Integer.valueOf(array[i])-1).first, 1);// 1- checked, 2 - unchecked
            }
        } catch (JSONException jse) {
            jse.getMessage();
            jse.printStackTrace();
        }
    }

    //The following methods are all required for all the activities that updates information
    //from user interface
    @Override
    protected void initiateCheckboxes(){
        //TT
        jsonCheckboxMap.put("cTT1", getCheckbox(R.id.Clients_TT_Tika1));
        jsonCheckboxMap.put("cTT2", getCheckbox(R.id.Clients_TT_Tika2));
        jsonCheckboxMap.put("cTT3", getCheckbox(R.id.Clients_TT_Tika3));
        jsonCheckboxMap.put("cTT4", getCheckbox(R.id.Clients_TT_Tika4));
        jsonCheckboxMap.put("cTT5", getCheckbox(R.id.Clients_TT_Tika5));

        //Complicated Delivery History
        //NOTE: These JSON keys are not present in the Servlet response
        //The response will be manipulated to trick Checkbox handlers
        //so everything is handled in a general way.
        //manipulate json
        initializeJsonManipulation();
        for ( Pair<String, Integer> pair:deliveryHistoryMapping) {
            jsonCheckboxMap.put(pair.first, getCheckbox(pair.second));
        }
    };

    @Override
    protected void initiateEditTexts(){};
    @Override
    protected void initiateSpinners(){};
    @Override
    protected void initiateEditTextDates(){};
    @Override
    protected void initiateRadioGroups(){};

}
