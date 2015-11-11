package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/30/2015.
 */
public class Utilities {

    public final static int SPINNER_INDEX_OFFSET = 0; //no offset is required
    private final static String LOGTAG = "FWC-UTILITIES";

    // This method added by Al Amin on 10/09/2015 (dd/MM/yyyy)
    public static void Disable(Activity activity, int id) {

        ViewGroup testgroup = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; i <count; i++) {
            View view = testgroup.getChildAt(i);

            if(view instanceof LinearLayout) {
                Disable(activity, view.getId());
            }

            else if (view instanceof CheckBox) {

                view.setClickable(false);
                view.setEnabled(false);
            }

            else if (view instanceof Spinner) {

                (view).setClickable(false);
                (view).setEnabled(false);
            }

            else if (view instanceof RadioButton) {

                view.setClickable(false);
                view.setEnabled(false);
             }

            else if (view instanceof ImageButton) {

                (view).setClickable(false);
                (view).setEnabled(false);
            }

            else if (view instanceof Button) {

                view.setEnabled(false);
                (view).setClickable(false);
            }

            else if (view instanceof EditText )
            {
                if(view.getId()==R.id.Clients_House_No || view.getId()==R.id.Clients_Mobile_no);
                else
                {   (view).setFocusable(false);
                    (view).setEnabled(false);
                }
            }

            else if (view instanceof TextView)
            {
                (view).setFocusable(false);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void MakeInvisible(Activity activity, int id)
    {
        ViewGroup visibility = (ViewGroup)activity.findViewById(id);
        visibility.setVisibility(View.GONE);
    }
    public static void MakeVisible(Activity activity, int id)
    {
        ViewGroup visibility = (ViewGroup)activity.findViewById(id);
        visibility.setVisibility(View.VISIBLE);
    }

    public static void VisibleButton(Activity activity,int id)
    {
        Button visibility = (Button)activity.findViewById(id);
        visibility.setVisibility(Button.VISIBLE);
    }

    public static void InVisibleButton(Activity activity,int id)
    {
        Button visibility = (Button)activity.findViewById(id);
        visibility.setVisibility(Button.GONE);
    }

    public static void VisibleLayout(Activity activity,int id)
    {
        LinearLayout visibility = (LinearLayout)activity.findViewById(id);
        visibility.setVisibility(View.VISIBLE);
    }

    public static void InVisibleLayout(Activity activity,int id)
    {
        LinearLayout visibility = (LinearLayout)activity.findViewById(id);
        visibility.setVisibility(View.GONE);
    }

    public static void Reset(Activity activity, int id) {

        ViewGroup testgroup = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; i <count; i++) {
            View view = testgroup.getChildAt(i);

            if(view instanceof LinearLayout) {
                Reset(activity, view.getId());
            }

            else if (view instanceof CheckBox) {

                ((CheckBox) view).setChecked(false);
                view.setClickable(true);
                view.setEnabled(true);
            }
            else if (view instanceof Spinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof RadioButton) {

                ((RadioButton) view).setChecked(false);
                (view).setClickable(true);
                (view).setEnabled(true);
             }

            else if (view instanceof ImageButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof Button) {

                (view).setEnabled(true);
                (view).setClickable(true);
            }

            else if (view instanceof EditText ) {

                if(view.getId()==R.id.Clients_House_No || view.getId()==R.id.Clients_Mobile_no);
                else
                {
                    (view).setFocusable(true);
                    (view).setFocusableInTouchMode(true);
                    (view).setEnabled(true);
                    ((EditText)view).setText("");
                }
            }

            else if (view instanceof TextView) {

                (view).setFocusable(true);
                (view).setFocusableInTouchMode(true);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void Enable(Activity activity, int id) {

        ViewGroup testgroup = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; i <count; i++) {
            View view = testgroup.getChildAt(i);

            if(view instanceof LinearLayout) {
                Enable(activity, view.getId());
            }

            else if (view instanceof CheckBox) {

                view.setClickable(true);
                view.setEnabled(true);
            }
            else if (view instanceof Spinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof RadioButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof ImageButton) {

                (view).setClickable(true);
                (view).setEnabled(true);
            }

            else if (view instanceof Button) {

                (view).setEnabled(true);
                (view).setClickable(true);
            }

            else if (view instanceof EditText ) {

                if(view.getId()==R.id.Clients_House_No || view.getId()==R.id.Clients_Mobile_no);
                else
                {
                    (view).setFocusable(true);
                    (view).setFocusableInTouchMode(true);
                    (view).setEnabled(true);
                }
            }

            else if (view instanceof TextView) {

                (view).setFocusable(true);
                (view).setFocusableInTouchMode(true);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void EnableField(Activity activity,int id,String type)
    {
        EditText e = (EditText)activity.findViewById(id);

        e.setFocusableInTouchMode(true);
        e.setFocusable(true);
        e.setEnabled(true);
        if(type=="reset")
            (e).setText("");
    }

    public static void DisableField(Activity activity,int id)
    {
        EditText e = (EditText)activity.findViewById(id);

        e.setFocusable(false);
        e.setEnabled(false);
    }

    public static void getSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, String.valueOf(spinner.getSelectedItemPosition() + SPINNER_INDEX_OFFSET));
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //Get values instead of indices from spinner
    public static void getSpinnerValues(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, "\"" + String.valueOf(spinner.getSelectedItem()) + "\"");
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key+ "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //Get values instead of indices from spinner
    public static void getSpinnersValuesWithoutSlash(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, String.valueOf(spinner.getSelectedItem()) );
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //TODO: Error prone -> only handles keys related to treatment modules, will get NullPointer Exception if the key does not exist.
    public static JSONObject getMultiSelectSpinnerIndices( HashMap<String, MultiSelectionSpinner> treatmentSpinnerMap, JSONObject json) {
        for (String key: treatmentSpinnerMap.keySet()) {
            try {
                json.put(key,
                        "[\"" + TextUtils.join("\",\"",
                            treatmentSpinnerMap.get(key).getSelectedIndicesInText(SPINNER_INDEX_OFFSET))
                        + "\"]");
            } catch (JSONException JSE) {
                Log.e("Utilities", "JSON Exception:\n" + JSE.toString());
            } catch (NullPointerException NPE) {
                Log.e("Utilities", "Key does not exist in map: " + key + "\n" +
                        "NullPointerException Exception:\n" + NPE.toString());
            }
        }
        return json;
    }

    //update by position
    public static void setMultiSelectSpinners(HashMap<String, MultiSelectionSpinner> keyMap, JSONObject json) {
        MultiSelectionSpinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    String value = json.getString(key);
                    if(!value.equals("[\"\"]")) {
                        String keyStr[] = value.replaceAll("(\"|\\[|\\])", "").split(",");
                        int values[] = new int[keyStr.length];
                        for (int i = 0; i < keyStr.length; ++i) {
                            values[i] = Integer.valueOf(keyStr[i]);
                        }

                        spinner.setSelection(values);
                    }
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (NumberFormatException nfe) {
                Log.e(LOGTAG, "Could not convert value for key: '" + key + "' JSON:\n\t{"+ json.toString() +"}\n\t" + nfe.getStackTrace());
            }
        }
    }

    //update by position
    public static void setSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    spinner.setSelection((json.getInt(key)));
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    //Update by value
    public static void setSpinners(HashMap<String, Pair<Spinner, Integer>> keyMap, JSONObject json, Context context) {
        Spinner spinner;
        String compareValue;

        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key).first;
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, keyMap.get(key).second, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    compareValue = json.getString(key);
                    if (!compareValue.equals(null)) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        spinner.setSelection(spinnerPosition);
                    }
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void setCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setChecked((json.getString(key).equals("1")));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    public static void setTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    public static void getCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : 2));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    public static void getCheckboxesBlank(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : ""));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }
    /*
    public static void getCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : 2));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }*/

    public static void getEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setText(json.getString(key));
                json.put(key, (keyMap.get(key).getText()));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void setEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            }
        }
    }

    public static void setEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                currentDate = json.getString(key);
                if(!currentDate.equals("")) {
                    Date k=dbFormat.parse(currentDate);
                    String v=uiFormat.format(k);
                    keyMap.get(key).setText(v);
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                System.out.println("Parsing Exception: Could not parse date");
            }
        }
    }

    public static void getEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

        SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                currentDate = (keyMap.get(key).getText()).toString();
                if(!currentDate.equals("")) {
                    Date k=uiFormat.parse(currentDate);
                    String v=dbFormat.format(k);
                    json.put(key, v);
                }
                json.put(key, dbFormat.format(uiFormat.parse(keyMap.get(key).getText().toString())));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                Log.e(LOGTAG, "Parsing Exception: Could not parse date:\n" + pe.toString());
            } catch (NullPointerException NP) {
                Log.e(LOGTAG, "Parse:\n\t" + NP.getMessage());
            }
        }
    }

    public static void getRadioGroupButtons(HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {

                String value = "";
                if(keyMap.get(key).first.getCheckedRadioButtonId() == keyMap.get(key).second.first.getId()) {
                    value = "1";
                } else if (keyMap.get(key).first.getCheckedRadioButtonId() == keyMap.get(key).second.second.getId()) {
                    value = "2";
                }
                json.put(key, value);

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e("Null Pointer", NP.getMessage());
            }
        }
    }

    public static void setRadioGroupButtons(HashMap<String, Pair<RadioGroup, Pair<RadioButton,RadioButton>>> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {

                if (json.getString(key).equals("1")) {
                    keyMap.get(key).first.check(keyMap.get(key).second.first.getId());

                } else if (json.getString(key).equals("2")) {
                    keyMap.get(key).first.check(keyMap.get(key).second.second.getId());
                }

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (NullPointerException NP) {
                Log.e(LOGTAG, NP.getMessage());
            }
        }
    }

    public static Date addDateOffset(Date given, int days) {
        Calendar edd_cal = Calendar.getInstance();
        edd_cal.setTime(given);

        edd_cal.add(Calendar.DATE, days);
        return edd_cal.getTime();
    }
}
