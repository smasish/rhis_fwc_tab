package org.sci.rhis.fwc;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PACActivity extends ClinicalServiceActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MinimumDeliveryInfoFragment.DeliverySavedListener {

    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    AsyncPACInfoUpdate pacInfoUpdateTask;

    private int lastPacVisit = 0;
    private int pacSaveClick = 0;
    private String serviceProvider = "";
    private JSONObject jsonResponse = null;

    final private String SERVLET = "pac";
    final private String ROOTKEY = "PACInfo";

    private final String LOGTAG = "FWC-PAC";
    private MultiSelectionSpinner multiSelectionSpinner;

    private LinearLayout lay_pac;

    private Context con;
    private LinearLayout lay_frag_pac;
    private PregWoman mother;
    private ProviderInfo provider;

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

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //initiate multiselect spinners
        setMultiSelectSpinners();
        con = this;

        //lay_frag_pac = (LinearLayout)findViewById(R.id.historyFragmentLayout);


        //int name = intent.getIntExtra("PregWoman",0);
        //provider = getIntent().getParcelableExtra("Provider");

        Log.d("------>>>------------", "START-----PAC----" );
        ll = (LinearLayout)findViewById(R.id.llay);

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

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

        getEditText(R.id.pacServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.pacReferCheckBox).setOnCheckedChangeListener(this);

        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pacServiceDateValue));
        initialize();
        //mother = getIntent().getParcelableExtra("PregWoman");
        //
        Intent intent = getIntent();

        mother = intent.getParcelableExtra("PregWoman");
        provider = intent.getParcelableExtra("Provider");
        //pacvisit
        lastPacVisit=0;

        if (mother.getAbortionInfo() == 0) {
            showAbortionLayout();
        } else {
            getAbortionInformation();
        }
        //showHidePacDeleteButton();
    }

    private void getAbortionInformation() {
        AsyncPACInfoUpdate sendPostReqAsyncTask = new AsyncPACInfoUpdate(PACActivity.this);

        String queryString =   "{" +
                "pregNo:" + mother.getPregNo() + "," +
                "healthId:" + mother.getHealthId() + "," +
                "pacLoad:" + "retrieve" +
                "}";

        String servlet = "pac";
        String jsonRootkey = "PACInfo";
        Log.d(LOGTAG, "Mother Part:\n" + queryString);
        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootkey);
    }

    private void showAbortionLayout() {
        //Disable PAC and History Layout first
        // Utilities.Disable(this, R.id.pacEntryMasterLayout);
        Utilities.MakeInvisible(this, R.id.historyFragmentLayout);
        Utilities.MakeVisible(this, R.id.idPacAbortionInfo);
        Utilities.Disable(this, R.id.pacEntryMasterLayout);
    }

    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
    }

    private void setMultiSelectSpinners() {
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PACProblemDropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});

        final List<String> pncmhematurialist = Arrays.asList(getResources().getStringArray(R.array.PACHematuriaDropdown));
        final List<String> dangersignlist = Arrays.asList(getResources().getStringArray(R.array.ANC_Danger_Sign_DropDown));
        final List<String> diseaselist = Arrays.asList(getResources().getStringArray(R.array.ANC_Disease_DropDown));
        final List<String> treatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> advicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Advice_DropDown));
        final List<String> referreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Refer_Reason_DropDown));
        final List<String> pacabdomenlist = Arrays.asList(getResources().getStringArray(R.array.PACAbdomenDropdown));
        final List<String> paccervixlist = Arrays.asList(getResources().getStringArray(R.array.PACcervixDropDown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacDangerSignsSpinner);
        if (multiSelectionSpinner == null) {
            Log.d("------" + dangersignlist.get(1), ".........");
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

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacAbdomenSpinner);
        multiSelectionSpinner.setItems(pacabdomenlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacHematuriaSpinner);
        multiSelectionSpinner.setItems(pncmhematurialist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pacCervixSpinner);
        multiSelectionSpinner.setItems(paccervixlist);
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

        try {
            jsonResponse = new JSONObject(result);
            String key;
            Log.d(LOGTAG, "here:" + jsonResponse.toString());
            lastPacVisit = jsonResponse.getInt("count");
            if(lastPacVisit>0) {
                serviceProvider = jsonResponse.getJSONObject(String.valueOf(lastPacVisit)).getString("providerId");
                showHidePacDeleteButton();
            }
            getTextView(R.id.pacVisitValue).setText(String.valueOf(lastPacVisit + 1)); //next visit




            Log.d(LOGTAG, "PAC Response Received:\n\t" + result);
            //handleExistingChild(result);

            //JSONObject json = new JSONObject(result);
            Utilities.setTextViews(jsonTextViewsMap, jsonResponse);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "PAC Error:\n\t\t" + result);
            Utilities.printTrace(jse.getStackTrace());
        }
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
            //Resources res = getResources();
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
                String abdomen = jsonRootObject.getString("abdomen");
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

                list.add("" + getString(R.string.abdomen) + " " + abdomen);


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
        Spinner Item = (Spinner) findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

    protected void initiateCheckboxes() {
        jsonCheckboxMap.put("refer", getCheckbox(R.id.pacReferCheckBox));
    }

    protected void initiateEditTexts() {
        jsonEditTextMap.put("temperature", getEditText(R.id.pacTemperatureValue));
        jsonEditTextMap.put("bpSystolic", getEditText(R.id.pacBloodPresserValueSystolic));
        jsonEditTextMap.put("bpDiastolic", getEditText(R.id.pacBloodPresserValueDiastolic));
        jsonEditTextMap.put("hemoglobin", getEditText(R.id.pacHemoglobinValue));
    }
    protected void initiateTextViews(){
        jsonTextViewsMap.put("outcomeDate", getTextView(R.id.pacDate));
        jsonTextViewsMap.put("outcomePlace", getTextView(R.id.pacPlace));
    };
    protected void initiateSpinners() {
        jsonSpinnerMap.put("anemia", getSpinner(R.id.pacAnemiaSpinner));
        jsonSpinnerMap.put("uterusInvolution", getSpinner(R.id.pacUterusHeightSpinner));
        jsonSpinnerMap.put("perineum", getSpinner(R.id.pacPerineumSpinner));
        jsonSpinnerMap.put("FPMethod", getSpinner(R.id.pacFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.pacReferCenterNameSpinner));
        jsonSpinnerMap.put("serviceSource", getSpinner(R.id.pacOtherCenterNameSpinner));

    }

    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("symptom", getMultiSelectionSpinner(R.id.pacDrawbackSpinner));
        jsonMultiSpinnerMap.put("complicationSign", getMultiSelectionSpinner(R.id.pacDangerSignsSpinner));
        jsonMultiSpinnerMap.put("disease", getMultiSelectionSpinner(R.id.pacDiseaseSpinner));
        jsonMultiSpinnerMap.put("treatment", getMultiSelectionSpinner(R.id.pacTreatmentSpinner));
        jsonMultiSpinnerMap.put("advice", getMultiSelectionSpinner(R.id.pacAdviceSpinner));
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.pacReasonSpinner));
        jsonMultiSpinnerMap.put("cervix", getMultiSelectionSpinner(R.id.pacCervixSpinner));
        jsonMultiSpinnerMap.put("hematuria", getMultiSelectionSpinner(R.id.pacHematuriaSpinner));
        jsonMultiSpinnerMap.put("abdomen", getMultiSelectionSpinner(R.id.pacAbdomenSpinner));
    }

    protected void initiateEditTextDates() {
        jsonEditTextDateMap.put("visitDate", getEditText(R.id.pacServiceDateValue));
    }

    protected void initiateRadioGroups() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pacServiceDateValue ||
                v.getId() == R.id.Date_Picker_Button) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }

        if (v.getId() == R.id.pacSaveButton) {
            pacSaveToJson();
        }
    }

    //Callback method of delivery Saved even notification
    @Override
    public void onDeliverySaved(String result) {
        Log.d(LOGTAG, "Abortion Info saved response:\n\t\t" + result);
        Utilities.MakeInvisible(this, R.id.idMinDeliveryFragmentHolder);
        Utilities.MakeVisible(this, R.id.pacEntryMasterLayout);
        Utilities.MakeVisible(this,R.id.historyFragmentLayout);
        Utilities.Enable(this, R.id.historyFragmentLayout);
        Utilities.Enable(this, R.id.pacEntryMasterLayout);
    }

    private Activity getActivity () {
        return this;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.pacOtherCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;

            if(!isChecked)
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);
            else
                getSpinner(R.id.pacOtherCenterNameSpinner).setSelection(0);

            getSpinner(R.id.pacOtherCenterNameSpinner).setVisibility(visibility);

        }

        if (buttonView.getId() == R.id.pacReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pacReferCenterNameLayout, R.id.pacReasonLayout};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    private void pacSaveToJson() {
        pacInfoUpdateTask = new AsyncPACInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);

            pacInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);

            System.out.print("PAC Save Json in Servlet:" + ROOTKEY + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pacText);

        } catch (JSONException jse) {
            Log.e("PAC JSON Exception: ", jse.getMessage());
        }

    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString = "{" +
                "healthId:" + mother.getHealthId() + "," +
                (isRetrieval ? "" : "providerId:\"" + String.valueOf(provider.getProviderCode()) + "\",") +
                "pregNo:" + mother.getPregNo() + "," +
                "pacLoad:" + (isRetrieval ? "retrieve" : "\"\"") +
                "}";

        return new JSONObject(queryString);
    }

    private void showHidePacDeleteButton() {
        //serviceProvider=jsonResponse.getString("providerId");
        Log.d("provider Id's", provider.getProviderCode().toString() + " & " + serviceProvider);
        Utilities.SetVisibility(this, R.id.deleteLastPacButton, ((lastPacVisit > 0) && provider.getProviderCode().equals(serviceProvider)) ? View.VISIBLE : View.GONE);
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("pacLoad", "delete");

            pacInfoUpdateTask = new AsyncPACInfoUpdate(this);
            pacInfoUpdateTask.execute(deleteJson.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastPAC(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(PACActivity.this).create();
        alertDialog.setIcon(android.R.drawable.ic_delete);
        alertDialog.setTitle("Delete Service?");
        alertDialog.setMessage(getString(R.string.ServiceDeletionWarning));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteConfirmed();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(PACActivity.this).create();
        alertDialog.setTitle("EXIT CONFIRMATION");
        alertDialog.setMessage("আপনি কি গর্ভপাত পরবর্তী সেবা( PAC ) থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        //alertDialog.s

        alertDialog.show();
        //finish();
    }
}
