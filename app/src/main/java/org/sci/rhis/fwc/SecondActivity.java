
package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class SecondActivity extends ClinicalServiceActivity  {

    private Button button;
    private PregWoman woman;
    private Vector<Pair<String, Integer>>  deliveryHistoryMapping;
    private int providerCode;

    private View mClientIntroLayout;
    private View mClientInfoLayout;
    Boolean flag=false;

    AsyncClientInfoUpdate clientInfoQueryTask;
    AsyncClientInfoUpdate clientInfoUpdateTask;

    final private String SERVLET = "handlepregwomen";
    final private String ROOTKEY = "pregWomen";

    private BigInteger responseID= BigInteger.valueOf(0);
    EditText lmpEditText;
    EditText eddEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ProviderInfo provider = ProviderInfo.getProvider();

        TextView FWCName = (TextView) findViewById(R.id.fwc_heading);
        FWCName.setText(provider.getProviderFacility());

        providerCode = Integer.parseInt(String.valueOf(provider.getProviderCode()));
        Log.i("SecondActivity", "" + provider.getProviderFacility());

        Log.e("aaf", "" + provider.getProviderFacility()+ providerCode);
        initialize();//super class
        Spinner staticSpinner = (Spinner) findViewById(R.id.ClientsIdentityDropdown);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.Health_Id, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        lmpEditText = (EditText) findViewById(R.id.lmpDate);
        eddEditText = (EditText) findViewById(R.id.edd);

        lmpEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date lmp = sdf.parse(lmpEditText.getText().toString());

                            Date edd = Utilities.addDateOffset(lmp, 240);
                            eddEditText.setText(sdf.format(edd));

                        } catch (ParseException PE) {

                        }

                    }
                }

        );

    }

    public void startSearch(View view) {
        Spinner searchOptions = (Spinner)findViewById(R.id.ClientsIdentityDropdown);
        EditText searchableId = (EditText)findViewById(R.id.searchableTextId);
        //TODO - remove
        long index = (searchOptions.getSelectedItemId() + 1);
        String stringId= (String) searchOptions.getSelectedItem();
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

        TextView mHealthIdLayout = (TextView) findViewById(R.id.health_id);
        mHealthIdLayout.setVisibility(View.VISIBLE);

        TextView healthId = (TextView) findViewById(R.id.health_id);
        healthId.setText(String.valueOf(stringId) + ": " + String.valueOf(id));

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
                    System.out.println("JSON Exception Thrown(test):\n " );
                    jse.printStackTrace();
                }
            }
        }

        HashMap<String, Pair<Spinner, Integer>> clientSpinnerMap= new HashMap<>(1); //fixed capacity ??
        clientSpinnerMap.put("cBloodGroup", Pair.create((Spinner) findViewById(R.id.Blood_Group_Dropdown), R.array.Blood_Group_Dropdown));

        //manipulateJson(json);
        Utilities.setSpinners(clientSpinnerMap, json, this);
        Utilities.setCheckboxes(jsonCheckboxMap, json);
    }

    @Override
    public void callbackAsyncTask(String result) { //Get results back from healthId search

        try {
            JSONObject json = new JSONObject(result);
            String key;

            //DEBUG response from servlet
            for ( Iterator<String> ii = json.keys(); ii.hasNext(); ) {
                key = ii.next();
                System.out.println("1.Key:" + key + " Value:\'" + json.get(key)+"\'");
            }



            if(json.has("cNewMCHClient"))
            {
                woman = PregWoman.CreatePregWoman(json);

                if(json.get("False").toString().equals("")) { //Client exists
                    populateClientDetails(json, DatabaseFieldMapping.CLIENT_INTRO);
                    responseID= new BigInteger(json.get("cHealthID").toString());
                if(woman != null) {
                    manipulateJson(json);
                    populateClientDetails(json, DatabaseFieldMapping.CLIENT_INFO);
                    woman.UpdateUIField(this);
                    Utilities.Disable(this, R.id.clients_info_layout);

                    EditText HouseGREditText = (EditText) findViewById(R.id.Clients_House_No);
                    EditText MobileNoEditText = (EditText) findViewById(R.id.Clients_Mobile_no);

                    HouseGREditText.setFocusable(false);
                    MobileNoEditText.setFocusable(false);

                }

                    // To Make disable desired fields
                    Utilities.Disable(this, R.id.clients_intro_layout);
                    //Utilities.Enable(this, R.id.clients_intro_layout);

                }

            }
            else
            {

                ////
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown( At callbackAsyncTask ):\n " );
            jse.printStackTrace();
        }
    }


    public void addListenerOnButton() {

        final Context context = this;

       Button  button1 = (Button) findViewById(R.id.nonregiser);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, NRCActivity.class);
                intent.putExtra("Provider",providerCode);
                startActivity(intent);
            }
        });
    }

    public void startANC(View view) {
        Intent intent = new Intent(this, ANCActivity.class);
        if(checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.ANC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for ANC, verify ...", Toast.LENGTH_LONG).show();
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
        if(checkClientInfo() && woman.isEligibleFor(PregWoman.PREG_SERVICE.PNC)) {
            intent.putExtra("PregWoman", woman);
            intent.putExtra("Provider", ProviderInfo.getProvider());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Too Late for PNC, verify ...", Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkClientInfo() {
        if(woman == null ) {
            Toast.makeText(this, "No Client, Get Client Information first ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onClickSaveClient(View view) {
        /*
        if(view.getTag() != null && view.getTag().equals("DateField")) {
            datePickerDialog.show(datePickerPair.get(view.getId()));
        }*/
            saveClientToJson();
            Toast.makeText(this, "Clicked...", Toast.LENGTH_LONG).show();
            System.out.print("Clicked");
    }

   ////for complication history
    private void initializeJsonManipulation() {
        deliveryHistoryMapping = new Vector<Pair<String, Integer>>(9);
        //The order is important
        deliveryHistoryMapping.addElement(Pair.create("bleeding",            R.id.previousDeliveryBleedingCheckBox)); //0
        deliveryHistoryMapping.addElement(Pair.create("delayedDelivery", R.id.delayedBirthCheckBox));//1
        deliveryHistoryMapping.addElement(Pair.create("blockedDelivery", R.id.blockedDeliveryCheckBox));//2
        deliveryHistoryMapping.addElement(Pair.create("blockedPlacenta", R.id.placentaInsideUterusCheckBox));//3
        deliveryHistoryMapping.addElement(Pair.create("deadBirth",       R.id.giveBirthDeadCheckBox));//4
        deliveryHistoryMapping.addElement(Pair.create("lived48Hour",     R.id.newbornDieWithin48hoursCheckBox));//5
        deliveryHistoryMapping.addElement(Pair.create("edemaSwelling", R.id.swellingLegsOrWholeBodyCheckBox));//6
        deliveryHistoryMapping.addElement(Pair.create("convulsion", R.id.withConvulsionSenselessCheckBox));//7
        deliveryHistoryMapping.addElement(Pair.create("caesar", R.id.caesarCheckBox));//8
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
/*
    private void buildJson(JSONObject complicated) {
        try {

            for (int i = 1; i <= complicated.names().length(); i++) {
                if (complicated.get(complicated.names().getString(i))==1)
                // complicated.put(deliveryHistoryMapping.get(Integer.valueOf(array[i]) - 1).first, 1);
            }

        }catch (JSONException jse) {
                jse.getMessage();
                jse.printStackTrace();
        }
    }
*/
    /////

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
    }
    @Override
    protected void initiateEditTexts(){
        jsonEditTextMap.put("para",getEditText(R.id.para));
        jsonEditTextMap.put("gravida",getEditText(R.id.gravida));
        jsonEditTextMap.put("boy",getEditText(R.id.SonNum));
        jsonEditTextMap.put("girl",getEditText(R.id.DaughterNum));
        jsonEditTextMap.put("houseGRHoldingNo",getEditText(R.id.Clients_House_No));
        jsonEditTextMap.put("mobileNo",getEditText(R.id.Clients_Mobile_no));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("FacilityName",getTextView(R.id.fwc_heading));
    };
    @Override
    protected void initiateSpinners(){
        jsonSpinnerMap.put("bloodGroup",getSpinner(R.id.Blood_Group_Dropdown));
    };
    @Override
    protected void initiateMultiSelectionSpinners(){}
    @Override
    protected void initiateEditTextDates(){
          jsonEditTextDateMap.put("lmp", getEditText(R.id.lmpDate));
    }

    protected void initiateEditTextDatesPlusFormat(){
        jsonEditTextDatePlusFormatMap.put("edd", getEditText(R.id.edd));
    }

    @Override
    protected void initiateRadioGroups(){};

    private void saveClientToJson() {
       // Toast.makeText(this, "Clicked from here...", Toast.LENGTH_LONG).show();
       // AsyncClientInfoUpdate saveClient = new AsyncClientInfoUpdate(this);
       clientInfoUpdateTask = new AsyncClientInfoUpdate(this);
        JSONObject json;
        JSONObject complicated = null;
        try {
            json = buildQueryHeader(false);
           // Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getEditTextDatesPlusFormat(jsonEditTextDatePlusFormatMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            //buildJson(complicated);
            Utilities.getSpinnersValuesWithoutSlash(jsonSpinnerMap, json);
            //  Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getSpecialCases(json);
            Log.e("Pregwomen", "***************In progress :" + json.toString());
           // clientInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
           String outputJSON;
            outputJSON="{\"pregNo\":\"\",\n" +              //done
                    "\"healthId\":\"11332922450608\",\n" + //done
                    "\"providerId\":\"6608\",\n" +          //done
                    "\"houseGRHoldingNo\":\"2\",\n" + //done
                    "\"mobileNo\":\"0222\",\n" + //done
                    "\"lmp\":\"2015-10-20\",\n" +   //done
                    "\"edd\":\"26+Jul+2016\",\n" + //done
                    "\"para\":\"1\",\n" +   //done
                    "\"gravida\":\"4\",\n" + //done
                    "\"boy\":\"1\",\n" +//done
                    "\"girl\":\"0\",\n" +//done
                    "\"lastChildAge\":11,\n" + //done
                    "\"height\":64,\n" +  //done
                    "\"bloodGroup\":\"A+\",\n" +
                    "\"tt1\":1,\"ttDate1\":\"2015-10-20\",\"tt2\":\"\",\"ttDate2\":\"\",\"tt3\":\"\",\"ttDate3\":\"\",\"tt4\":\"\",\"ttDate4\":\"\",\"tt5\":\"\",\"ttDate5\":\"\",\n" +
                    "complicatedHistory\":\"\",\n" +
                    "\"complicatedHistoryNote\":\"9\"}";

            JSONObject z;
            z = new JSONObject(outputJSON);
           // Log.d("How the json looks", "***************In progress b4 json:" + z.toString());
            // outputJSON={"pregNo":"","healthId":"38236987455329","providerId":"6608","houseGRHoldingNo":"","mobileNo":"01678945666","lmp":"2015-10-12","edd":"18+Jul+2016","para":"0","gravida":"1","boy":"0","girl":"0","lastChildAge":0,"height":64,"bloodGroup":"none","tt1":"","ttDate1":"","tt2":"","ttDate2":"","tt3":"","ttDate3":"","tt4":"","ttDate4":"","tt5":"","ttDate5":"","complicatedHistory":"","complicatedHistoryNote":"1,2,3,9"};

            clientInfoUpdateTask.execute(z.toString(), SERVLET, ROOTKEY);
          //  System.out.print("In Save, Client Json in Query:" + json.toString());
        }
        catch (JSONException jse) {
            Log.e("Pregwomen", "JSON Exception: " + jse.getMessage());
        }

    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:\"" +responseID + "\"," +
                "providerid:\""+ ProviderInfo.getProvider().getProviderCode()+"\","+
                "pregno:\"\""+
                "}";
        return new JSONObject(queryString);
    }

    public void getSpecialCases(JSONObject json) {
        try {

            Integer month = Integer.parseInt(getEditText(R.id.lastChildYear).getText().toString())*12;
            month+= Integer.parseInt(getEditText(R.id.lastChildMonth).getText().toString());

            Integer feet=Integer.parseInt(getEditText((R.id.heightFeet)).getText().toString())*12;
            feet+= Integer.parseInt(getEditText(R.id.heightInch).getText().toString());

            json.put("lastChildAge", month);
            json.put("height", feet);
        }
        catch (JSONException jse) {

        }
    }
}
