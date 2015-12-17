package org.sci.rhis.fwc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeliveryNewbornActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
                                                                                View.OnClickListener,
                                                                                CompoundButton.OnCheckedChangeListener{

    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;


    private PregWoman mother;
    private JSONObject deliveryJsonObj;
    private JSONObject newbornDeleteObj;
    private ProviderInfo provider;
    private int flag =0;
    private int integerRecd = 0;
    private int currentChildNo = 0;
    private int countSaveClick=0;

    final private String SERVLET = "newborn";
    final private String ROOTKEY= "newbornInfo";
    private  final String LOGTAG    = "FWC-NEWBORN";

    AsyncNewbornInfoUpdate newbornInfoQueryTask;
    AsyncNewbornInfoUpdate newbornInfoUpdateTask;
    Intent returnIntent;

    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_newborn);

        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initialize(); //initialize the inherited maps
        /**get the intent*/
        Intent intent = getIntent();

        integerRecd = intent.getIntExtra("Layout", flag);

        switch(integerRecd) {   /*Design different Layouts for different types*/
            case 1:
                Utilities.MakeInvisible(this, R.id.newBornDetectionLayout);
                jsonEditTextMap.get("birthStatus").setText("1");
                break;
            case 2:
                Utilities.MakeInvisible(this, R.id.newBornDetectionLayout);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);
                jsonEditTextMap.get("birthStatus").setText("2");
                break;

            case 3:
                Utilities.MakeInvisible(this, R.id.deliveryWipe);
                Utilities.MakeInvisible(this, R.id.deliveryResastation);
                Utilities.MakeInvisible(this, R.id.StimulationBagNMask);
                Utilities.MakeInvisible(this, R.id.layout_only_for_neborn);

                jsonEditTextMap.get("birthStatus").setText("3");
                break;
        }

        newbornDeleteObj=new JSONObject();

        //Intent outComePlace = getIntent();
        String str = intent.getStringExtra("DeliveryJson");
        Log.d(LOGTAG, "Delivery JSON:\t"+ str);

        Spinner referSpinner= (Spinner)findViewById(R.id.deliveryChildReferCenterNameSpinner);

        final List<String> newbornReferReasonList = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornReferReasonList);
        multiSelectionSpinner.setSelection(new int[]{});

        getCheckbox(R.id.deliveryChildReferCheckBox).setOnCheckedChangeListener(this);
        getRadioGroup(R.id.id_newBornResasscitationRadioGroup).setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    handleRadioButton(group, checkedId);
                }
        });
        //create the mother
        mother = intent.getParcelableExtra("PregWoman");
        provider = intent.getParcelableExtra("Provider");

        try {
            deliveryJsonObj = new JSONObject(str);
            /*if(isImmature(mother, deliveryJsonObj.get("dDate").toString())){
                jsonTextViewsMap.get("immature").setVisibility(View.VISIBLE);
            }*/
            checkSetMaturity();
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }

        try {

            if(intent.hasExtra("NewbornJson")) { //check if new bron info is given
                Log.d(LOGTAG, "Restoring Child Info");

                JSONObject restoreJson=new JSONObject(getIntent().getStringExtra("NewbornJson"));

                newbornDeleteObj.put("gender", restoreJson.getString("gender"));
                newbornDeleteObj.put("childno",restoreJson.getString("childno"));
                newbornDeleteObj.put("birthStatus", String.valueOf(integerRecd));

                restoreNewbornFromJSON(restoreJson);


            } else {
                Utilities.Enable(this, R.id.DeliveryNewBornLayout);
                //Utilities.Disable(this, R.id.deliveryNewBornNo);
                if(intent.hasExtra("childno")){ //set child no in UI
                    int childno = intent.getIntExtra("childno", 0);
                    jsonTextViewsMap.get("childno").setText(String.valueOf(childno));
                }
            }

        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(LOGTAG, "Resumed Activity");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(LOGTAG, "Stopped Activity");
        Utilities.Reset(this, R.id.DeliveryNewBornLayout);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(LOGTAG, "Paused Activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery_newborn, menu);
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
        returnIntent = new Intent();
        returnIntent.putExtra("ReloadNewborn", true);
        returnIntent.putExtra("ChildDetails", result);
        setResult(RESULT_OK, returnIntent);
        finishActivity(ActivityResultCodes.NEWBORN_ACTIVITY);
        finish();
    }

    @Override
    protected void initiateCheckboxes() {
// For New born
        jsonCheckboxMap.put("stimulation", getCheckbox(R.id.stimulation));
        jsonCheckboxMap.put("bagNMask", getCheckbox(R.id.bag_n_mask));
        jsonCheckboxMap.put("refer", getCheckbox(R.id.deliveryChildReferCheckBox));
    }

    @Override
    protected void initiateEditTexts() {
        // for New born layout
        //jsonEditTextMap.put("childno",  getEditText(R.id.deliveryNewBornNo));
        jsonEditTextMap.put("birthStatus", getEditText(R.id.deliveryNewBornConditionValue));
        jsonEditTextMap.put("weight", getEditText(R.id.deliveryNewBornWeightValue));
    }

    @Override
    protected void initiateTextViews() {
        jsonTextViewsMap.put("childno",  getTextView(R.id.deliveryNewBornNo));
        jsonTextViewsMap.put("immature",  getTextView(R.id.deliveryNewBornMaturity));
        jsonTextViewsMap.put("birthStatus",getTextView(R.id.deliveryNewBornConditionValue));
    }

    @Override
    protected void initiateSpinners() {
        // for New born Layout
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.deliveryChildReferCenterNameSpinner));
    }

    @Override
    protected void initiateMultiSelectionSpinners(){
        jsonMultiSpinnerMap.put("referReason", getMultiSelectionSpinner(R.id.deliveryChildReferReasonSpinner));
    }

    @Override
    protected void initiateEditTextDates() {

    }

    @Override
    protected void initiateRadioGroups() {
        //for NewBorn
        jsonRadioGroupButtonMap.put("gender", Pair.create(
                        getRadioGroup(R.id.id_newBornSexRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryNewBornSon),
                                getRadioButton(R.id.deliveryNewBornDaughter)
                                //getRadioButton(R.id.deliveryNewBornNotDetected)
                        )
                )
        );

        jsonRadioGroupButtonMap.put("dryingAfterBirth", Pair.create(
                        getRadioGroup(R.id.id_newBornWipeRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryWipeYesButton),
                                getRadioButton(R.id.deliveryWipeNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("resassitation", Pair.create(
                        getRadioGroup(R.id.id_newBornResasscitationRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryResastationYesButton),
                                getRadioButton(R.id.deliveryResastationNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("chlorehexidin", Pair.create(
                        getRadioGroup(R.id.id_newBornChlorohexidineRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryPlacentaCuttingYesButton),
                                getRadioButton(R.id.deliveryPlacentaCuttingNoButton))
                )
        );
        jsonRadioGroupButtonMap.put("skinTouch", Pair.create(
                        getRadioGroup(R.id.id_newBornChordCareRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryFittedWithMotherSkinYesButton),
                                getRadioButton(R.id.deliveryFittedWithMotherSkinNoButton))
                )
        );

        jsonRadioGroupButtonMap.put("breastFeed", Pair.create(
                        getRadioGroup(R.id.id_newBornBreastFeedingRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryBreastFeedingYesButton),
                                getRadioButton(R.id.deliveryBreastFeedingNoButton))
                )
        );
    }
    private HashMap<RadioGroup, Pair<RadioButton, RadioButton>> getRadioMap(int groupId, int yesId, int noId) {

        HashMap<RadioGroup, Pair<RadioButton, RadioButton>> temp
                = new HashMap<RadioGroup, Pair<RadioButton, RadioButton>>();
        temp.put(
                getRadioGroup(groupId),
                Pair.create(
                        getRadioButton(yesId),
                        getRadioButton(noId)
                )
        );
        return temp;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.deliveryChildReferCheckBox) {
            int visibility = isChecked? View.VISIBLE: View.INVISIBLE;
            int layouts[] = {R.id.deliveryChildReferCenterName, R.id.deliveryChildReferReason};

            for(int i = 0 ; i < layouts.length; i++) {
                Utilities.SetVisibility(this, layouts[i],visibility);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_saveNewbornButton) {
            countSaveClick++;
            if (countSaveClick == 2) {
                newbornSaveToJson();
                Toast.makeText(this, "Newborn Saved Successfully", Toast.LENGTH_LONG).show();
                countSaveClick = 0;

            } else if (countSaveClick == 1) {
                if(!hasTheRequiredFileds()) {
                    countSaveClick = 0;
                    return;
                }
                Utilities.Disable(this, R.id.NewBorn);
                Utilities.Enable(this, R.id.id_saveNewbornButton);
                Utilities.Enable(this, R.id.id_OkNewbornButton);
                Utilities.Enable(this, R.id.DeleteLastNewbornButton);

                getButton(R.id.id_saveNewbornButton).setText("Confirm");
                Utilities.MakeVisible(this, R.id.id_OkNewbornButton);

                Toast toast = Toast.makeText(this, R.string.DeliverySavePrompt, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(20);
                toast.show();
            }

        } else if (v.getId() == R.id.id_OkNewbornButton) {
            if(countSaveClick == 0) {
                finishActivity(ActivityResultCodes.NEWBORN_ACTIVITY);
                finish();
            }

            else if(countSaveClick == 1) {
                countSaveClick = 0;
                Utilities.Enable(this, R.id.NewBorn);
                getButton(R.id.id_saveNewbornButton).setText("Save");
            }
        } else if (v.getId() == R.id.DeleteLastNewbornButton) {
            deleteLastNewborn(v);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleRadioButton(RadioGroup group, int checkedId) {
        if(checkedId == R.id.deliveryResastationYesButton) {
            Utilities.MakeVisible(this, R.id.StimulationBagNMask);
            getCheckbox(R.id.stimulation).setChecked(true);
            getCheckbox(R.id.bag_n_mask).setChecked(false);
        } else if (checkedId == R.id.deliveryResastationNoButton) {
            Utilities.MakeInvisible(this, R.id.StimulationBagNMask);
            getCheckbox(R.id.stimulation).setChecked(false);
            getCheckbox(R.id.bag_n_mask).setChecked(false);
        }
    }
    private void newbornSaveToJson() {

        newbornInfoUpdateTask = new AsyncNewbornInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getMultiSelectSpinnerIndices(jsonMultiSpinnerMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            getSpecialCases(json);
            Log.d("DeliveryJsonFoundinSave", json.toString());
            Log.e("Servlet and Rootkey", SERVLET + " " + ROOTKEY);
            newbornInfoUpdateTask.execute(json.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {

            Log.d("Newborn", "JSON Exception: " + jse.getMessage());
        }
    }

    private void restoreNewbornFromJSON(JSONObject json) {
        Utilities.setCheckboxes(jsonCheckboxMap, json);
        Utilities.setSpinners(jsonSpinnerMap, json);
        Utilities.setMultiSelectSpinners(jsonMultiSpinnerMap, json);
        //updateRadioButtons(json);
        Utilities.setEditTexts(jsonEditTextMap, json);
        Utilities.setEditTextDates(jsonEditTextDateMap, json);
        Utilities.setRadioGroupButtons(jsonRadioGroupButtonMap, json);
        Utilities.setTextViews(jsonTextViewsMap,json);
        //updateEditTextTimes(json);
        setSpecialCases(json);
        Log.d(LOGTAG, "Delivery Response Received:\n\t" + json.toString());
        //dJson = json;

        showHideNewbornDeleteButton(json);

        Utilities.Disable(this, R.id.DeliveryNewBornLayout);
        Utilities.Enable(this, R.id.id_OkNewbornButton);
        Utilities.Enable(this, R.id.DeleteLastNewbornButton);
    }

    public void setSpecialCases(JSONObject json) {
        //check if massarated
        try {
            if(integerRecd == 3) {
                if(json.has("gender") &&
                   json.getString("gender").equals("3")) {
                   getRadioButton(R.id.deliveryNewBornNotDetected).setChecked(true);
                }
            }
        }  catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    private void showHideNewbornDeleteButton(JSONObject json) {
        Utilities.SetVisibility(this, R.id.DeleteLastNewbornButton, isLastNewborn(json) ? View.VISIBLE :View.INVISIBLE);
        //findViewById(R.id.deleteLastAncButton).setVisibility(isLastAncDeletable(jso) ? View.VISIBLE :View.INVISIBLE);
    }

    private boolean isLastNewborn(JSONObject json){
        try {
            return (json.getString("lastchildno").equals(json.getString("childno")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getSpecialCases(JSONObject json) {
        try {
           json.put("childno",jsonTextViewsMap.get("childno").getText());
           json.put("immature", (jsonTextViewsMap.get("immature").getVisibility()==View.VISIBLE) ? "1" : "2");
           json.put("outcomeplace", deliveryJsonObj.getInt("dPlace"));
           json.put("outcomedate", deliveryJsonObj.getString("dDate"));
           json.put("outcometime", deliveryJsonObj.getString("dTime"));
           json.put("outcometype", deliveryJsonObj.getInt("dType"));
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        }
    }


    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {

        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
               "providerid:"+ String.valueOf(provider.getProviderCode()) + "," +
                "pregno:" + mother.getPregNo() + "," +
                "newbornLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

          //Log.e("Is there have Values?", queryString);

        return new JSONObject(queryString);
    }

    private void checkSetMaturity() {
        try {
            if (deliveryJsonObj.has("immatureBirth") && deliveryJsonObj.getInt("immatureBirth") == 1 ) {
                jsonTextViewsMap.get("immature").setText(getString(R.string.premature_birth_before_37_weeks_full)
                        + Utilities.ConvertNumberToBangla(deliveryJsonObj.getString("immatureBirthWeek"))
                + getString(R.string.week));
                Utilities.SetVisibility(this, R.id.deliveryNewBornMaturity, View.VISIBLE);
            }
        } catch (JSONException jse) {
            Log.e(LOGTAG, "JSON Error:\n\t\t");
            Utilities.printTrace(jse.getStackTrace());
        } catch (NumberFormatException nfe) {
            Log.e(LOGTAG, "Could nt convert Response to Bangla");
            Utilities.printTrace(nfe.getStackTrace());
        }
    }

    private boolean isImmature(PregWoman mother, String date) { // receive date in json format that is
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long days = 0;
        try {
            Date deliveryDate = sdf.parse(date);
            days = TimeUnit.DAYS.convert(mother.getLmp().getTime() - deliveryDate.getTime(), TimeUnit.DAYS);

        } catch (ParseException PE) {

        }
        return (days < (37 * 7) );
    }

    private void deleteConfirmed() {
        try {

            JSONObject deleteJson = buildQueryHeader(false);
            deleteJson.put("newbornLoad", "delete");
            deleteJson.put("birthStatus", newbornDeleteObj.getString("birthStatus"));
            deleteJson.put("gender", newbornDeleteObj.getString("gender"));
            deleteJson.put("childno", newbornDeleteObj.getString("childno"));

            Log.d("look", deleteJson.toString());

            newbornInfoQueryTask = new AsyncNewbornInfoUpdate(this);
            newbornInfoQueryTask.execute(deleteJson.toString(), SERVLET, ROOTKEY);
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Could not build delete ANC request");
            Utilities.printTrace(jse.getStackTrace());
        }
    }

    public void deleteLastNewborn(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliveryNewbornActivity.this).create();
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

    private boolean hasTheRequiredFileds() {

        RadioButton boy,girl,notDetected;

        boy = (RadioButton) findViewById(R.id.deliveryNewBornSon);
        girl=(RadioButton) findViewById(R.id.deliveryNewBornDaughter);
        notDetected=(RadioButton) findViewById(R.id.deliveryNewBornNotDetected);

        boolean allSelected= boy.isChecked() || girl.isChecked() || notDetected.isChecked();

        //TODO - there may not exist a village
        if(!allSelected) {
            Toast toast = Toast.makeText(this, R.string.NRCSaveWarning, Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();
            return false;
        }

        return true;
    }
}
