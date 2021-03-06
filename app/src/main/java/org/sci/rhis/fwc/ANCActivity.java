package org.sci.rhis.fwc;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class ANCActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener,
                                                                     CompoundButton.OnCheckedChangeListener{

    private PregWoman mother;
    private ProviderInfo provider;
    private Date today;

// For Date pick added by Al Amin
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    final static int FIRST_ANC_1 = 15; //WEEKS
    final static int FIRST_ANC_2 = 16; //WEEKS
    final static int SECOND_ANC_1 = 23; //WEEKS
    final static int SECOND_ANC_2 = 24; //WEEKS
    final static int THIRD_ANC_1 = 31; //WEEKS
    final static int THIRD_ANC_2 = 32; //WEEKS
    final static int FOURTH_ANC_1 = 35; //WEEKS
    final static int FOURTH_ANC_2 = 36; //WEEKS

    //whoprovide last service
    final static int PROVIDER_CODE = 23;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    LinearLayout ll;

    ANCListAdapter ancAdapter;
    AsyncAncInfoUpdate ancInfoUpdate;


//    ExpandableListAdapter listAdapter2;
//    ExpandableListView expListView2;
//    List<String> listDataHeader2;
//    HashMap<String, List<String>> listDataChild2;
//
//    ExpandableListAdapter listAdapter3;
//    ExpandableListView expListView3;
//    List<String> listDataHeader3;
//    HashMap<String, List<String>> listDataChild3;

    final private String SERVLET = "anc";
    final private String ROOTKEY = "ANCInfo";

    private  final String LOGTAG    = "FWC-ANC";

    //JSONArray visits = null;

    ListView listView ;
    //private Button newanc;
    private View mANCLayout;
    private MultiSelectionSpinner multiSelectionSpinner;
    ArrayList<String> list;
    Boolean flag=false;

    JSONObject jsonStr;
    String[] mainlist;
    String[] details;
    ArrayList list1;

    private LinearLayout history_layout;


    private int lastAncVisit;
    JSONArray json_Array = null;
    private Context con;
    private int countSaveClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // woman = getIntent().getParcelableExtra("PregWoman");


       // Log.d("---woman---------",""+woman.toString());
        today = new Date();
        lastAncVisit = 0; // assume no visit
//        if( woman.getAncThreshold().after(today)) { // add ANC threshold
//            Toast.makeText(this, "Too Late for ANC, ask delivery status ...", Toast.LENGTH_LONG).show();
//        } else {
//            setContentView(R.layout.activity_anc);
//        }

        setContentView(R.layout.activity_anc);

        con = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        history_layout = (LinearLayout)(findViewById(R.id.history_lay_anc));


        // Find the view whose visibility will change
        mANCLayout = findViewById(R.id.ancLayoutScrollview);

        OnClickListener mnewancVisibleListener = new OnClickListener() {
            public void onClick(View v) {
                if(flag==false) {
                    mANCLayout.setVisibility(View.VISIBLE);
                    flag=true;
                }
                else
                {
                    mANCLayout.setVisibility(View.INVISIBLE);
                    flag=false;
                }
            }
        };

        // Find our buttons
        Button visibleButton = (Button) findViewById(R.id.ancLabelButton);

        OnClickListener mVisibleListener = new OnClickListener() {
            public void onClick(View v) {
            if(flag==false) {
                mANCLayout.setVisibility(View.VISIBLE);
                flag=true;
               // listView.setVisibility(View.VISIBLE);
            }
            else
            {
                mANCLayout.setVisibility(View.INVISIBLE);
                flag=false;
                //listView.setVisibility(View.INVISIBLE);
            }
        }
        };

        // Wire each button to a click listener
        visibleButton.setOnClickListener(mVisibleListener);
        getCheckbox(R.id.ancOtherCheckBox).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.ancOtherCheckBox:
                        setItemVisible(R.id.ancOtherCenterNameSpinner, isChecked);
                        break;
                }
            }
        });

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
        //listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) listAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
        // get the listview
      //  expListView = (ExpandableListView) findViewById(R.id.lvExp);

    //    expListView2 = (ExpandableListView) findViewById(R.id.lvExp2);

     //   expListView3 = (ExpandableListView) findViewById(R.id.lvExp3);

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");
        provider = getIntent().getParcelableExtra("Provider");

        AsyncClientInfoUpdate client = new AsyncClientInfoUpdate(ANCActivity.this);


        loadANCHistory();




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


        getEditText(R.id.ancServiceDateValue).setOnClickListener(this);
        getCheckbox(R.id.ancReferCheckBox).setOnCheckedChangeListener(this);
      //custom date picker Added By Al Amin
        datePickerDialog = new CustomDatePickerDialog(this, "dd/MM/yyyy");
        datePickerPair = new HashMap<Integer, EditText>();
       datePickerPair.put(R.id.Date_Picker_Button, (EditText)findViewById(R.id.ancServiceDateValue));

        //disable ANC entry if delivery info is present
        if(mother.getDeliveryInfo() == 1) {
            Utilities.MakeInvisible(this, R.id.ancEntryMasterLayout);
            Toast.makeText(this, "Mother is not eligible for new ANC",Toast.LENGTH_LONG).show();
        } else {
            //set ideal vist periods
            setAncVisitAdvices();
        }
    }

    private void setAncVisitAdvices() {
        Date lmp = mother.getLmp();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
        getTextView(R.id.ancVisit1Date).setText(sdf.format(Utilities.addDateOffset(lmp, FIRST_ANC_1 * 7)) + " - " + sdf.format(Utilities.addDateOffset(lmp, FIRST_ANC_2 * 7)));
        getTextView(R.id.ancVisit2Date).setText(sdf.format(Utilities.addDateOffset(lmp, SECOND_ANC_1 * 7)) + " - " + sdf.format(Utilities.addDateOffset(lmp, SECOND_ANC_2 * 7)));
        getTextView(R.id.ancVisit3Date).setText(sdf.format(Utilities.addDateOffset(lmp, THIRD_ANC_1 * 7)) + " - " + sdf.format(Utilities.addDateOffset(lmp, THIRD_ANC_2 * 7)));
        getTextView(R.id.ancVisit4Date).setText(sdf.format(Utilities.addDateOffset(lmp, FOURTH_ANC_1 * 7)) + " - " + sdf.format(Utilities.addDateOffset(lmp, FOURTH_ANC_2 * 7)));
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

    private void loadANCHistory() {
        //SendPostRequestAsyncTask
        AsyncLoginTask sendPostReqAsyncTask = new AsyncLoginTask(ANCActivity.this);

        Log.d("-!!!!!!!!!!->"+provider.getProviderCode(), "---=keys()====>" + mother.getPregNo());
        String queryString =   "{" +
                "pregNo:" + mother.getPregNo() + "," +
                "healthid:" + mother.getHealthId() + "," +
                "ancLoad:" + provider.getProviderCode() +
                "}";
        String servlet = "anc";
        String jsonRootkey = "ANCInfo";
        sendPostReqAsyncTask.execute(queryString, servlet, jsonRootkey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anc, menu);
        return true;
    }

    @Override
    public void callbackAsyncTask(String result) {

        ll.removeAllViews();
        try {
            jsonStr = new JSONObject(result);
            String key;
            lastAncVisit = jsonStr.length() -1 ; //each anc visit has 1 extra ket denoting current anc status
            getTextView(R.id.ancVisitValue).setText(Utilities.ConvertNumberToBangla(String.valueOf(lastAncVisit >= 0 ? lastAncVisit + 1 : 1)));
            Log.d("ANC", "JSON Response:\n"+jsonStr.toString());

            //Check if eligible for new ANC
            if(jsonStr.has("ancStatus") &&
               jsonStr.getBoolean("ancStatus")) {
                //Utilities.MakeInvisible(this, R.id.ancEntryMasterLayout);
                Utilities.Disable(this, R.id.ancEntryMasterLayout);
                Toast.makeText(this, "Mother is not eligible for new ANC",Toast.LENGTH_LONG).show();
            }
            else {
                Utilities.Enable(this, R.id.ancEntryMasterLayout);
                showHideAncDeleteButton(jsonStr);
            }

            //DEBUG
            Resources res = getResources();
            mainlist = res.getStringArray(R.array.list_item);

            for ( Iterator<String> ii = jsonStr.keys(); ii.hasNext(); ) {
                key = ii.next();
                list = new ArrayList<String>();
                System.out.println("1.Key:" + key + " Value:\'" + jsonStr.get(key) + "\'");


                if (key.equalsIgnoreCase("ancStatus")) {

                    // continue;
                } else {
                    //list.add("" + key);
                    try {
                        JSONArray jsonArray = jsonStr.getJSONArray(key);


                        for (int i = 1; i < jsonArray.length()-1; i++) {
                            Log.i("--------", "hhhhhh--- is" + jsonArray.get(i).toString());
                            String det = jsonArray.get(i).toString();
                            det = det.replaceAll("[^0-9]+", " ");
                            det = det.trim();
                            if (i == 2) {

                            } else
                            if (i == 3) {
                                list.add("" + mainlist[i-2] + "" + jsonArray.get(i-1).toString() + " / " + jsonArray.get(i).toString());
                            }
                            else if (i == 5) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Edema_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+" "+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }
                            else if (i == 8) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Fetal_Presentation_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+" "+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }

                            else if (i == 9) {
                                list.add("" + mainlist[i-1] + "" + jsonArray.get(i).toString()+"%");
                            }
                            else if (i == 10) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Jaundice_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+" "+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }
                            else if (i == 11) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Urine_Test_Sugar_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+" "+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }
                            else if (i == 12) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Urine_Test_Albumin_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+" "+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }
                            else if (i == 18 && Integer.parseInt(det)==1) {
                                list.add("" + mainlist[i-1] + "Yes" );
                            }else if (i == 18 && Integer.parseInt(det)==2) {
                                list.add("" + mainlist[i-1] + "No" );
                            }
                            else if (i == 19) {
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.FacilityType_DropDown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+"\n"+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-1] +temp );

                            }
                            else if (i == 21) {

                            }
                            else if (i == 22) { //anemia
                                String[] details;
                                Resources res1 = con.getResources();
                                String str1 = det;


                                String[] animals = str1.split(" ");
                                String temp = "";
                                details = res1.getStringArray(R.array.Anemia_Dropdown);
                                for (String animal : animals) {
                                    System.out.println(animal);
                                    if(animal.length()>0)
                                        temp = temp+details[Integer.parseInt(animal)];
                                }
                                list.add("" + mainlist[i-2] +temp );

                            }
                            else
                                list.add("" + mainlist[i-1] + "" + jsonArray.get(i).toString());

                           // list.add("" + key);
                        }//end for
                        listDataHeader = new ArrayList<String>();
                        listDataChild = new HashMap<String, List<String>>();

                        listDataHeader.add("Visit "+jsonArray.get(0).toString() + ":");
                        listDataChild.put(listDataHeader.get(0), list);

                        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);


                        initPage();


                        // ll.addView(btn);
                        ll.addView(expListView);
                        expListView.setScrollingCacheEnabled(true);
                        expListView.setAdapter(listAdapter);


                        ll.invalidate();
                        //  expListView.setAdapter(listAdapter);


                    } catch (JSONException e) {
                        Log.e("::::", "onPostExecute > Try > JSONException => " + e);
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException aiob) {
                        Log.e(LOGTAG, "Array Exception:\n\t\t");
                        Utilities.printTrace(aiob.getStackTrace(), 10);
                    }
                }
            }

        } catch (JSONException jse) {
            System.out.println("JSON Exception Thrown::\n " );
            jse.printStackTrace();
        }
    }

    private String parseString(int arg2) {

        String s = null;
        try {
            s = ""+json_Array.get(arg2).toString();
            s = s.replaceAll("[^0-9]+", " ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //AlertMessage.showMessage(con,"Details",temp);
        return s;
    }


    private void initPage() {
        expListView = new ExpandableListView(this);
        expListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        expListView.setIndicatorBounds(0, 0);
        expListView.setChildIndicatorBounds(0, 0);
        expListView.setStackFromBottom(true);
       // ll.addView(expListView);
      //  expListView.smoothScrollToPosition(expListView.getCount() - 1);
    }


    void setItemVisible(int ItemId, boolean isChecked) {
        Spinner Item=(Spinner)findViewById(ItemId);
        Item.setSelection(0);
        findViewById(ItemId).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
     {
         if (buttonView.getId() == R.id.ancReferCheckBox) {
             int visibility = isChecked? View.VISIBLE: View.GONE;
             int layouts[] = {R.id.ancReferCenterNameLayout, R.id.ancReasonLayout};

             for(int i = 0 ; i < layouts.length; i++) {
                 Utilities.SetVisibility(this, layouts[i],visibility);
             }
         }
     }


    // Added by Al Amin
    @Override
    public void onClick(View v) {

        if( v.getId() == R.id.ancServiceDateValue ||
            v.getId() == R.id.Date_Picker_Button ) {
            datePickerDialog.show(datePickerPair.get(v.getId()));
        }

        if(v.getId() == R.id.ancSaveButton) {

            ll.removeAllViews();

            //-- confirm first
            countSaveClick++;
            if( countSaveClick == 2 ) {
                saveAnc(v);
                getButton(R.id.ancSaveButton).setText("Save");
                Utilities.Enable(this, R.id.ancEntryMasterLayout);
                Utilities.MakeInvisible(this, R.id.ancEditButton);
                countSaveClick = 0;

            } else if(countSaveClick == 1) {
                Utilities.Disable(this, R.id.ancEntryMasterLayout);
                getButton( R.id.ancSaveButton).setText("Confirm");
                Utilities.Enable(this, R.id.ancSaveButton);
                getButton( R.id.ancEditButton).setText("Cancel");
                Utilities.Enable(this, R.id.ancEditButton);
                Utilities.MakeVisible(this, R.id.ancEditButton);

                Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(20);
                toast.show();
            }
        } else if(v.getId() == R.id.ancEditButton) {
            if(countSaveClick == 1) {
                countSaveClick = 0;
                Utilities.Enable(this, R.id.ancEntryMasterLayout);
                getButton(R.id.ancSaveButton).setText("Save");
                //TODO - Review
                Utilities.MakeInvisible(this, R.id.ancEditButton);
            }
        }
            // --

            //initPage();
            //loadANCHistory();

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
        jsonSpinnerMap.put("ancanemia",getSpinner(R.id.ancAnemiaSpinner));
        jsonSpinnerMap.put("ancjaundice", getSpinner(R.id.ancJaundiceSpinner));
        jsonSpinnerMap.put("ancsugar", getSpinner(R.id.ancUrineSugarSpinner));
        jsonSpinnerMap.put("ancalbumin", getSpinner(R.id.ancUrineAlbuminSpinner));
        jsonSpinnerMap.put("anccentername", getSpinner(R.id.ancReferCenterNameSpinner));
        jsonSpinnerMap.put("ancservicesource", getSpinner(R.id.ancOtherCenterNameSpinner));
    }

    //verride
    protected void initiateMultiSelectionSpinners() {
        jsonMultiSpinnerMap.put("anccomplication", getMultiSelectionSpinner(R.id.ancDangerSignsSpinner));
        jsonMultiSpinnerMap.put("ancsymptom", getMultiSelectionSpinner(R.id.ancDrawbackSpinner));
        jsonMultiSpinnerMap.put("ancdisease", getMultiSelectionSpinner(R.id.ancDiseaseSpinner));
        jsonMultiSpinnerMap.put("anctreatment", getMultiSelectionSpinner(R.id.ancTreatmentSpinner));
        jsonMultiSpinnerMap.put("ancadvice", getMultiSelectionSpinner(R.id.ancAdviceSpinner));
        jsonMultiSpinnerMap.put("ancreferreason", getMultiSelectionSpinner(R.id.ancReasonSpinner));
    }

    @Override
    protected void initiateEditTexts() {
        //anc visit
        //jsonEditTextMap.put("pregNo", getEditText(R.id.ancVisitValue));
        jsonEditTextMap.put("ancbpsys", getEditText(R.id.ancBloodPresserValueSystolic));
        jsonEditTextMap.put("ancbpdias", getEditText(R.id.ancBloodPresserValueDiastolic));
        jsonEditTextMap.put("ancweight", getEditText(R.id.ancWeightValue));
        jsonEditTextMap.put("ancuheight", getEditText(R.id.ancUterusHeightValue));
        jsonEditTextMap.put("anchrate", getEditText(R.id.ancHeartSpeedValue));
        jsonEditTextMap.put("anchemoglobin", getEditText(R.id.ancHemoglobinValue));
       }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateEditTextDates() {
        // ANC Service Date
        jsonEditTextDateMap.put("ancdate", getEditText(R.id.ancServiceDateValue));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void saveAnc(View view) {
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getEditTextDates(jsonEditTextDateMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            //getEditTextTime(json);
            getSpecialCases(json);
            ancInfoUpdate = new AsyncAncInfoUpdate(this);
            ancInfoUpdate.execute(json.toString(), SERVLET, ROOTKEY);

            //Utilities.Reset(this, R.id.ancEntryMasterLayout);
            //Utilities.Disable(this, R.id.clients_info_layout);

            Log.i("ANC", "Save Succeeded");
            Log.d("ANC", "JSON:\n" + json.toString());
            Utilities.Reset(this, R.id.ancText);

        } catch (JSONException jse) {
            Log.e("ANC", "JSON Exception: " + jse.getMessage());
        }
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("ancsatelitecentername", provider.getSatelliteName()); //If the service was given from satellite
            if(jsonSpinnerMap.get("ancservicesource").getVisibility() != View.VISIBLE) {
                json.put("ancservicesource", "5"); //anc service source UHFWC
            }
        } catch (JSONException jse) {

        }
    }

    private void showHideAncDeleteButton(JSONObject jso) {
        Utilities.SetVisibility(this, R.id.deleteLastAncButton, isLastAncDeletable(jso) ? View.VISIBLE :View.INVISIBLE);
        //findViewById(R.id.deleteLastAncButton).setVisibility(isLastAncDeletable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregNo:\"" + String.valueOf(mother.getPregNo()) + "\"," +
                "ancLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        //SendPostRequestAsyncTask retrieveDelivery = new AsyncDeliveryInfoUpdate(this);
        //retrieveDelivery.execute(queryString, SERVLET, ROOTKEY);
        return new JSONObject(queryString);
    }

    private boolean isLastAncDeletable(JSONObject jso) {
        String lastAncKey = "ancVisit";
        lastAncKey += (lastAncVisit <10 ? "0" : "") + String.valueOf(lastAncVisit);

        JSONArray lastVisit = null;
        String providerCode = null;
        try {
            lastVisit = jso.getJSONArray(lastAncKey);
            providerCode = lastVisit.getString(PROVIDER_CODE);
            Log.d(LOGTAG,"Last Service "+lastAncKey+" was provide by: \t" + providerCode);

            return (provider.getProviderCode().equals(providerCode));

        } catch (JSONException jse) {

        }

        //Set<String> keySey = jso.

        return false;
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("ancLoad", "delete");
            ancInfoUpdate = new AsyncAncInfoUpdate(this);
            ancInfoUpdate.execute(deleteJson.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastANC(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(ANCActivity.this).create();
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
        AlertDialog alertDialog = new AlertDialog.Builder(ANCActivity.this).create();
        alertDialog.setTitle("EXIT CONFIRMATION");
        alertDialog.setMessage("আপনি কি গর্ভকালীন সেবা ( ANC ) থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

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
