package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PNCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                     View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener {

    final static int FIRST_PNC_1 = 0; //DAYS
    final static int FIRST_PNC_2 = 1; //DAYS
    final static int SECOND_PNC_1 = 2; //DAYS
    final static int SECOND_PNC_2 = 3; //DAYS
    final static int THIRD_PNC_1 = 7; //DAYS
    final static int THIRD_PNC_2 = 14; //DAYS
    final static int FOURTH_PNC_1 = 35; //DAYS
    final static int FOURTH_PNC_2 = 42; //DAYS

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
    private  final String LOGTAG       = "FWC-PNC";

    ArrayList<HashMap<String, String>> contactList;

    private View mPNCLayout;
    Boolean flag=false,mother_flag=false,child_flag=false,child_tree=true;

    private Button pnc_mother;//,pnc_child ; //,expand;
    private LinearLayout pnclay_child,pnclay_mother,lay_frag_child;

    private LinearLayout lay_frag_mother;
    private Context con;

    private ArrayAdapter<String> childAdapter;
    private ArrayList<String> childList;

    private int lastPncVisit = 0;
    private int lastPncVisitChild = 0;
    private int pncSaveClick = 0;

    private JSONObject jsonRespChild = null;
    private JSONObject jsonRespMother = null;

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

        //Radio button listener
        getRadioGroup(R.id.pncMotherChildSelector).setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        handleRadioButton(group, checkedId);
                    }
                });

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

        // Generic works common to all the clinical service sub classes
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
        Log.d(LOGTAG, "Mother Part:\n" + queryString);
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
        Log.d(LOGTAG, "Child Part:\n" + queryString_child);
        sendPostReqAsyncTaskChild.execute(queryString_child, servlet_child, jsonRootkey_child);

        expListView_child = new ExpandableListView(this);
        ll_pnc_child.addView(expListView_child);


/*

pnc child history
 */

        Log.d(LOGTAG, "---=====>" + queryString);

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

        getRadioButton(R.id.pncMotherSelector).setChecked(true); //select mother by default
    }

    private void handleRadioButton(RadioGroup group, int checkedId) {
        //getRadioButton(checkedId).setChecked();
        group.getCheckedRadioButtonId();
        if(checkedId == R.id.pncMotherSelector) {
            enableMotherLayout();
            Utilities.MakeInvisible(this, R.id.id_pncChildListDropdown);
            /*try {
                if(lastPncVisit> 0) {*/
                    showHidePncDeleteButton(jsonRespMother, true);
               /* } else { //no visit means nothing to show

                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "Could not check last provider:\n\t\t"
                 + " lastVisit: " + lastPncVisit
                 + " JSON:\t" + jsonRespMother.toString());

                Utilities.printTrace(jse.getStackTrace());
            }*/
            //findViewById(R.id.id_pncChildListDropdown).setVisibility(View.GONE);
            //getCheckbox(R.id.stimulation).setChecked(true);
            //getCheckbox(R.id.bag_n_mask).setChecked(false);
        } else if (checkedId == R.id.pncChildSelector) {
            Utilities.SetVisibility(this, R.id.deleteLastPncButton, View.INVISIBLE); //always first set it to invisible until a child is selected
            Utilities.MakeVisible(this, R.id.id_pncChildListDropdown);
            //findViewById(R.id.id_pncChildListDropdown).setVisibility(View.VISIBLE);
            //getCheckbox(R.id.stimulation).setChecked(false);
            //getCheckbox(R.id.bag_n_mask).setChecked(false);
        }
    }

    private void setPncVisitAdvices() {
        Date lmp = mother.getActualDelivery();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
        getTextView(R.id.pncVisit1Date).setText(sdf.format(Utilities.addDateOffset(lmp, FIRST_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(lmp, FIRST_PNC_2)));
        getTextView(R.id.pncVisit2Date).setText(sdf.format(Utilities.addDateOffset(lmp, SECOND_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(lmp, SECOND_PNC_2)));
        getTextView(R.id.pncVisit3Date).setText(sdf.format(Utilities.addDateOffset(lmp, THIRD_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(lmp, THIRD_PNC_2)));
        getTextView(R.id.pncVisit4Date).setText(sdf.format(Utilities.addDateOffset(lmp, FOURTH_PNC_1)) + " - " + sdf.format(Utilities.addDateOffset(lmp, FOURTH_PNC_2)));
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

        ll.removeAllViews();
        Log.d(LOGTAG, "Handle Mother:\n" + result);

            try {
                jsonRespMother = new JSONObject(result);
                String key;

                lastPncVisit = jsonRespMother.getInt("count");
                getTextView(R.id.pncVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastPncVisit+1))); //next visit

                //Check if eligible for new PNC
                if(jsonRespMother.has("pncStatus") &&
                   jsonRespMother.getBoolean("pncStatus")) {
                    Utilities.MakeInvisible(this, R.id.pncMotherInfo);
                    Toast.makeText(this, "Mother is not eligible for new PNC",Toast.LENGTH_LONG).show();
                } else {
                    //get outcome date and populate ideal pnc visit info
                    mother.setActualDelivery(jsonRespMother.getString("outcomeDate"), "yyyy-MM-dd");
                    setPncVisitAdvices();
                    showHidePncDeleteButton(jsonRespMother, true);
                }
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

                    JSONObject jsonRootObject = jsonRespMother.getJSONObject(""+in);
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
                    str1 = edema;

                     animals = str1.split(" ");
                   temp = "";
                    details = res1.getStringArray(R.array.Edema_Dropdown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+" "+details[Integer.parseInt(animal)];
                    }
                    list.add("" + getString(R.string.edema) + " " + temp.trim());

                    // for breastCondition value
                    str1 = "";
                    str1 = breastCondition;

                     animals = str1.split(" ");
                     temp = "";
                    details = res1.getStringArray(R.array.Breast_Condition_DropDown);
                    for (String animal : animals) {
                        System.out.println(animal);
                        if(animal.length()>0)
                            temp = temp+" "+details[Integer.parseInt(animal)];
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
                            temp = temp+" "+details[Integer.parseInt(animal)];
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
                            temp = temp+" "+details[Integer.parseInt(animal)];
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
                                temp = temp+" "+details[Integer.parseInt(animal)];
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
            getSpeciaTextViews(jsonTextViewMapChild, json);

            pncInfoUpdateTask.execute(json.toString(), SERVLET_CHILD, ROOTKEY_CHILD);

            System.out.print("PNCC Save Json in Servlet:" + ROOTKEY_MOTHER + ":{" + json.toString() + "}");

            Utilities.Reset(this, R.id.pncChildInfo);


        } catch (JSONException jse) {
            Log.e("PNCC JSON Exception: ", jse.getMessage());
        }

    }

    private void getSpeciaTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setText(json.getString(key));
                json.put(key, (keyMap.get(key).getText()));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                Utilities.printTrace(jse.getStackTrace());
            }
        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval, boolean isMother) throws JSONException {
        //get info from database
        JSONObject query = null;
        if (isMother) {
            query = buildQueryHeaderMother(isRetrieval);
        } else {
            query = buildQueryHeaderChild(isRetrieval);
        }
        return query;
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

    private void saveService(int saveButton, int cancelButton, int masterLayoutId, boolean saveButtonPressed, boolean isMother) {
        if(saveButtonPressed) {
            pncSaveClick++;
            if(pncSaveClick == 2) {
                if(isMother) {
                    pncMotherSaveToJson();
                } else {
                    pncChildSaveToJson();
                }
                Toast.makeText(this, "Saving Entered Information", Toast.LENGTH_LONG).show();
                pncSaveClick = 0;
                Utilities.Enable(this, masterLayoutId);
                Utilities.MakeInvisible(this, cancelButton);
                getButton(saveButton).setText("Save");

            } else if (pncSaveClick == 1) {

                Utilities.Disable(this, masterLayoutId);
                getButton(saveButton).setText("Confirm");
                Utilities.Enable(this, cancelButton);
                Utilities.Enable(this, saveButton);
                Utilities.MakeVisible(this, cancelButton);
                Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(20);
                toast.show();
            }
        } else {
            pncSaveClick = 0;
            getButton(saveButton).setText("Save");
            Utilities.Enable(this, masterLayoutId);
            Utilities.MakeInvisible(this, cancelButton);
        }
    }

    public void savePnc(View view) {

        if(view.getId() == R.id.pncSaveButton) {
            /// ---

            saveService(R.id.pncSaveButton,
                        R.id.pncCancelButton,
                    R.id.pncMotherInfo,true, true);

            /// ---
            /*
            pncSaveClick++;
            if(pncSaveClick == 2) {
                pncMotherSaveToJson();
                Toast.makeText(this, "Saving Entered Information", Toast.LENGTH_LONG).show();
                pncSaveClick = 0;
                Utilities.Enable(this, R.id.pncMotherInfo);
                Utilities.MakeInvisible(this, R.id.pncCancelButton);
                getButton(R.id.pncSaveButton).setText("Save");

            } else if (pncSaveClick == 1) {

                Utilities.Disable(this, R.id.pncMotherInfo);
                getButton(R.id.pncSaveButton).setText("Confirm");
                Utilities.Enable(this, R.id.pncCancelButton);
                Utilities.Enable(this, R.id.pncSaveButton);
                Utilities.MakeVisible(this, R.id.pncCancelButton);
                Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(20);
                toast.show();
            }*/
        } else if(view.getId() == R.id.pncCancelButton) {
            /*pncSaveClick = 0;
            getButton(R.id.pncSaveButton).setText("Save");
            Utilities.Enable(this, R.id.pncMotherInfo);
            Utilities.MakeInvisible(this, R.id.pncCancelButton);*/
            saveService(R.id.pncSaveButton,
                    R.id.pncCancelButton,
                    R.id.pncMotherInfo, false, true);
        }
    }

    public void savePNCChild (View view){
        if(view.getId() == R.id.pncChildSaveButton) {
            saveService(R.id.pncChildSaveButton,
                    R.id.pncChildCancelButton, R.id.pncChildInfo, true, false);
        } else if(view.getId() == R.id.pncChildCancelButton) {
            saveService(R.id.pncChildSaveButton,
                    R.id.pncChildCancelButton, R.id.pncChildInfo, false, false);
        }
        //pncChildSaveToJson();
        Toast.makeText(this, "Saving Child's Information", Toast.LENGTH_LONG).show();
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
        jsonEditTextMapChild.put("pnctemperature", getEditText(R.id.pncChildTemperatureValue));
        jsonEditTextMapChild.put("pncweight", getEditText(R.id.pncChildWeightValue));
        jsonEditTextMapChild.put("pncbreathingperminute", getEditText(R.id.pncChildBreathValue));

    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewMapChild.put("pncchildno", getTextView(R.id.pncNewBornNumber));
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

        if(!result.equals(child_result)) {
            child_result = result;
        }

        Log.d(LOGTAG, "Handle child:\n\t" + result);
        ll_pnc_child.removeAllViews();
        try {
            jsonRespChild = new JSONObject(result);
            String key;
            //int num=1;
            if(selected_child == 0) { //no child is selected
                return;
            }

            getTextView(R.id.pncNewBornNumber).setText(Utilities.ConvertNumberToBangla(String.valueOf(selected_child)));
            //Utilities.Disable(this, R.id.pncNewBornNumber);

            JSONObject childJson = jsonRespChild.getJSONObject(String.valueOf(selected_child));

            int serviceCount = childJson.getInt("serviceCount");
            getTextView(R.id.pncChildVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(serviceCount+1)));
            if(serviceCount > 0) {
                showHidePncDeleteButton(childJson, false);

                for(int in = 1; in <= serviceCount; in++ ) {
//////
                    JSONObject jsonObject = childJson.getJSONObject("" + in);


                    Log.d("--====>" , "---serviceSource child=====>" + jsonObject.toString());

                    //String complicationsign = jsonRootObject.getString("serviceSource");
                    //String complicationsign = jsonObject.getString("complicationsign");
                    String visitDate = jsonObject.getString("visitDate");


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
                        listDataHeader.add("Visit " + in + ":");//jsonArray.get(0).toString()
                        listDataChild.put(listDataHeader.get(0), list);

                        listAdapter_child = new ExpandableListAdapterforPNC_Child(PNCActivity.this, listDataHeader, listDataChild);

//                                if(serviceCount.length()==1) {
//                                    count = Integer.parseInt(serviceCount);
//
//
//                                }
//                                else
                        //count = 0;
                        //Log.d("...........>", "=....count=>" + count);
                        //num++;
                        initPage_child();
                        //ll_pnc_child = (LinearLayout)findViewById(R.id.llay_frag);

                        //if (count >= 1) {
                        ll_pnc_child.addView(expListView_child);
                        expListView_child.setScrollingCacheEnabled(true);
                        expListView_child.setAdapter(listAdapter_child);
                        ll_pnc_child.invalidate();

                    } catch (Exception e) {
                        Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                        e.printStackTrace();
                    }
//////    ---------------------------------------------------------------------------------
                }
            }
        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n ");
            jse.printStackTrace();
        }
    }

    private void enableMotherLayout() {
        lay_frag_child.setVisibility(View.GONE);
        pnclay_child.setVisibility(View.GONE);
        getSpinner(R.id.id_pncChildListDropdown).setSelection(0);// un-select selected children

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pncServiceDateValue || v.getId() == R.id.Date_Picker_Button || v.getId() == R.id.Date_Picker_Button_Child) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }



        if(v.getId() == R.id.pncmother){
           enableMotherLayout();
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
            childDropdown.setVisibility(getRadioButton(R.id.pncChildSelector).isChecked()? View.VISIBLE:View.INVISIBLE);
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

        Log.d("-------child test------","---------");
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

    private void showHidePncDeleteButton(JSONObject jso, boolean isMother) {
        String key = (isMother? "count":"serviceCount");
        int lastVisit = 0; // no last visit

        try {
            if (jso != null && jso.has(key)) {
                lastVisit = jso.getInt(key);

                //if (lastVisit > 0) {
                Utilities.SetVisibility(
                        this,
                        R.id.deleteLastPncButton,
                        (lastVisit > 0) && isLastPncDeletable(jso.getJSONObject(String.valueOf(lastVisit))) ? View.VISIBLE : View.GONE);
                //}
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could determine visibility:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private boolean isLastPncDeletable(JSONObject jso) {

        String providerCode = null;
        if(jso == null) { //initial response
            return false;
        }
        try {

            providerCode = jso.getString("providerId");
            Log.d(LOGTAG,"Last Service was provide by: \t" + providerCode);
            return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {
            Log.e(LOGTAG,"JSON Exception Caught\n\t\t");
            if(jso != null) {
                Log.e(LOGTAG,"JSON ->" + jso.toString());
            }
            Utilities.printTrace(jse.getStackTrace(), 10);
        }

        //Set<String> keySey = jso.

        return false;
    }

    private void deleteConfirmed(boolean isMother) {
        try {

            JSONObject deleteJson = buildQueryHeader(false, isMother);
            String servlet = "";
            String rootkey = "";
            String loadKey = "";

            if(isMother) {
                servlet = SERVLET_MOTHER;
                rootkey = ROOTKEY_MOTHER;
                loadKey = "pncMLoad";
                pncInfoUpdateTask = new AsyncPNCInfoUpdate(this);
            } else {
                servlet = SERVLET_CHILD;
                rootkey = ROOTKEY_CHILD;
                loadKey = "pncCLoad";
                deleteJson.put("pncchildno", selected_child);
                pncInfoUpdateTask = new AsyncPNCInfoUpdate(new AsyncCallback() {
                    @Override
                    public void callbackAsyncTask(String result) {
                        handleChild(result);
                    }
                });
            }

            deleteJson.put(loadKey, "delete");

            pncInfoUpdateTask.execute(deleteJson.toString(), servlet, rootkey);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastPNC(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(PNCActivity.this).create();
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
                        deleteConfirmed(findViewById(R.id.pncMotherInfo).getVisibility() == View.VISIBLE);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(PNCActivity.this).create();
        alertDialog.setTitle("EXIT CONFIRMATION");
        alertDialog.setMessage("আপনি কি প্রসবোত্তর সেবা ( PNC ) থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

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
