package org.sci.rhis.fwc;

import android.util.Pair;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/28/2015.
 */
public abstract class ClinicalServiceActivity extends FWCServiceActivity {

    protected HashMap<String, CheckBox> jsonCheckboxMap;
    protected HashMap<String, CheckBox> jsonCheckboxGroupMap;
    protected HashMap<String, Spinner> jsonSpinnerMap;
    protected HashMap<String, MultiSelectionSpinner> jsonMultiSpinnerMap;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupButtonMap;
    protected HashMap<String, EditText> jsonEditTextMap;
    protected HashMap<String, EditText> jsonEditTextRetrieveMap;
    protected HashMap<String, EditText> jsonEditTextDateMap;
    protected HashMap<String, EditText> jsonEditTextDatePlusFormatMap;
    protected HashMap<String, TextView> jsonTextViewsMap;

    @Override
    public abstract void callbackAsyncTask(String result);


    protected void initialize() {
        //populate checkboxes
        jsonCheckboxMap = new HashMap<>();
        initiateCheckboxes();

        jsonCheckboxGroupMap = new HashMap<>();
        initiateCheckboxesGroup();

        //populate Spinners
        jsonSpinnerMap = new HashMap<String, Spinner>();
        initiateSpinners();

        //populate Spinners
        jsonMultiSpinnerMap = new HashMap<String, MultiSelectionSpinner>();
        initiateMultiSelectionSpinners();


        //populate RadioGroupButtons
        jsonRadioGroupButtonMap = new HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>>();
        initiateRadioGroups();

        //populate EditTexts
        jsonEditTextMap = new HashMap<String, EditText>();
        initiateEditTexts();

        //populate TextViews
        jsonTextViewsMap = new HashMap<String, TextView>();
        initiateTextViews();

        //populate EditTextsDate
        jsonEditTextDateMap = new HashMap<String, EditText>();
        initiateEditTextDates();

        //populate EditTextsDate(edd)PlusFormat
        jsonEditTextDatePlusFormatMap = new HashMap<String, EditText>();
        initiateEditTextDatesPlusFormat();

    }


    protected abstract void initiateCheckboxes();
    protected abstract void initiateEditTexts();
    protected abstract void initiateTextViews();
    protected abstract void initiateSpinners();
    protected abstract void initiateMultiSelectionSpinners();
    protected abstract void initiateEditTextDates();
    protected void initiateEditTextDatesPlusFormat() {};
    protected void initiateCheckboxesGroup() {};
    protected abstract void initiateRadioGroups();

    public void RetriveHistory() {

    }

    public void DisplayHistory() {

    }

    public void Record() {

    }

    public void Update () {

    }
}
