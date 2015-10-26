package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ADVSearchActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private HashMap<String, Pair<Integer, Integer>> districtCodeMap;
    private HashMap<String, Integer> upazilaCodeMap;
    private HashMap<String, Integer> unionCodeMap;
    private HashMap<String, Pair<Integer, Integer>> villageCodeMap;

    private  String selectedDistName, selectedUpazilaName,selectedUnionName, selectedVillageName, vilStringValue, villMouza, sumString="";
    private  int divValue, distValue, upValue, unValue, vilValue, mouzaValue;
    private  Button cancelBtn, searchBtn;
    private  String SERVLET = "advancesearch";
    private  String ROOTKEY = "advanceSearch";
    private String[] jsonString = new String[3];
    private ListView searchListView ;
    private ArrayAdapter<String> listAdapter ;
    AsyncADVSearchUpdate ADVSearchUpdateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advsearch);
        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cancelBtn=(Button)findViewById(R.id.cancelBtn);
        searchBtn=(Button)findViewById(R.id.searchBtn);

        initialize();
        ZillaMapping();
        UpMapping();
        unionMapping();
        villageMapping();
        addListenerOnButton();
    }



    private void initList(){




        ArrayList<String> clientsList = new ArrayList<String>();
        clientsList.addAll(Arrays.asList(jsonString));

        ArrayAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  clientsList);
        searchListView = (ListView) findViewById(R.id.search_result);

        searchListView.setAdapter( listAdapter );
    }


  private String  ZillaMapping(){
      districtCodeMap = new HashMap<String,Pair<Integer, Integer>>();
      districtCodeMap.put("ব্রাক্ষ্মণবাড়িয়া", Pair.create(12, 20));
      districtCodeMap.put("হবিগঞ্জ", Pair.create(36, 60));
      districtCodeMap.put("টাঙ্গাইল", Pair.create(93, 30));


      ArrayList<String> distLIst = new ArrayList<String>();
      distLIst.addAll(districtCodeMap.keySet());

      ArrayAdapter<String> adapter = new ArrayAdapter<String>(
              this, android.R.layout.simple_spinner_item, distLIst);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      Spinner spnDist = (Spinner) findViewById(R.id.Clients_District);
      spnDist.setAdapter(adapter);
      selectedDistName = spnDist.getSelectedItem().toString();
      distValue = districtCodeMap.get(selectedDistName).first;
      divValue = districtCodeMap.get(selectedDistName).second;
      Log.e("selected division Value", String.valueOf(divValue));
      Log.e("selected district Value", String.valueOf(distValue));

        return  (distValue + "_" + divValue);
  }


private int UpMapping(){
    upazilaCodeMap = new HashMap<String,Integer>();
    switch(distValue) {
        case 36:
            upazilaCodeMap.put("মাধবপুর ", 71);
            break;
        case 93:
            upazilaCodeMap.put("বাসাইল ", 36);
            break;
    }
    ArrayList<String> upLIst = new ArrayList<String>();
    upLIst.addAll(upazilaCodeMap.keySet());

    ArrayAdapter<String> upAdapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, upLIst);
    upAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    Spinner spnUpz = (Spinner) findViewById(R.id.Clients_Upazila);
    spnUpz.setAdapter(upAdapter);

    spnUpz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View view,
                                   int position, long row_id) {
            switch (position) {
                case 1:
                    break;
                case 2:

                    break;

            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    });

    selectedUpazilaName = spnUpz.getSelectedItem().toString();
    Log.e("selected Up Value", String.valueOf(selectedUpazilaName));
    upValue = upazilaCodeMap.get(selectedUpazilaName);
    Log.e("selected upazila Value", String.valueOf(upValue));

    return upValue;
}

private  int unionMapping(){
   // upValue =71;
    unionCodeMap = new HashMap<String,Integer>();
    switch(upValue) {
        case 71:
            unionCodeMap.put("আদাঐর",16);
            unionCodeMap.put("শাহজাহানপুর",94);
            break;
        case 36:
            unionCodeMap.put("কাঞ্চনপুর",59);
            unionCodeMap.put("কাশিল",71);
            break;
    }
    ArrayList<String> unLIst = new ArrayList<String>();
    unLIst.addAll(unionCodeMap.keySet());
    ArrayAdapter<String> unAdapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, unLIst);
    unAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    Spinner spnUN = (Spinner) findViewById(R.id.Clients_Union);
    spnUN.setAdapter(unAdapter);
    selectedUnionName = spnUN.getSelectedItem().toString();
    unValue = unionCodeMap.get(selectedUnionName);
    Log.e("selected Union Name", String.valueOf(selectedUnionName));
    Log.e("selected Union Value", String.valueOf(unValue));

    return unValue;
}
    private String villageMapping(){
        villageCodeMap = new HashMap<String,Pair<Integer, Integer>>();
        switch(unValue) {

            case 94:
                villageCodeMap.put("বান্দারিয়া",Pair.create(01, 164));
                villageCodeMap.put("ফারোদপুর",Pair.create(02, 324));
                break;
            case 59:
                villageCodeMap.put("যৌতুকী",Pair.create(01, 458));
                villageCodeMap.put("তারাবাড়ী",Pair.create(05, 547));
                break;
            case 71:
                villageCodeMap.put("বাংড়া",Pair.create(01, 167));
                villageCodeMap.put("পিচুরী",Pair.create(01, 816));
                break;
            case 16:
                //villageCodeMap.put("মিঠাপুকুর",Pair.create(02, 368));
               villageCodeMap.put("দক্ষিণমোহাম্মদপুর",Pair.create(01, 276));
                break;
        }
        ArrayList<String> vilLIst = new ArrayList<String>();
        vilLIst.addAll(villageCodeMap.keySet());
        ArrayAdapter<String> vilAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, vilLIst);
        vilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spnVillage = (Spinner) findViewById(R.id.Clients_Village);
        spnVillage.setAdapter(vilAdapter);
        selectedVillageName = spnVillage.getSelectedItem().toString();
        vilValue = villageCodeMap.get(selectedVillageName).first;
        mouzaValue = villageCodeMap.get(selectedVillageName).second;
        Log.e("selected Village Name", String.valueOf(selectedVillageName));
        if(vilValue<10)
        {
            vilStringValue = "0" + String.valueOf(vilValue);
        }

        Log.e("selected Village Value", String.valueOf(vilStringValue));
        Log.e("selected mouza Value", String.valueOf(mouzaValue));
       // villMouza = vilStringValue + "_" + mouzaValue;
        villMouza = "\"\"";

                Log.e(String.valueOf(villMouza), "selected villMouza Value");

        return villMouza;
    }


    private JSONObject buildQueryHeader() throws JSONException {
        Log.e("Selected District Value",String.valueOf(distValue));
        //get info from database
        String queryString =   "{" +
                "\"zilla\":" + ZillaMapping() + "," +
                "\"upz\":" + UpMapping()+ "," +
                "\"union\":" + unionMapping()+ "," +
                "\"villagemouza\":" + villageMapping()+
                "}";
        // Log.e("selected Item's Value", String.valueOf(distValue));
        Log.e("QueryStrig",queryString);
        return new JSONObject(queryString);
    }

    private void advSearchSaveToJson() {
        ADVSearchUpdateTask = new AsyncADVSearchUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader();
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);

            Log.e("ADVSearch JSON 2SERVLET", json.toString());

            ADVSearchUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);


        } catch (JSONException jse) {
            Log.e("NRC", "JSON Exception: " + jse.getMessage());
        }

    }
    public void addListenerOnButton() {

        final Context context = this;

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.searchBtn)
                {
                    advSearchSaveToJson();

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advsearch, menu);
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
        Log.e("Found result", result);
        //String[] jsonString = new String[100];
        JSONObject json;
        try {
            json = new JSONObject(result);
            String key;

            //DEBUG
            for (int i=1; i<= (json.getInt("count")); i++) {

               // System.out.println("1.Key:" + key + " Value:\'" + json.get(key) + "\'");

            JSONObject json1 = new JSONObject(json.getString(String.valueOf(i)));
            JSONObject json2 = new JSONObject(json.getString(String.valueOf(i)));


               String sss = json2.getString("name") + " | " + json1.getString("healthId") ;


                jsonString[i-1] = sss;
               // sumString = sumString + sss;
                //Log.e("Found", sss);

            }


         // for (int i=0; i< (json.getInt("count")); i++)
            Log.e("Found", Arrays.toString(jsonString));

            initList();
        }
            catch (JSONException jse) {
                jse.printStackTrace();
            }

    }

    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("name", getEditText(R.id.advClient_name));
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("gender", getSpinner(R.id.advClientsSexSpinner));

    }

    @Override
    protected void initiateMultiSelectionSpinners() {

    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
