package org.sci.rhis.fwc;


import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ANCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private PregWoman woman;
    private Date today;

// For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ExpandableListAdapter listAdapter2;
    ExpandableListView expListView2;
    List<String> listDataHeader2;
    HashMap<String, List<String>> listDataChild2;

    ExpandableListAdapter listAdapter3;
    ExpandableListView expListView3;
    List<String> listDataHeader3;
    HashMap<String, List<String>> listDataChild3;

    private static final String TAG_VISIT_NO = "ancVisit01";
    private static final String TAG_DATE = "2015-07-02";
    private static final String TAG_BLOOD_PRESSURE = "120";
    private static final String TAG_WEIGHT = "22";
    private static final String TAG_EDIMA = "22";
    private static final String TAG_J_HEIGHT = "5.6";
    private static final String TAG_FITNESS_PM = "FIT";
    private static final String TAG_PHITAL_PRESENTATION = "GOOD";
    private static final String TAG_HIMOGLOBIN = "2";
    private static final String TAG_JONDIS = "3";
    private static final String TAG_URIN_SUGAR = "3";
    private static final String TAG_URIN_TEST = "3";
    private static final String TAG_DANGER_SIGN = "OFF";
    private static final String TAG_DISADVANTAGE = "NA";
    private static final String TAG_DISEASE = "FEVER";
    private static final String TAG_TREATMENT = "OK";
    private static final String TAG_REFER = "DR";
    private static final String TAG_CENTER_NAME = "DHAKA";
    private static final String TAG_CAUSE = "MAN";

    JSONArray visits = null;
    LinearLayout ll;


    private MultiSelectionSpinner multiSelectionSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // woman = getIntent().getParcelableExtra("PregWoman");


       // Log.d("---woman---------",""+woman.toString());
        today = new Date();
//        if( woman.getAncThreshold().after(today)) { // add ANC threshold
//            Toast.makeText(this, "Too Late for ANC, ask delivery status ...", Toast.LENGTH_LONG).show();
//        } else {
//            setContentView(R.layout.activity_anc);
//        }

        setContentView(R.layout.activity_anc);

//        GridView gv = (GridView)findViewById(R.id.gridAncVisit);
 //       gv.setAdapter(new CustomGridAdapter(ANCActivity.this));

        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Danger_Sign_DropDown));
        final List<String> drabackblist = Arrays.asList(getResources().getStringArray(R.array.ANC_Drawback_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.ANC_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancDangerSignsSpinner);
        if(multiSelectionSpinner == null){
            Log.d("------"+ dangersignlist.get(1),".........");
        }
        multiSelectionSpinner.setItems(dangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancDrawbackSpinner);
        multiSelectionSpinner.setItems(drabackblist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancDiseaseSpinner);
        multiSelectionSpinner.setItems(diseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancTreatmentSpinner);
        multiSelectionSpinner.setItems(treatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancAdviceSpinner);
        multiSelectionSpinner.setItems(advicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.ancReasonSpinner);
        multiSelectionSpinner.setItems(referreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});


        ll = (LinearLayout)findViewById(R.id.llay);
//        ll.setOrientation(LinearLayout.HORIZONTAL);
//        ll.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
//        // ARGB: Opaque Red
//        ll.setBackgroundColor(0x88ff0000);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);
        // get the listview
      //  expListView = (ExpandableListView) findViewById(R.id.lvExp);

    //    expListView2 = (ExpandableListView) findViewById(R.id.lvExp2);

     //   expListView3 = (ExpandableListView) findViewById(R.id.lvExp3);



        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(ANCActivity.this);
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(ANCActivity.this);
        String queryString =   "{" +
                "pregNo:" + 3 + "," +
                "healthid:" + "43366275025436" + "," +
                "ancLoad:" + ProviderInfo.getProvider().getProviderCode() +
                "}";
        String servlet = "anc";
        String jsonRootkey = "ANCInfo";
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);


        // preparing list data
//        prepareListData();
//
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//
//        prepareListData2();
//        listAdapter2 = new ExpandableListAdapter(this, listDataHeader2, listDataChild2);

    //    prepareListData3();
    //    listAdapter3 = new ExpandableListAdapter(this, listDataHeader3, listDataChild3);


        // setting list adapter
    //    expListView.setAdapter(listAdapter);
   //     expListView2.setAdapter(listAdapter2);
   //     expListView3.setAdapter(listAdapter3);

    // Initialize Spinner added By Al Amin
        initialize(); //super class
        Spinner spinners[] = new Spinner[6];
        spinners[0] = (Spinner) findViewById(R.id.ancEdemaSpinner);
        spinners[1] = (Spinner) findViewById(R.id.ancFetalPresentationSpinner);
        spinners[2] = (Spinner) findViewById(R.id.ancJaundiceSpinner);
        spinners[3] = (Spinner) findViewById(R.id.ancUrineSugarSpinner);
        spinners[4] = (Spinner) findViewById(R.id.ancUrineAlbuminSpinner);
        spinners[5] = (Spinner) findViewById(R.id.ancReferCenterNameSpinner);

        for(int i = 0; i < spinners.length; ++i) {
            spinners[i].setOnItemSelectedListener(this);
        }

      //custom date picker Added By Al Amin
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<Integer, EditText>();
       datePickerPair.put(R.id.Date_Picker_Button, (EditText)findViewById(R.id.ancServiceDateValue));

    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }


    /*
 * Preparing the list data
 */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        listDataHeader.add(getString(R.string.history_visit1));


       // listDataChild.put(listDataHeader.get(1), nowShowing);
       // listDataChild.put(listDataHeader.get(2), comingSoon);

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anc, menu);
        return true;
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

                        if(i == 2){

                        }else if(i==3){
                            list.add(""+mainlist[i]+""+jsonArray.get(i-1).toString()+" / "+jsonArray.get(i).toString());
                        }else
                            list.add(""+mainlist[i]+""+jsonArray.get(i).toString());


                    }//end for
                    listDataHeader = new ArrayList<String>();
                    listDataChild = new HashMap<String, List<String>>();


                  //  listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");
                    listDataHeader.add("Visit "+jsonArray.get(0).toString() + ":");
                    listDataChild.put(listDataHeader.get(0), list);

                    listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

                   // expListView = new ExpandableListView(this);
                   // expListView.setAdapter(listAdapter);


//                    expListView = new ExpandableListView(this);
//                    expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//
//                    expListView.setIndicatorBounds(0, 0);
//                    expListView.setChildIndicatorBounds(0, 0);
//                    expListView.setStackFromBottom(true);
//
//
//                    expListView.smoothScrollToPosition(expListView.getCount() - 1);

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
    // Added by Al Amin
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ancServiceDateValue || v.getId() == R.id.Date_Picker_Button) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
// Have to set condition for Check Box Yet Checked to visible Refer Center, Refer reason
        CheckBox checkBox = (CheckBox) findViewById(R.id.ancReferCheckBox);
        if (parent.getId()== R.id.ancReferCheckBox && checkBox.isChecked()) {
            {
                System.out.println("Hit the Refer center Spinner");
                LinearLayout[] section;
                section = new LinearLayout[2];
                section[0] = (LinearLayout) findViewById(R.id.ancReferCenterNameLayout);
                section[1] = (LinearLayout) findViewById(R.id.ancReasonLayout);

                for (int i = 0; i < section.length; ++i) {
                    section[i].setVisibility(position == 0 ? View.GONE : View.VISIBLE); //0 - home
                }

            }
        }
        else{
            System.out.println("The condition for Refer is Not working!");
        }


    }

    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("ancrefer", getCheckbox(R.id.ancReferCheckBox));
    };

    @Override
    protected void initiateRadioGroups(){};


    @Override
    protected void initiateSpinners() {
        jsonSpinnerMap.put("ancedema", getSpinner(R.id.ancEdemaSpinner));
        jsonSpinnerMap.put("ancfpresentation", getSpinner(R.id.ancFetalPresentationSpinner));
        jsonSpinnerMap.put("ancjaundice", getSpinner(R.id.ancJaundiceSpinner));
        jsonSpinnerMap.put("ancsugar", getSpinner(R.id.ancUrineSugarSpinner));
        jsonSpinnerMap.put("ancalbumin", getSpinner(R.id.ancUrineAlbuminSpinner));
        jsonSpinnerMap.put("anccomplication", getSpinner(R.id.ancDangerSignsSpinner));
        jsonSpinnerMap.put("ancsymptom", getSpinner(R.id.ancDrawbackSpinner));
        jsonSpinnerMap.put("ancdisease", getSpinner(R.id.ancDiseaseSpinner));
        jsonSpinnerMap.put("anctreatment", getSpinner(R.id.ancTreatmentSpinner));
        jsonSpinnerMap.put("ancadvice", getSpinner(R.id.ancAdviceSpinner));
        jsonSpinnerMap.put("anccentername", getSpinner(R.id.ancReferCenterNameSpinner));
        jsonSpinnerMap.put("ancreferreason", getSpinner(R.id.ancReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //anc visit
        jsonEditTextMap.put("pregNo", getEditText(R.id.ancVisitValue));
        jsonEditTextMap.put("ancbpsys", getEditText(R.id.ancBloodPresserValueSystolic));
        jsonEditTextMap.put("ancbpdias", getEditText(R.id.ancBloodPresserValueDiastolic));
        jsonEditTextMap.put("ancweight", getEditText(R.id.ancWeightValue));
        jsonEditTextMap.put("ancuheight", getEditText(R.id.ancUterusHeightValue));
        jsonEditTextMap.put("anchrate", getEditText(R.id.ancHeartSpeedValue));

        jsonEditTextMap.put("anchemoglobin",getEditText(R.id.ancHemoglobinValue));
       }

    @Override
    protected void initiateEditTextDates() {
        // ANC Service Date
        jsonEditTextDateMap.put("ancdate", getEditText(R.id.ancServiceDateValue));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
