package org.sci.rhis.fwc;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.CustomDatePickerDialog;
import org.sci.rhis.utilities.CustomTimePickerDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.valueOf;

public class DeliveryNewbornActivity extends ClinicalServiceActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;

    final private String servlet = "newborn";
    final private String rootkey = "newbornInfo";
    private PregWoman mother;

    private ProviderInfo provider;

    AsyncNewbornInfoUpdate newbornInfoQueryTask;
    AsyncNewbornInfoUpdate newbornInfoUpdateTask;

    private MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_newborn);
        // Remove Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Spinner referSpinner= (Spinner)findViewById(R.id.deliveryChildReferCenterNameSpinner);

        final List<String> newbornreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});

        getCheckbox(R.id.deliveryChildReferCheckBox).setOnCheckedChangeListener(this);
        buildQueryHeader();

        //create the mother
        mother = getIntent().getParcelableExtra("PregWoman");

        provider = getIntent().getParcelableExtra("Provider");
     //get info from database
        String queryString = "";
/*

        try {
            queryString = buildQueryHeader(true).toString();
            queryString = buildQueryHeader();
            Log.e("Newborn", "build query String: " + "working properly");
        } catch (JSONException JSE) {
            Log.e("Newborn", "Could not build query String: " + JSE.getMessage());
        }
        */
       newbornInfoQueryTask = new AsyncNewbornInfoUpdate(this);
        newbornInfoQueryTask.execute(queryString, servlet, rootkey);

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
        jsonEditTextMap.put("weight",getEditText(R.id.deliveryNewBornWeightValue));
    }

    @Override
    protected void initiateTextViews() {

    }

    @Override
    protected void initiateSpinners() {
        // for New born Layout
        jsonSpinnerMap.put("referCenterName", getSpinner(R.id.deliveryChildReferCenterNameSpinner));
        jsonSpinnerMap.put("referReason", getSpinner(R.id.deliveryChildReferReasonSpinner));
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
                                getRadioButton(R.id.deliveryNewBornDaughter))
                )
        );
        jsonRadioGroupButtonMap.put("immatureBirth", Pair.create(
                        getRadioGroup(R.id.id_newBornImmaturRadioGroup), Pair.create(
                                getRadioButton(R.id.deliveryPrematureBirthYesButton),
                                getRadioButton(R.id.deliveryPrematureBirthNoButton))
                )
        );

        jsonRadioGroupButtonMap.put("drying", Pair.create(
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
            getTextView(R.id.deliveryChildReferCenterNameLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferCenterNameSpinner).setVisibility(visibility);
            getTextView(R.id.deliveryChildReferReasonLabel).setVisibility(visibility);
            getSpinner(R.id.deliveryChildReferReasonSpinner).setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_saveNewbornButton) {
            newbornsaveToJson();
            Log.e("Newborn", "Saved Newborn Successfully?");

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void newbornsaveToJson() {

        newbornInfoUpdateTask = new AsyncNewbornInfoUpdate(this);
        JSONObject json;
        try {
            json = buildQueryHeader(false);
            Utilities.getCheckboxes(jsonCheckboxMap, json);
            Utilities.getEditTexts(jsonEditTextMap, json);
            Utilities.getSpinners(jsonSpinnerMap, json);
            Utilities.getRadioGroupButtons(jsonRadioGroupButtonMap, json);
            newbornInfoUpdateTask.execute(json.toString(),servlet,rootkey);
        } catch (JSONException jse) {

            Log.d("Newborn", "JSON Exception: " + jse.getMessage());
        }

    }
    private String buildQueryHeader()
    {
        Log.e("Hello", "World");

        return null;
    }
    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        Log.e("Newborn", "boolean Value is found " + valueOf(isRetrieval));

        //get info from database
        String queryString =   "{" +
                "healthid:" + mother.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+ valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + mother.getPregNo() + "," +
                "newbornLoad:" + (isRetrieval? "retrieve":"\"\"") + "outcomeplace" +
                "}";

        return new JSONObject(queryString);
    }
}
