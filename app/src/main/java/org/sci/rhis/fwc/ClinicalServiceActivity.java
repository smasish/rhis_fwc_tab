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
    protected HashMap<String, Spinner> jsonSpinnerMap;
    protected HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> jsonRadioGroupButtonMap;
    protected HashMap<String, EditText> jsonEditTextMap;
    protected HashMap<String, EditText> jsonEditTextDateMap;
  //  protected HashMap<String, EditText> jsonEditTextDateMap2;
    protected HashMap<String, TextView> jsonTextViewsMap;

    @Override
    public abstract void callbackAsyncTask(String result);


    protected void initialize() {
//populate checkboxes
        jsonCheckboxMap = new HashMap<>();
        initiateCheckboxes();

        //populate Spinners
        jsonSpinnerMap = new HashMap<String, Spinner>();
        initiateSpinners();

        //populate RadioGroupButtons
        jsonRadioGroupButtonMap = new HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>>();
        initiateRadioGroups();

        //populate EditTexts
        jsonEditTextMap = new HashMap<String, EditText>();
        initiateEditTexts();

        //populate TextViews
        jsonTextViewsMap = new HashMap<String, TextView>();
        initiateTextViews();

        //populate EditTexts
        jsonEditTextDateMap = new HashMap<String, EditText>();
        initiateEditTextDates();

     //   jsonEditTextDateMap2 = new HashMap<String, EditText>();
     //   initiateEditTextDates();

    }


    protected abstract void initiateCheckboxes();
    protected abstract void initiateEditTexts();
    protected abstract void initiateTextViews();
    protected abstract void initiateSpinners();
    protected abstract void initiateEditTextDates();
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
