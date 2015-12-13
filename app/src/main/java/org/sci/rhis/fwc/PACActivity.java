package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity {


    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private  final String LOGTAG    = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;


    private LinearLayout lay_pac;

    private Context con;
    private LinearLayout lay_frag_pac;
    private PregWoman mother;
   // private ProviderInfo provider;

    private JSONObject jsonRespMother = null;
    ExpandableListView expListView;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListAdapterforPAC listAdapter;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //initiate multiselect spinners
        setMultiSelectSpinners();
        con = this;

        lay_frag_pac = (LinearLayout)findViewById(R.id.history_lay);
        Intent intent = getIntent();
        mother = getIntent().getParcelableExtra("PregWoman");

        //int name = intent.getIntExtra("PregWoman",0);
        //provider = getIntent().getParcelableExtra("Provider");

        Log.d("------>>>------------", "START-----PAC----" );
        ll = (LinearLayout)findViewById(R.id.llay);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

        AsyncPNCInfoUpdate sendPostReqAsyncTask = new AsyncPNCInfoUpdate(PACActivity.this);

        String queryString =   "{" +
                "pregNo:" + mother.getPregNo() + "," +
                "healthId:" + mother.getHealthId() + "," +
                "pacLoad:" + "retrieve" +
                "}";

        String servlet = "pac";
        String jsonRootkey = "PACInfo";
        Log.d(LOGTAG, "Mother Part:\n" + queryString);
        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootkey);



        getCheckbox(R.id.pacOtherCheckBox).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.pacOtherCheckBox:
                        setItemVisible(R.id.pacOtherCenterNameSpinner, isChecked);
                        break;
                }
            }
        });
    }

    private void setMultiSelectSpinners() {
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PACProblemDropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Danger_Sign_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.ANC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDangerSignsSpinner);
        if(multiSelectionSpinner == null){
            Log.d("------"+ dangersignlist.get(1),".........");
        }
        multiSelectionSpinner.setItems(dangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDiseaseSpinner);
        multiSelectionSpinner.setItems(diseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});
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




    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
        //  expListView.smoothScrollToPosition(expListView.getCount() - 1);
    }

    @Override
    public void callbackAsyncTask(String result) {
        Log.d(LOGTAG, "PAC Response Received:\n\t" + result);
        //handleExistingChild(result);

        ll.removeAllViews();
        Log.d(LOGTAG, "Handle Mother:\n" + result);

        try {
            jsonRespMother = new JSONObject(result);
            String key;

        //    lastPncVisit = jsonRespMother.getInt("count");
        //    getTextView(R.id.pncVisitValue).setText(String.valueOf(lastPncVisit+1)); //next visit

            //Check if eligible for new PNC
//            if(jsonRespMother.has("pncStatus") &&
//                    jsonRespMother.getBoolean("pncStatus")) {
//                Utilities.MakeInvisible(this, R.id.pncMotherInfo);
//                Toast.makeText(this, "Mother is not eligible for new PNC", Toast.LENGTH_LONG).show();
//            } else {
//                //get outcome date and populate ideal pnc visit info
//                mother.setActualDelivery(jsonRespMother.getString("outcomeDate"), "yyyy-MM-dd");
//                setPncVisitAdvices();
//
//                showHidePncDeleteButton(jsonRespMother, true);
//
//            }
            //

            int in=1;

            //DEBUG
            Resources res = getResources();
            int item=0;
            for (Iterator<String> ii = jsonRespMother.keys(); ii.hasNext(); ) {
                key = ii.next();
                item++;
                Log.d("--:::>", "---key=====>" + item);
            }

            for (Iterator<String> ii = jsonRespMother.keys(); ii.hasNext(); ) {
                key = ii.next();


                System.out.println("1.Key:" + key + " Value:\'" + jsonRespMother.get(key) + "\'");

                //if(in == item-3)
                if(in > jsonRespMother.getInt("count"))
                    break;
                //It's just json and not so hard to understand, keep getiing exception at this point

                JSONObject jsonRootObject = jsonRespMother.getJSONObject("" + in);
                Log.d("--:::>", "---serviceSource=====>" + jsonRootObject.getString("serviceSource"));

                String complicationsign = jsonRootObject.getString("complicationSign");
                String serviceSource = jsonRootObject.getString("serviceSource");
                String anemia = jsonRootObject.getString("anemia");
                String referCenterName = jsonRootObject.getString("referCenterName");
                String treatment = jsonRootObject.getString("treatment");
                String perineum = jsonRootObject.getString("perineum");
                String uterusInvolution = jsonRootObject.getString("uterusInvolution");
                String visitDate = jsonRootObject.getString("visitDate");
                String bpDiastolic = jsonRootObject.getString("bpDiastolic");
                String disease = jsonRootObject.getString("disease");
                String bpSystolic = jsonRootObject.getString("bpSystolic");
                String hematuria = jsonRootObject.getString("hematuria");
                String temperature = jsonRootObject.getString("temperature");
                String referReason = jsonRootObject.getString("referReason");
                String refer = jsonRootObject.getString("refer");
               // String edema = jsonRootObject.getString("edema");
                String serviceId = jsonRootObject.getString("serviceId");
                String hemoglobin = jsonRootObject.getString("hemoglobin");
                String FPMethod = jsonRootObject.getString("FPMethod");
               // String breastCondition = jsonRootObject.getString("breastCondition");
                String advice = jsonRootObject.getString("advice");
                String symptom = jsonRootObject.getString("symptom");
                // String  pncStatus= jsonRootObject.getString("pncStatus");
                //Log.d("--:::>", "---complicationsign=====>"+jsonStr.get(key));

                ArrayList<String> list = new ArrayList<String>();

                String[] details;
                Resources res1 = con.getResources();
                String str1 = "";

                list.add("" + getString(R.string.visitDate) + " " + visitDate);
                list.add("" + getString(R.string.complicationsign) + " " + symptom);
                list.add("" + getString(R.string.temperature) + " " + temperature);
                list.add("" + getString(R.string.bpSystolic) + " " + bpSystolic+"/"+bpDiastolic);

                // for anemia value
                str1 = "";
                str1 = anemia;
                Log.d("--:::>", "---complicationsign=====>"+str1);
                String[] animals = str1.split(" ");
                String temp = "";
                details = res1.getStringArray(R.array.Anemia_Dropdown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if(animal.length()>0)
                        temp = temp+" "+details[Integer.parseInt(animal)];
                }
                list.add("" + getString(R.string.anemia) + " " + temp);


                list.add("" + getString(R.string.hemoglobin) + " " + hemoglobin+"%");


                // for edema value
                str1 = "";
//                str1 = edema;
//
//                animals = str1.split(" ");
//                temp = "";
//                details = res1.getStringArray(R.array.Edema_Dropdown);
//                for (String animal : animals) {
//                    System.out.println(animal);
//                    if(animal.length()>0)
//                        temp = temp+" "+details[Integer.parseInt(animal)];
//                }
//                list.add("" + getString(R.string.edema) + " " + temp.trim());

                // for breastCondition value
                str1 = "";
//                str1 = breastCondition;
//
//                animals = str1.split(" ");
//                temp = "";
//                details = res1.getStringArray(R.array.Breast_Condition_DropDown);
//                for (String animal : animals) {
//                    System.out.println(animal);
//                    if(animal.length()>0)
//                        temp = temp+" "+details[Integer.parseInt(animal)];
//                }
//
//                list.add("" + getString(R.string.breastCondition) + " " + temp);

                // for hematuria value
                str1 = "";
                str1 = uterusInvolution;
                str1 = str1.replaceAll("[^0-9]+", " ");
                System.out.println("-----"+str1);
                animals = str1.split(" ");
                System.out.println("---splitted--");

                temp = "";
                details = res1.getStringArray(R.array.Cervix_Involution_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if(animal.length()>0)
                        temp = temp+" "+details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.uterusInvolution) + " " + temp);


                // for hematuria value
                str1 = "";
                str1 = hematuria;
                str1 = str1.replaceAll("[^0-9]+", " ");
                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Family_Planning_Methods_DropDown);// discharge_bleeding_dropdown
                for (String animal : animals) {
                    System.out.println(animal);
                    if(animal.length()>0)
                        temp = temp+" "+details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.hematuria) + " " + temp);


                // for perineum value
                str1 = "";
                str1 = perineum;

                str1 = str1.replaceAll("[^0-9]+", " ");
                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Perineum_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if(animal.length()>0)
                        temp = temp+" "+details[Integer.parseInt(animal)];
                }


                list.add("" + getString(R.string.perineum) + " " + temp);

                // for Family_Planning value
                str1 = "";
                str1 = FPMethod;
                str1 = str1.replaceAll("[^0-9]+", " ");
                animals = str1.split(" ");
                temp = "";
                details = res1.getStringArray(R.array.Family_Planning_Methods_DropDown);
                for (String animal : animals) {
                    System.out.println(animal);
                    if(animal.length()>0)
                        temp = temp+" "+details[Integer.parseInt(animal)];
                }

                list.add("" + getString(R.string.family_planning_methods) + " " + temp);
                list.add("" + getString(R.string.danger_signs) + " " + complicationsign);
                list.add("" + getString(R.string.disease) + " " + disease);
                list.add("" + getString(R.string.treatment) + " " + treatment);
                list.add("" + getString(R.string.advice) + " " + advice);
                if(Integer.parseInt(refer) == 1)
                    list.add("" + getString(R.string.refer) + " " + "Yes");
                else if(Integer.parseInt(refer) == 2)
                    list.add("" + getString(R.string.refer) + " " + "No");
                list.add("" + getString(R.string.referCenterName) + " " + referCenterName);
                list.add("" + getString(R.string.referReason) + " " + referReason);


                try {
                    // JSONArray jsonArray = jsonStr.getJSONArray(key);


                    listDataHeader = new ArrayList<String>();
                    listDataChild = new HashMap<String, List<String>>();


                    // listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");
                    listDataHeader.add("Visit " + in + ":");//jsonArray.get(0).toString()
                    listDataChild.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapterforPAC(this, listDataHeader, listDataChild);

                    in++;

                    initPage();

                    ll.addView(expListView);
                    expListView.setScrollingCacheEnabled(true);
                    expListView.setAdapter(listAdapter);
                    ll.invalidate();
                    // expListView.setAdapter(listAdapter);


                } catch (Exception e) {
                    Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                    e.printStackTrace();
                }
            }


        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }

    }

    void setItemVisible(int ItemId, boolean isChecked) {
        Spinner Item=(Spinner)findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

    protected void initiateCheckboxes(){};
    protected void initiateEditTexts(){
        jsonEditTextMap.put("temperature", getEditText(R.id.pacTemperatureValue));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pacBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pacBloodPresserValueDiastolic));
        jsonEditTextMap.put("hemoglobin", getEditText(R.id.pacHemoglobinValue));
    };
    protected void initiateTextViews(){};
    protected void initiateSpinners(){
        jsonSpinnerMap.put("anemia",getSpinner(R.id.pacAnemiaSpinner));
        jsonSpinnerMap.put("ancjaundice", getSpinner(R.id.ancJaundiceSpinner));
        jsonSpinnerMap.put("ancsugar", getSpinner(R.id.ancUrineSugarSpinner));
        jsonSpinnerMap.put("ancalbumin", getSpinner(R.id.ancUrineAlbuminSpinner));
    };
    protected void initiateMultiSelectionSpinners(){};
    protected void initiateEditTextDates(){};
    ;
    protected void initiateRadioGroups(){};
}
