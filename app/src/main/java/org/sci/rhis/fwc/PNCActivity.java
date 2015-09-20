package org.sci.rhis.fwc;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                     View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener {


    ExpandableListAdapterforPNC listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnc);


        ll = (LinearLayout)findViewById(R.id.llay);


        expListView = new ExpandableListView(this);
        ll.addView(expListView);


        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(PNCActivity.this);
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(PNCActivity.this);
        String queryString =   "{" +
                "pregNo:" + 3 + "," +
                "healthid:" + "43366275025436" + "," +
                "pncMLoad:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "pncmother";
        String jsonRootkey = "PNCMotherInfo";
        Log.d("-->","---=====>"+queryString);
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pnc, menu);
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


        try {
            JSONObject jsonStr = new JSONObject(result);
            String key;

            // woman = PregWoman.CreatePregWoman(json);

            //DEBUG
            Resources res = getResources();
            String[] mainlist = res.getStringArray(R.array.list_item);

            for ( Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                key = ii.next();

                System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key)+"\'");

                ArrayList<String> list = new ArrayList<String>();

                try {
                    JSONArray jsonArray = jsonStr.getJSONArray(key);

                    for (int i = 1; i < jsonArray.length(); i++) {

                        Log.d("-->","---=====>"+jsonArray.get(i).toString());
                        list.add(""+mainlist[i]+""+jsonArray.get(i).toString());


                    }//end for
                    listDataHeader = new ArrayList<String>();
                    listDataChild = new HashMap<String, List<String>>();


                    //  listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");
                    listDataHeader.add("Visit "+jsonArray.get(0).toString() + ":");
                    listDataChild.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapterforPNC(this, listDataHeader, listDataChild);



                    initPage();

                    ll.addView(expListView);
                    expListView.setScrollingCacheEnabled(true);
                    expListView.setAdapter(listAdapter);
                    ll.invalidate();
                    expListView.setAdapter(listAdapter);


                } catch (JSONException e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        }
    }

    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);


        //  expListView.smoothScrollToPosition(expListView.getCount() - 1);




    }

    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncReferCheckBox));
        jsonCheckboxMap.put("pncbreastfeedingonly", getCheckbox(R.id.pncChildOnlyBreastFeedingCheckBox));
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncChildReferCheckBox));
    };

    @Override
    protected void initiateRadioGroups(){};


    @Override
    protected void initiateSpinners() {
        // PNC Mother Info
        jsonSpinnerMap.put("pncbreastcondition", getSpinner(R.id.pncBreastConditionSpinner));
        jsonSpinnerMap.put("pnchematuria", getSpinner(R.id.pncDischargeBleedingSpinner));
        jsonSpinnerMap.put("pncperineum", getSpinner(R.id.pncPerineumSpinner));
        jsonSpinnerMap.put("pncfpmethod", getSpinner(R.id.pncFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("pncsymptom", getSpinner(R.id.pncDrawbackSpinner));
        jsonSpinnerMap.put("pncdisease", getSpinner(R.id.pncDiseaseSpinner));
        jsonSpinnerMap.put("pnctreatment", getSpinner(R.id.pncTreatmentSpinner));
        jsonSpinnerMap.put("pncadvice", getSpinner(R.id.pncAdviceSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));
        jsonSpinnerMap.put("pncreferreason", getSpinner(R.id.pncReasonSpinner));

        // PNC Child Info
        jsonSpinnerMap.put("pncdangersign", getSpinner(R.id.pncChildDangerSignsSpinner));
        jsonSpinnerMap.put("pncsymptom", getSpinner(R.id.pncChildDrawbackSpinner));
        jsonSpinnerMap.put("pncdisease", getSpinner(R.id.pncChildDiseaseSpinner));
        jsonSpinnerMap.put("pnctreatment", getSpinner(R.id.pncChildTreatmentSpinner));
        jsonSpinnerMap.put("pncadvice", getSpinner(R.id.pncChildAdviceSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));;
        jsonSpinnerMap.put("pncreferreason", getSpinner(R.id.pncChildReasonSpinner));
        }

    @Override
    protected void initiateEditTexts() {
        //PNC Mother visit

        //jsonEditTextMap.put("pncserviceId", getEditText(R.id.pncVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncTemperatureValue));
        jsonEditTextMap.put("pncbpsys", getEditText(R.id.pncBloodPresserValueS));
        jsonEditTextMap.put("pncbpdias",getEditText(R.id.pncBloodPresserValueD));
        jsonEditTextMap.put("pnchemoglobin",getEditText(R.id.pncAnemiaHemoglobinValue));
        jsonEditTextMap.put("pncCervixInvolutionValue",getEditText(R.id.pncCervixInvolutionValue));

        //PNC Child visit
        jsonEditTextMap.put("pncchildno", getEditText(R.id.pncNewBornNumber));
       // jsonEditTextMap.put("pregno", getEditText(R.id.pncChildVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMap.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMap.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));

    }

    @Override
    protected void initiateEditTextDates() {
        // PNC Mother Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncServiceDateValue));
        // PNC Child Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncChildServiceDateValue));
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
