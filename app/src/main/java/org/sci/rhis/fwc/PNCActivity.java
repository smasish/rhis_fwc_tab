package org.sci.rhis.fwc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                     View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener {
    private MultiSelectionSpinner multiSelectionSpinner;



    // For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    ExpandableListAdapterforPNC listAdapter;
    ExpandableListAdapterforPNC_Child listAdapter_child;
    ExpandableListView expListView;
    ExpandableListView expListView_child;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    LinearLayout ll,ll_pnc_child;
    private int selected_child=1;
    private String child_result="";

    private PregWoman mother;
    private ProviderInfo provider;
    AsyncPNCInfoUpdate pncInfoUpdateTask;

    final private String SERVLET_MOTHER = "pncmother";
    final private String ROOTKEY_MOTHER = "PNCMotherInfo";
    final private String SERVLET_CHILD = "pncchild";
    final private String ROOTKEY_CHILD = "PNCChildInfo";

    ArrayList<HashMap<String, String>> contactList;
    JSONArray contacts = null;

    private View mPNCLayout;
    Boolean flag=false,mother_flag=false,child_flag=false,child_tree=true;

    private Button pnc_mother;//,pnc_child ; //,expand;
    private LinearLayout pnclay_child,pnclay_mother,lay_frag_child;

    private LinearLayout lay_frag_mother;
    private Context con;

    private ArrayAdapter<String> childAdapter;
    private ArrayList<String> childList;

    private int lastPncVisit = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnc);

      // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        con = this;

        mPNCLayout = findViewById(R.id.pncScroll);
        // Find our buttons
        Button visibleButton = (Button) findViewById(R.id.pncLabel);

        View.OnClickListener mVisibleListener = new View.OnClickListener() {
            public void onClick(View v) {
                if(flag==false) {
                    mPNCLayout.setVisibility(View.VISIBLE);
                    flag=true;
                }
                else
                {
                    mPNCLayout.setVisibility(View.INVISIBLE);
                    flag=false;
                }
            }
        };

        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        //pncvisit
        lastPncVisit = 0;

        pnc_mother = (Button)findViewById(R.id.pncmother);
      //  pnc_child = (Button)findViewById(R.id.pncchild);
      //  expand = (Button)findViewById(R.id.expandview);

        pnc_mother.setOnClickListener(this);
     //   pnc_child.setOnClickListener(this);
     //   expand.setOnClickListener(this);

        child_tree=true;
        childList  = new ArrayList<>(); //childList

        pnclay_child = (LinearLayout)findViewById(R.id.pncChildInfo);
        pnclay_mother = (LinearLayout)findViewById(R.id.pncMotherInfo);


        lay_frag_mother = (LinearLayout)findViewById(R.id.pnc_mother_frag);
        lay_frag_child = (LinearLayout)findViewById(R.id.pnc_child_frag);

        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int width  = mDisplay.getWidth();
        //int width=600;
        int height=100;
        //LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        //lay_frag_mother.setLayoutParams(parms);
        //lay_frag_mother.invalidate();

        lay_frag_child.setVisibility(View.GONE);
        pnclay_child.setVisibility(View.GONE);


        // Wire each button to a click listener
        visibleButton.setOnClickListener(mVisibleListener);
// Added By Al Amin
        initialize(); //super class
        Spinner spinners[] = new Spinner[6];
        spinners[0] = (Spinner) findViewById(R.id.pncBreastConditionSpinner);
        spinners[1] = (Spinner) findViewById(R.id.pncDischargeBleedingSpinner);
        spinners[2] = (Spinner) findViewById(R.id.pncPerineumSpinner);
        spinners[3] = (Spinner) findViewById(R.id.pncFamilyPlanningMethodsSpinner);
        spinners[4] = (Spinner) findViewById(R.id.pncReferCenterNameSpinner);
        spinners[5] = (Spinner) findViewById(R.id.pncAnemiaSpinner);
        for(int i = 0; i < spinners.length; ++i) {
          //  spinners[i].setOnItemSelectedListener(this);
        }

        // Multi Select Spinner Initialisation
        final List<String> pncmdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Danger_Sign_DropDown));
        final List<String> pnccdangersignlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Danger_Sign_DropDown));
        final List<String> pncmdrawbacklist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Drawback_DropDown));
        final List<String> pnccdrawbackblist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Drawback_DropDown));
        final List<String> pncmdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Disease_DropDown));
        final List<String> pnccdiseaselist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Disease_DropDown));
        final List<String> pncmtreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncctreatmentlist = Arrays.asList(getResources().getStringArray(R.array.Treatment_DropDown));
        final List<String> pncmadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Advice_DropDown));
        final List<String> pnccadvicelist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Advice_DropDown));
        final List<String> pncmreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Mother_Refer_Reason_DropDown));
        final List<String> pnccreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.PNC_Child_Refer_Reason_DropDown));

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDangerSignsSpinner);
        multiSelectionSpinner.setItems(pncmdangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDangerSignsSpinner);
        multiSelectionSpinner.setItems(pnccdangersignlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDrawbackSpinner);
        multiSelectionSpinner.setItems(pncmdrawbacklist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDrawbackSpinner);
        multiSelectionSpinner.setItems(pnccdrawbackblist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncDiseaseSpinner);
        multiSelectionSpinner.setItems(pncmdiseaselist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildDiseaseSpinner);
        multiSelectionSpinner.setItems(pnccdiseaselist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncTreatmentSpinner);
        multiSelectionSpinner.setItems(pncmtreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildTreatmentSpinner);
        multiSelectionSpinner.setItems(pncctreatmentlist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncAdviceSpinner);
        multiSelectionSpinner.setItems(pncmadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildAdviceSpinner);
        multiSelectionSpinner.setItems(pnccadvicelist);
        multiSelectionSpinner.setSelection(new int[]{});

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncReasonSpinner);
        multiSelectionSpinner.setItems(pncmreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.pncChildReasonSpinner);
        multiSelectionSpinner.setItems(pnccreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});



        ll = (LinearLayout)findViewById(R.id.llay);
        ll_pnc_child = (LinearLayout)findViewById(R.id.llay_frag);



        contactList = new ArrayList<HashMap<String, String>>();

        expListView = new ExpandableListView(this);
        ll.addView(expListView);

        //SendPostRequestAsyncTask
        AsyncPNCInfoUpdate sendPostReqAsyncTask = new AsyncPNCInfoUpdate(PNCActivity.this);

        String queryString =   "{" +
                "pregno:" + mother.getPregNo() + "," +
                "healthid:" + mother.getHealthId() + "," +
                "pncMLoad:" + "retrieve" +
                "}";

        String servlet = "pncmother";
        String jsonRootkey = "PNCMotherInfo";
        Log.d("PNC", "Mother Part:\n" + queryString);
        sendPostReqAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString, servlet, jsonRootkey);

        AsyncLoginTask sendPostReqAsyncTaskChild = new AsyncLoginTask(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                child_result = result;

                try {
                    JSONObject jsonStr = new JSONObject(result);

                    //int count = 0;
                    // start handle child drop down
                    populateChild(jsonStr);
                }catch (JSONException jse) {
                        System.out.println("JSON Exception Thrown::\n ");
                        jse.printStackTrace();
                    }
                //handleChild(result);

            }
        });
        //LongOperation sendPostReqAsyncTask_child = new LongOperation();

        String queryString_child = "{" +
                "pregno:" + mother.getPregNo() + "," +
                "healthid:" + mother.getHealthId() + "," +
                "pncCLoad:" + "retrieve" +
                "}";

        String servlet_child = "pncchild";
        String jsonRootkey_child = "PNCChildInfo";
        Log.d("PNC", "Child Part:\n" +  queryString_child);
        sendPostReqAsyncTaskChild.execute(queryString_child, servlet_child, jsonRootkey_child);

        expListView_child = new ExpandableListView(this);
        ll_pnc_child.addView(expListView_child);


/*

pnc child history
 */

        Log.d("-->", "---=====>" + queryString);

        getEditText(R.id.pncServiceDateValue).setOnClickListener(this);
        getEditText(R.id.pncChildServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.pncReferCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncChildReferCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncOthersCheckBox).setOnCheckedChangeListener(this);
        getCheckbox(R.id.pncChildOthersCheckBox).setOnCheckedChangeListener(this);

//custom date picker Added By Al Amin
        datePickerDialog = new CustomDatePickerDialog(this);
        datePickerPair = new HashMap<Integer, EditText>();
        datePickerPair.put(R.id.Date_Picker_Button, (EditText) findViewById(R.id.pncServiceDateValue));
        datePickerPair.put(R.id.Date_Picker_Button_Child, (EditText) findViewById(R.id.pncChildServiceDateValue));
    }





    public void pickDate(View view) {
        datePickerDialog.show(datePickerPair.get(view.getId()));
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


        Log.d("PNC", "Handle Mother:\n" + result);

            try {
                JSONObject jsonStr = new JSONObject(result);
                String key;

                lastPncVisit = jsonStr.getInt("count") + 1;
                getTextView(R.id.pncVisitValue).setText(String.valueOf(lastPncVisit));

                //Check if eligible for new PNC
                if(jsonStr.has("pncStatus") &&
                   jsonStr.getBoolean("pncStatus")) {
                    Utilities.MakeInvisible(this, R.id.pncMotherInfo);
                    Toast.makeText(this, "Mother is not eligible for new ANC",Toast.LENGTH_LONG).show();
                }
                //

                int in=1;

                // woman = PregWoman.CreatePregWoman(json);
                Log.d("--:::>", "---complicationsign=====>" + result);
                //DEBUG
                Resources res = getResources();
                // String[] mainlist = res.getStringArray(R.array.list_item);
                Log.d("-->", "---=jsonStr.keys()====>" + jsonStr.keys());
                for (Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                    key = ii.next();


                    System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key) + "\'");


                    JSONObject jsonRootObject = jsonStr.getJSONObject(""+in);
                    Log.d("--:::>", "---serviceSource=====>" + jsonRootObject.getString("serviceSource"));

                    String complicationsign = jsonRootObject.getString("complicationsign");
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
                    String edema = jsonRootObject.getString("edema");
                    String serviceID = jsonRootObject.getString("serviceID");
                    String hemoglobin = jsonRootObject.getString("hemoglobin");
                    String FPMethod = jsonRootObject.getString("FPMethod");
                    String breastCondition = jsonRootObject.getString("breastCondition");
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
                    details = res1.getStringArray(R.array.pnc_Anemia_Dropdown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
                    }
                    list.add("" + getString(R.string.anemia) + " " + temp);


                    list.add("" + getString(R.string.hemoglobin) + " " + hemoglobin+"%");


                    // for edema value
                    str1 = "";
                    str1 = edema;

                     animals = str1.split(" ");
                   temp = "";
                    details = res1.getStringArray(R.array.Edema_Dropdown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
                    }
                    list.add("" + getString(R.string.edema) + " " + temp);

                    // for breastCondition value
                    str1 = "";
                    str1 = breastCondition;

                     animals = str1.split(" ");
                     temp = "";
                    details = res1.getStringArray(R.array.Breast_Condition_DropDown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
                    }

                    list.add("" + getString(R.string.breastCondition) + " " + temp);

                    // for hematuria value
                    str1 = "";
                    str1 = uterusInvolution;

                     animals = str1.split(" ");
                     temp = "";
                    details = res1.getStringArray(R.array.Cervix_Involution_DropDown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
                    }

                    list.add("" + getString(R.string.uterusInvolution) + " " + temp);


                    // for hematuria value
                    str1 = "";
                    str1 = hematuria;

                     animals = str1.split(" ");
                     temp = "";
                    details = res1.getStringArray(R.array.Discharge_Bleeding_DropDown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
                    }

                    list.add("" + getString(R.string.hematuria) + " " + temp);


                       // for perineum value
                         str1 = "";
                         str1 = perineum;


                        animals = str1.split(" ");
                        temp = "";
                        details = res1.getStringArray(R.array.Perineum_DropDown);
                        for (String animal : animals) {
                            System.out.println(animal);
                            if(animal.length()>0)
                                temp = temp+"\n"+details[Integer.parseInt(animal)];
                        }


                    list.add("" + getString(R.string.perineum) + " " + temp);

                    // for Family_Planning value
                    str1 = "";
                    str1 = FPMethod;

                     animals = str1.split(" ");
                     temp = "";
                    details = res1.getStringArray(R.array.Family_Planning_Methods_DropDown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+"\n"+details[Integer.parseInt(animal)];
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

                        listAdapter = new ExpandableListAdapterforPNC(this, listDataHeader, listDataChild);

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

       // }
    }

    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
        //  expListView.smoothScrollToPosition(expListView.getCount() - 1);
    }
    private void initPage_child() {
        expListView_child = new ExpandableListView(this);
        expListView_child.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView_child.setIndicatorBounds(0, 0);
        expListView_child.setChildIndicatorBounds(0, 0);
        expListView_child.setStackFromBottom(true);
        //  expListView.smoothScrollToPosition(expListView.getCount() - 1);
    }


    private void pncMotherSaveToJson() {
        pncInfoUpdateTask = new AsyncPNCInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeaderMother(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);

           pncInfoUpdateTask.execute(json.toString(), SERVLET_MOTHER, ROOTKEY_MOTHER);

            System.out.print("PNCM Save Json in Servlet:" + ROOTKEY_MOTHER + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pncMotherInfo);

        } catch (JSONException jse) {
            Log.e("PNCM JSON Exception: ", jse.getMessage());
        }

    }
    private JSONObject buildQueryHeaderMother(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + mother.getPregNo() + "," +
                "pncMLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        return new JSONObject(queryString);
    }

    private void pncChildSaveToJson(){

        pncInfoUpdateTask = new AsyncPNCInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleChild(result);
            }
        });
        JSONObject json;
        try {
            json = buildQueryHeaderChild(false);
            Utilities.getCheckboxes(jsonCheckboxMapChild, json);
            Utilities.getEditTexts(jsonEditTextMapChild, json);
            Utilities.getEditTextDates(jsonEditTextDateMapChild, json);
            Utilities.getSpinners(jsonSpinnerMapChild, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMapChild, json);

            pncInfoUpdateTask.execute(json.toString(), SERVLET_CHILD, ROOTKEY_CHILD);

            System.out.print("PNCC Save Json in Servlet:" + ROOTKEY_MOTHER + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pncChildInfo);


        } catch (JSONException jse) {
            Log.e("PNCC JSON Exception: ", jse.getMessage());
        }



    }

    private JSONObject buildQueryHeaderChild(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                "providerid:"+String.valueOf(provider.getProviderCode())+","+
                "pregno:" + mother.getPregNo() + "," +
                "pncCLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";
        return new JSONObject(queryString);
    }


    public void savePnc(View view) {
            pncMotherSaveToJson();
             Toast.makeText(this, "Save Button onClick performed", Toast.LENGTH_LONG).show();

    }

    public void savePNCChild (View view){
        pncChildSaveToJson();
        Toast.makeText(this, "Save Button onClick performed", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void initiateCheckboxes(){
        jsonCheckboxMap.put("pncrefer", getCheckbox(R.id.pncReferCheckBox));
        jsonCheckboxMap.put("pncservicesource", getCheckbox(R.id.pncOthersCheckBox));

       // for Child
        jsonCheckboxMapChild.put("pncbreastfeedingonly", getCheckbox(R.id.pncChildOnlyBreastFeedingCheckBox));
        jsonCheckboxMapChild.put("pncrefer", getCheckbox(R.id.pncChildReferCheckBox));
        jsonCheckboxMapChild.put("pncservicesource", getCheckbox(R.id.pncChildOthersCheckBox));
    };

    @Override
    protected void initiateRadioGroups(){};


    @Override
    protected void initiateSpinners() {
        // PNC Mother Info
        jsonSpinnerMap.put("pncservicesource", getSpinner(R.id.pncServiceOthersSpinner));
        jsonSpinnerMap.put("pncanemia", getSpinner(R.id.pncAnemiaSpinner));
        jsonSpinnerMap.put("pncedema", getSpinner(R.id.pncEdemaSpinner));
        jsonSpinnerMap.put("pncbreastcondition", getSpinner(R.id.pncBreastConditionSpinner));
        jsonSpinnerMap.put("pncuterusinvolution", getSpinner(R.id.pncCervixInvolutionSpinner));
        jsonSpinnerMap.put("pnchematuria", getSpinner(R.id.pncDischargeBleedingSpinner));
        jsonSpinnerMap.put("pncperineum", getSpinner(R.id.pncPerineumSpinner));
        jsonSpinnerMap.put("pncfpmethod", getSpinner(R.id.pncFamilyPlanningMethodsSpinner));
        jsonSpinnerMap.put("pncrefercentername", getSpinner(R.id.pncReferCenterNameSpinner));

        // PNC Child Info
        jsonSpinnerMapChild.put("pncservicesource", getSpinner(R.id.pncChildServiceOthersSpinner));
        jsonSpinnerMapChild.put("pncrefercentername", getSpinner(R.id.pncChildReferCenterNameSpinner));;

    }
    @Override
    protected void initiateMultiSelectionSpinners(){
       // for mother
        jsonMultiSpinnerMap.put("pncsymptom",  getMultiSelectionSpinner(R.id.pncDrawbackSpinner));
        jsonMultiSpinnerMap.put("pnccomplicationsign",  getMultiSelectionSpinner(R.id.pncDangerSignsSpinner));
        jsonMultiSpinnerMap.put("pncdisease",  getMultiSelectionSpinner(R.id.pncDiseaseSpinner));
        jsonMultiSpinnerMap.put("pnctreatment",  getMultiSelectionSpinner(R.id.pncTreatmentSpinner));
        jsonMultiSpinnerMap.put("pncadvice",  getMultiSelectionSpinner(R.id.pncAdviceSpinner));
        jsonMultiSpinnerMap.put("pncreferreason",  getMultiSelectionSpinner(R.id.pncReasonSpinner));


        // for Child
        jsonMultiSpinnerMapChild.put("pncsymptom", getMultiSelectionSpinner(R.id.pncChildDrawbackSpinner));
        jsonMultiSpinnerMapChild.put("pncdangersign", getMultiSelectionSpinner(R.id.pncChildDangerSignsSpinner));
        jsonMultiSpinnerMapChild.put("pncdisease", getMultiSelectionSpinner(R.id.pncChildDiseaseSpinner));
        jsonMultiSpinnerMapChild.put("pnctreatment", getMultiSelectionSpinner(R.id.pncChildTreatmentSpinner));
        jsonMultiSpinnerMapChild.put("pncadvice", getMultiSelectionSpinner(R.id.pncChildAdviceSpinner));
        jsonMultiSpinnerMapChild.put("pncreferreason",  getMultiSelectionSpinner(R.id.pncChildReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //PNC Mother visit
       // jsonEditTextMap.put("serviceId", getEditText(R.id.pncVisitValue));
        jsonEditTextMap.put("pnctemperature", getEditText(R.id.pncTemperatureValue));
        jsonEditTextMap.put("pncbpsys", getEditText(R.id.pncBloodPresserValue));
        jsonEditTextMap.put("pncbpdias",getEditText(R.id.pncBloodPresserValueD));
        jsonEditTextMap.put("pnchemoglobin",getEditText(R.id.pncHemoglobinValue));


        //PNC Child visit
        jsonEditTextMapChild.put("pncchildno", getEditText(R.id.pncNewBornNumber));
        jsonEditTextMapChild.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMapChild.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMapChild.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));

    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateEditTextDates() {
        // PNC Mother Service Date
        jsonEditTextDateMap.put("pncdate", getEditText(R.id.pncServiceDateValue));
        // PNC Child Service Date
        jsonEditTextDateMapChild.put("pncdate", getEditText(R.id.pncChildServiceDateValue));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.pncOthersCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;

            if(!isChecked)
            getSpinner(R.id.pncServiceOthersSpinner).setSelection(5);
            else
            getSpinner(R.id.pncServiceOthersSpinner).setSelection(0);

            getSpinner(R.id.pncServiceOthersSpinner).setVisibility(visibility);

        }

        if (buttonView.getId() == R.id.pncChildOthersCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;

            if(!isChecked)
                getSpinner(R.id.pncChildServiceOthersSpinner).setSelection(5);
            else
                getSpinner(R.id.pncChildServiceOthersSpinner).setSelection(0);

            getSpinner(R.id.pncChildServiceOthersSpinner).setVisibility(visibility);

        }

        if (buttonView.getId() == R.id.pncReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pncReferCenterName, R.id.pncReason};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }

        if (buttonView.getId() == R.id.pncChildReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.GONE;
            int layouts[] = {R.id.pncChildReferCenterName, R.id.pncChildReason};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    private void handleChild(String result) {
        try {
            JSONObject jsonStr = new JSONObject(result);
            String key;
            //int num=1;

            int count = 0;
            String child="";
            // start handle child drop down
           // populateChild(jsonStr);
            // end child drop down

            Log.d("PNC", "Handle Child\n" + result);
            // woman = PregWoman.CreatePregWoman(json);
              //DEBUG
            Resources res = getResources();
            // String[] mainlist = res.getStringArray(R.array.list_item);

            for (Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                key = ii.next();

                Log.d("-->", "---=keys()====>" + key);
                System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key) + "\'");
               child = ""+selected_child;
               if(key.equalsIgnoreCase(child)) {
                   if (key.equalsIgnoreCase("childCount") || key.equalsIgnoreCase("outcomeDate") ||
                           key.equalsIgnoreCase("hasDeliveryInformation") || key.equalsIgnoreCase("pncStatus")) {

                   } else {


                       JSONObject jsonObject1 = jsonStr.getJSONObject("" + selected_child);

                       Log.d("--:::>", "---complicationsign=====>");
                       count = 0;

                       int in = 1,k;
                       k = jsonObject1.length();
                      // Log.d("--:::>", "---length=====>"+k);
                       for (Iterator<String> iii = jsonObject1.keys(); iii.hasNext(); ) {

                          // key = iii.next();


                           Log.d("--:::>", "---key 1 key=====>" + key);
                          // System.out.println("11.Key:" + key + " 11Value:\'" + jsonObject1.get(key) + "\'");

                           if (key.equalsIgnoreCase("pncStatus")) {

                           } else if (key.equalsIgnoreCase("serviceCount")) {
                               count = Integer.parseInt(jsonObject1.get(key).toString());
                               getEditText(R.id.pncChildVisitValue).setText(String.valueOf(count +1));
                               Utilities.Disable(this, R.id.pncChildVisitValue );
                           } else {
                               //JSONObject jsonObject = jsonObject1.getJSONObject(key);
                               //JSONObject jsonObject = jsonObject2.getJSONObject(key);

                               if((in - 1) == Integer.parseInt(key)) {
                                   //count = Integer.parseInt(jsonObject1.get(key).toString());
                                   getEditText(R.id.pncChildVisitValue).setText(""+in);
                                   Utilities.Disable(this, R.id.pncChildVisitValue );
                                   break;
                               }
                               key = ""+in;
                               JSONObject jsonObject = jsonObject1.getJSONObject("" + key);


                               Log.d("--====>" , "---serviceSource child=====>" + jsonObject.toString());

                               //String complicationsign = jsonRootObject.getString("serviceSource");
                               //String complicationsign = jsonObject.getString("complicationsign");
                               String visitDate = jsonObject.getString("visitDate");
                               // String serviceCount = jsonObject.getString("serviceCount");
                               String symptom = jsonObject.getString("symptom");
                               String weight = jsonObject.getString("weight");
                               String referCenterName = jsonObject.getString("referCenterName");
                               String childNo = jsonObject.getString("childNo");
                               String treatment = jsonObject.getString("treatment");
                               String breastFeedingOnly = jsonObject.getString("breastFeedingOnly");

                               String breathingPerMinute = jsonObject.getString("breathingPerMinute");
                               String disease = jsonObject.getString("disease");
                               String dangerSign = jsonObject.getString("dangerSign");
//                    String hematuria = jsonRootObject.getString("hematuria");
                               String temperature = jsonObject.getString("temperature");
//                    String referReason = jsonRootObject.getString("referReason");
                               String advice = jsonObject.getString("advice");
                               String refer = jsonObject.getString("refer");
                               String referReason = jsonObject.getString("referReason");

                               ArrayList<String> list = new ArrayList<String>();
                               list.add("" + getString(R.string.visitDate) + " " + visitDate);
                               list.add("" + getString(R.string.complicationsign) + " " + symptom);
                               list.add("" + getString(R.string.temperature) + " " + temperature);
                               list.add("" + getString(R.string.weight) + " " + weight);
                               list.add("" + getString(R.string.breath_per_minute) + " " + breathingPerMinute);
                               list.add("" + getString(R.string.danger_signs) + " " + dangerSign);

                               list.add("" + getString(R.string.breast_feeding) + " " + breastFeedingOnly);

                               list.add("" + getString(R.string.disease) + " " + disease);
                               list.add("" + getString(R.string.treatment) + " " + treatment);
                               list.add("" + getString(R.string.advice) + " " + advice);
                               list.add("" + getString(R.string.refer) + " " + refer);
                               list.add("" + getString(R.string.referCenterName) + " " + referCenterName);
                               list.add("" + getString(R.string.referReason) + " " + referReason);


                               try {
                                   // JSONArray jsonArray = jsonStr.getJSONArray(key);


                                   listDataHeader = new ArrayList<String>();
                                   listDataChild = new HashMap<String, List<String>>();


                                   // listDataHeader.add(getString(R.string.history_visit1) + "" + jsonArray.get(0).toString() + " :");
                                   listDataHeader.add("Visit " + key + ":");//jsonArray.get(0).toString()
                                   listDataChild.put(listDataHeader.get(0), list);

                                   listAdapter_child = new ExpandableListAdapterforPNC_Child(PNCActivity.this, listDataHeader, listDataChild);

//                                if(serviceCount.length()==1) {
//                                    count = Integer.parseInt(serviceCount);
//
//
//                                }
//                                else
                                   //count = 0;
                                   Log.d("...........>", "=....count=>" + count);
                                   //num++;
                                   initPage_child();
                                   //ll_pnc_child = (LinearLayout)findViewById(R.id.llay_frag);

                                   //if (count >= 1) {
                                       ll_pnc_child.addView(expListView_child);
                                       expListView_child.setScrollingCacheEnabled(true);
                                       expListView_child.setAdapter(listAdapter_child);
                                       ll_pnc_child.invalidate();
                                       count = 0;
                                  // }
                                   //expListView.setAdapter(listAdapter_child);

                                   in++;
                               } catch (Exception e) {
                                   Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                                   e.printStackTrace();
                               }
                           }
                       }
                   }
               }
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pncServiceDateValue || v.getId() == R.id.Date_Picker_Button || v.getId() == R.id.Date_Picker_Button_Child) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }



        if(v.getId() == R.id.pncmother){
            //pnclay_mother.setVisibility(View.GONE);
           // lay_frag_mother.setVisibility(View.GONE);
            lay_frag_child.setVisibility(View.GONE);
            pnclay_child.setVisibility(View.GONE);


            if(mother_flag==false) {
                lay_frag_mother.setVisibility(View.VISIBLE);
                pnclay_mother.setVisibility(View.VISIBLE);
               // mother_flag = true;
            }
            else
            {
              //  lay_frag_mother.setVisibility(View.GONE);
             //   pnclay_mother.setVisibility(View.GONE);
                mother_flag = false;
            }
        }
//        else if(v.getId() == R.id.expandview){
//            //int width=600;
//            int height=300;
//            Display mDisplay = this.getWindowManager().getDefaultDisplay();
//            final int width  = mDisplay.getWidth();
//            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
//            lay_frag_mother.setLayoutParams(parms);
//            lay_frag_mother.invalidate();
//           // Toast.makeText(con,"done",Toast.LENGTH_LONG).show();
//
//
//
//            //ll.invalidate();
//        }
//        else if(v.getId() == R.id.pncchild){
//            lay_frag_mother.setVisibility(View.GONE);
//            pnclay_mother.setVisibility(View.GONE);
//
//            if(child_flag==false) {
//                lay_frag_child.setVisibility(View.VISIBLE);
//                pnclay_child.setVisibility(View.VISIBLE);
//
//                ll_pnc_child.invalidate();
//                child_flag = true;
//            }
//            else
//            {
//                lay_frag_child.setVisibility(View.GONE);
//                pnclay_child.setVisibility(View.GONE);
//                child_flag = false;
//                ll_pnc_child.invalidate();
//            }

//            lay_frag_mother.setVisibility(View.GONE);
//            lay_frag_child.setVisibility(View.VISIBLE);
//            pnclay_child.setVisibility(View.VISIBLE);

//            if(child_tree) {
//                child_tree=false;
//                expListView = new ExpandableListView(this);
//                ll_pnc_child = (LinearLayout) findViewById(R.id.llay_frag);

                //ll_pnc_child.addView(expListView);
                //ll_pnc_child.invalidate();

//                AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(new AsyncCallback() {
//                    @Override
//                    public void callbackAsyncTask(String result) {
//                        handleChild(result);
//                    }
//                });
//                LongOperation sendPostReqAsyncTask_child = new LongOperation();
//
//                String queryString_child = "{" +
//                        "pregno:" + mother.getPregNo() + "," +
//                        "healthid:" + mother.getHealthId() + "," +
//                        "pncCLoad:" + "retrieve" +
//                        "}";
//
//                String servlet_child = "pncchild";
//                String jsonRootkey_child = "PNCChildInfo";
//                Log.d("-->", "---=====>" + queryString_child);
//                sendPostReqAsyncTask_child.execute(queryString_child, servlet_child, jsonRootkey_child);

 //          }
 //       }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(con,"done"+position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void populateChild(JSONObject childJson)  {
        Spinner childDropdown = getSpinner(R.id.id_pncChildListDropdown);
        childList.clear();
        //childDropdown.setVisibility(View.VISIBLE);
        //childDropdown.setAdapter();

        int currentChildCount = 0;
        String childArray [] = {};

        childList.add("");

        try {
            String childValues = childJson.getString("childMapping");
            if(!childValues.equals("")) {
                childArray = childValues.split(",");
            }

            for (int i  = 0; i < childArray.length; i++) {
                childList.add(childArray[i]);
            }

        } catch (JSONException JSE) {
            JSE.printStackTrace();
        }

        childAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,childList);
        childAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childDropdown.setAdapter(childAdapter);
        if(childArray.length > 0) {
            childDropdown.setVisibility(View.VISIBLE);
            childDropdown.setSelection(0);
        }

        childDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position != 0) {
                        handleChildSelected(position);
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void handleChildSelected(int childno) {
        String child = childList.get(childno);
        selected_child = childno;
        ll_pnc_child.removeAllViews();
//        AsyncLoginTask sendPostReqAsyncTaskChild = new AsyncLoginTask(new AsyncCallback() {
//            @Override
//            public void callbackAsyncTask(String result) {
//                handleChild(result);
//            }
//        });
//
//        String queryString_child = "{" +
//                "pregno:" + mother.getPregNo() + "," +
//                "healthid:" + mother.getHealthId() + "," +
//                "pncCLoad:" + "retrieve" +
//                "}";
//
//        String servlet_child = "pncchild";
//        String jsonRootkey_child = "PNCChildInfo";
//        Log.d("PNC", "Child Part:\n" +  queryString_child);
//        sendPostReqAsyncTaskChild.execute(queryString_child, servlet_child, jsonRootkey_child);

        handleChild(child_result);
        Log.d("------------------"+childno,"-----------"+child);
        lay_frag_mother.setVisibility(View.GONE);
        pnclay_mother.setVisibility(View.GONE);

        if(child_flag==false) {
            lay_frag_child.setVisibility(View.VISIBLE);
            pnclay_child.setVisibility(View.VISIBLE);

            ll_pnc_child.invalidate();
            //child_flag = true;
        }
        else
        {
           // lay_frag_child.setVisibility(View.GONE);
          //  pnclay_child.setVisibility(View.GONE);
            //child_flag = false;
            ll_pnc_child.invalidate();
        }


    }
}
