package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ADVSearchActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    class FileLoader extends AsyncTask<String, Integer, Integer> {
        Context context;
        FileLoader(Context c) {context = c;};
        protected Integer doInBackground(String... params) {
            loadLocations();
            return 0;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            getSpinner(R.id.advSearchDistrict).setAdapter(zillaAdapter);
            loadVillages.setVisibility(View.GONE);
            setSearchability(true);
        }

        protected void onProgressUpdate (Integer... progress) {
            Toast.makeText(context,"The Village list is Loading", Toast.LENGTH_LONG).show();
        }
    };

    private  Button cancelBtn, searchBtn;
    private  final String SERVLET   = "advancesearch";
    private  final String ROOTKEY   = "advanceSearch";
    private  final String LOGTAG    = "FWC-ADV-SEARCH";


    private ListView searchListView ;
    ArrayList<Person> personsList = new ArrayList<Person>();

    AsyncADVSearchUpdate ADVSearchUpdateTask;

    private  String zillaString = "";
    private  String villageString = "";

    ArrayList<LocationHolder> districtList;
    ArrayList<LocationHolder> upazillaList;
    ArrayList<LocationHolder> unionList;
    ArrayList<LocationHolder> villageList;

    ArrayAdapter<LocationHolder> zillaAdapter;
    ArrayAdapter<LocationHolder> upazilaAdapter;
    ArrayAdapter<LocationHolder> unionAdapter;
    ArrayAdapter<LocationHolder> villageAdapter;

    private StringBuilder jsonBuilder = null;
    private StringBuilder jsonBuilderVillage = null;
    private FileLoader loader = null;

    private JSONObject villJson = null;
    private LocationHolder blanc = new LocationHolder();
    private ProgressBar loadVillages = null;
    private boolean loaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advsearch);
        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cancelBtn=(Button)findViewById(R.id.cancelBtn);
        searchBtn=(Button)findViewById(R.id.advSearchBtn);

        districtList    =  new ArrayList<>();
        upazillaList    =  new ArrayList<>();
        unionList       =  new ArrayList<>();
        villageList     =  new ArrayList<>();

        initialize();
        addAndSetSpinners();
        addListenerOnButton();
        //Utilities.MakeVisible(this, R.id.loadingPanel);

        //FileLoader loader = new FileLoader();
        //loader.execute();
        //AsyncTask<Void, Void, Void> loadVillage = new AsyncTask()
        loadVillages = (ProgressBar)findViewById(R.id.advSearchProgressBar);
        loadVillages.setVisibility(View.VISIBLE);

        jsonBuilder = new StringBuilder();
        jsonBuilderVillage = new StringBuilder();
        loader = new FileLoader(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(!loader.isCancelled()) {
            setSearchability(false);
            loader.execute();
        }
        //loadVillages.setVisibility(View.GONE);
        //Utilities.MakeInvisible(this, R.id.loadingPanel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        //loadVillages = (ProgressBar)findViewById(R.id.advSearchProgressBar);
       // loadVillages.setVisibility(View.VISIBLE);
        //loadLocations();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        //loadVillages.setVisibility(View.GONE);
        jsonSpinnerMap.get("gender").setSelection(1); //select woman by default
    }

    private void loadLocations() {

        try {

            zillaString = LocationHolder.getZillaUpazillaUnionString();

            districtList.add(blanc);
            LocationHolder.loadListFromJson(zillaString, "nameEnglish", "nameBangla", "Upazila", districtList);

            //set zilla spinner
            zillaAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, districtList);
            zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            try {
                if(villJson == null) {
                    villJson = LocationHolder.getVillageJson();
                }
            } catch (Exception jse) {
                Log.e(LOGTAG, "JSON Exception in loading village");
                Utilities.printTrace(jse.getStackTrace());
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void handlePersonListClick( Person person) {
        Intent intent = new Intent();
        intent.putExtra("HealthId", person.getHealthId());
        intent.putExtra("HealthIdType", person.getIdIndex());
        setResult(RESULT_OK, intent);
        finishActivity(ActivityResultCodes.ADV_SEARCH_ACTIVITY);
        finish();

    }

    private void initList(){

        searchListView = (ListView) findViewById(R.id.search_result);
        PersonAdapter personAdapter = new PersonAdapter(this, R.layout.listview_item_row, personsList);
        View header = getLayoutInflater().inflate(R.layout.listview_header_row, null);

        if(searchListView.getHeaderViewsCount() < 1 ) {
            searchListView.addHeaderView(header);
        }
        searchListView.setAdapter(personAdapter);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position > 0) {
                    handlePersonListClick(personsList.get(position - 1));
                }
            }
        });
    }

    private void loadVillageFromJson(
            String zilla,
            String upazila,
            String union,
            ArrayList<LocationHolder> holderList) {


        try{
            if(villJson == null) {
                villJson = LocationHolder.getVillageJson();
            }

            if( union.equals("none") || upazila.equals("none") || zilla.equals("none") ||
                union.equals("") || upazila.equals("") || zilla.equals("")) {
                return;
            }
            JSONObject unionJson =
            villJson.getJSONObject(zilla).getJSONObject(upazila).getJSONObject(union);

            Log.d(LOGTAG, "Union deatails:\n\t" + union.toString());

            for(Iterator<String> mouzaKey = unionJson.keys(); mouzaKey.hasNext();) {
                String mouza = mouzaKey.next();
                JSONObject mouzaJson = unionJson.getJSONObject(mouza);

                for(Iterator<String> villageCode = mouzaJson.keys(); villageCode.hasNext();) {
                    String code = villageCode.next();
                    holderList.add(
                        new LocationHolder(
                                code+"_"+mouza,
                                mouzaJson.getString(code),
                                mouzaJson.getString(code),
                                ""));

                }

                Log.d(LOGTAG, "Mouja - Village: " + mouza + " -> " + unionJson.getString(mouza));

            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON KEY MISSING:" +
                    "\n\t");
            jse.printStackTrace();
        }
        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void loadJsonFile(String fileName, StringBuilder jsonBuilder) throws IOException{
        InputStream is = getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        jsonBuilder.append(new String(buffer, "UTF-8"));
    }

    private void addAndSetSpinners() {
        //getSpinner(R.id.Clients_District).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchDistrict).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchUpazila).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchUnion).setOnItemSelectedListener(this);
        getSpinner(R.id.advSearchVillage).setOnItemSelectedListener(this);


        zillaAdapter    = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        upazilaAdapter  = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        unionAdapter    = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        villageAdapter  = new ArrayAdapter(this, android.R.layout.simple_spinner_item);

        unionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private JSONObject buildQueryHeader() throws JSONException {
        //Log.e("Selected District Value",String.valueOf(distValue));
        //get info from database
        String queryString = "{}";
        try {
            queryString = "{" +
                    "\"zilla\":" + districtList.get(getSpinner(R.id.advSearchDistrict).getSelectedItemPosition()).getCode() + "," +
                    "\"upz\":" + upazillaList.get(getSpinner(R.id.advSearchUpazila).getSelectedItemPosition()).getCode() + "," +
                    "\"union\":" + unionList.get(getSpinner(R.id.advSearchUnion).getSelectedItemPosition()).getCode() + "," +
                    "\"villagemouza\":" + villageList.get(getSpinner(R.id.advSearchVillage).getSelectedItemPosition()).getCode() +
                    "}";
            // Log.e("selected Item's Value", String.valueOf(distValue));
            Log.d("QueryStrig", queryString);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            Log.e(LOGTAG, "System not ready yet ");
        }
        return new JSONObject(queryString);
    }

    private void advSearchSaveToJson() {
        ADVSearchUpdateTask = new AsyncADVSearchUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader();
            Utilities.getEditTexts(jsonEditTextMap, json);
            getSpecialCases(json);

            Log.d("ADVSearch JSON 2SERVLET", json.toString());

            ADVSearchUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);


        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Exception: " + jse.getMessage());
        }

    }

    private void getSpecialCases(JSONObject json) throws JSONException {
        json.put("gender", getSpinner(R.id.advClientsSexSpinner).getSelectedItemPosition() + 1);
    }

    public void addListenerOnButton() {

        final Context context = this;

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.advSearchBtn) {
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
        Log.d(LOGTAG, result);
        JSONObject json;
        JSONObject jsonPerson;
        try {
            json = new JSONObject(result);

            if (json.has("count") && json.getInt("count") > 0) { // if the result is present
                personsList.clear();
            }

            for (int i=1; i<= (json.getInt("count")); i++) {


                jsonPerson = json.getJSONObject(String.valueOf(i));
                personsList.add(new Person(jsonPerson.getString("name"),
                        jsonPerson.getString("fatherName"),
                        jsonPerson.getString("healthId"),
                        jsonPerson.getInt("healthIdPop") == 1 ? 0 : 4,
                        jsonSpinnerMap.get("gender").getSelectedItemPosition() != 1 ? R.drawable.man : R.drawable.woman));

            }
            initList();
            //searchListView.setAdapter(personsList);
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }            }
    @Override
    protected void initiateCheckboxes() {

    }

    @Override
    protected void initiateEditTexts() {
        jsonEditTextMap.put("name", getEditText(R.id.advClient_name));
        getEditText(R.id.advClient_name).setFilters(new InputFilter[]{new InputFilter.AllCaps()});
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

        //Utilities.MakeVisible(this, R.id.loadingPanel);

        switch (parent.getId()) {
            case R.id.advSearchDistrict:
                //LocationHolder location = (LocationHolder)parent.getSelectedItem();

                LocationHolder zilla = districtList.get(position);

                upazillaList.clear();
                upazilaAdapter.clear();
                upazillaList.add(blanc);
                LocationHolder.loadListFromJson(
                        zilla.getSublocation(),
                        "nameEnglishUpazila",
                        "nameBanglaUpazila",
                        "Union",
                        upazillaList);
                for (LocationHolder holder : upazillaList) {
                    Log.d(LOGTAG, "Upazila: -> " + holder.getBanglaName());
                }

                upazilaAdapter.addAll(upazillaList);
                upazilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                getSpinner(R.id.advSearchUpazila).setAdapter(upazilaAdapter);

                break;
            case R.id.advSearchUpazila:

                LocationHolder upazila = upazillaList.get(position);
                unionList.clear();
                unionAdapter.clear();
                unionList.add(new LocationHolder());
                LocationHolder.loadListFromJson(
                        upazila.getSublocation(),
                        "nameEnglishUnion",
                        "nameBanglaUnion",
                        "",
                        unionList);
                for (LocationHolder holder : unionList) {
                    Log.d(LOGTAG, "Union: -> " + holder.getBanglaName());
                }
                unionAdapter.addAll(unionList);
                getSpinner(R.id.advSearchUnion).setAdapter(unionAdapter);
                break;
            case R.id.advSearchUnion:
                villageList.clear();
                villageAdapter.clear();
                villageList.add(blanc);

                loadVillageFromJson(
                        ((LocationHolder) getSpinner(R.id.advSearchDistrict).getSelectedItem()).getCode().split("_")[0],
                        ((LocationHolder) getSpinner(R.id.advSearchUpazila).getSelectedItem()).getCode(),
                        ((LocationHolder) getSpinner(R.id.advSearchUnion).getSelectedItem()).getCode(),
                        villageList);
                for (LocationHolder holder : villageList) {
                    Log.d(LOGTAG, "Village: -> " + holder.getBanglaName());
                }

                villageAdapter.addAll(villageList);
                getSpinner(R.id.advSearchVillage).setAdapter(villageAdapter);

                Log.d(LOGTAG, "Union Case: -> ");

                break;
            case R.id.advSearchVillage:
                Log.d(LOGTAG, "Village Case: -> ");
                break;
            default:
                Log.e(LOGTAG, "Unknown spinner: " + parent.getId()
                        + " -> " + getResources().getResourceEntryName(parent.getId()));

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setSearchability(boolean enable) {
        if(enable) {
            Utilities.Enable(this, R.id.advSearchBtn);
        } else {
            Utilities.Disable(this, R.id.advSearchBtn);
        }
    }
}
