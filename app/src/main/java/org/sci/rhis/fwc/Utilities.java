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
import android.widget.Toast;

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

    public static void SetVisibility(Activity activity, int id, int visibity) {
        switch (visibity) {
            case View.VISIBLE:
                MakeVisible(activity, id);
                break;
            case View.INVISIBLE:
                Reset(activity, id);
                MakeInvisible(activity, activity.findViewById(id), visibity);
                break;
            case View.GONE:
                Reset(activity, id);
                MakeInvisible(activity, id);
                break;
        }
    }

    public static void Disable(Activity activity, int id) {
        Disable(activity, activity.findViewById(id));
    }

    public static void Disable(Activity activity, View view) {

        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            view = testgroup != null ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout  || //LinearLayout is also view group so exclude it
               ((view instanceof  ViewGroup) && !(view instanceof  Spinner))) {
                Disable(activity, view);
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
        MakeInvisible(activity, activity.findViewById(id), View.GONE);
    }

    public static void MakeInvisible(Activity activity, View view, int visibility)
    {
        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            View child = testgroup.getChildAt(i);

            if(child instanceof LinearLayout || //LinearLayout is also view group so exclude it
            ((child instanceof  ViewGroup) && !(child instanceof  Spinner))) {
                MakeInvisible(activity, child, visibility);
                Disable(activity, child);
            }
            child.setVisibility(visibility);
        }
        view.setVisibility(visibility);
        /////
    }
    public static void MakeVisible(Activity activity, int id)
    {
       MakeVisible(activity, activity.findViewById(id));
    }

    public static void MakeVisible(Activity activity, View view)
    {
        ViewGroup testgroup = null;
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
            View child = testgroup.getChildAt(i);

            if(view instanceof  LinearLayout || //LinearLayout is also view group so exclude it
                    ((child instanceof  ViewGroup) && !(child instanceof  Spinner))) {
                MakeVisible(activity, child);
                Enable(activity, child);
            }
            child.setVisibility(View.VISIBLE);
        }
        view.setVisibility(View.VISIBLE);
    }

    /*public static void VisibleButton(Activity activity,int id)
    {
        Button visibility = (Button)activity.findViewById(id);
        visibility.setVisibility(Button.VISIBLE);
    }

    public static void InVisibleButton(Activity activity,int id)
    {
        Button visibility = (Button)activity.findViewById(id);
        visibility.setVisibility(Button.GONE);
    }*/

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
        Reset(activity, activity.findViewById(id));
    }

    public static void Reset(Activity activity, View view) {

        ViewGroup testgroup = null;
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; i <count; i++) {
            view = testgroup != null ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout) {
                Reset(activity, view.getId());
            }

            else if (view instanceof CheckBox) {

                ((CheckBox) view).setChecked(false);
                view.setClickable(true);
                view.setEnabled(true);
            }
            else if (view instanceof MultiSelectionSpinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
                ((MultiSelectionSpinner) view).setSelection(new int[]{});
            }
            else if (view instanceof Spinner) {

                (view).setClickable(true);
                (view).setEnabled(true);
                ((Spinner) view).setSelection(0);
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
        Enable(activity, activity.findViewById(id));
    }

    public static void Enable(Activity activity, View view) {

        ViewGroup testgroup = null;
        //View view  = activity.findViewById(id);
        if(view instanceof ViewGroup) { //if not a layout but single button is passed
            testgroup  = (ViewGroup) view;
        }

        for( int i = 0, count = testgroup != null ? testgroup.getChildCount(): 1; //if not a viewgroup only 1 item
             i <count && view != null; i++) {
             view = testgroup != null ? testgroup.getChildAt(i) : view;

            if(view instanceof LinearLayout || //LinearLayout is also view group so exclude it
               ((view instanceof  ViewGroup) && !(view instanceof  Spinner))) {
                Enable(activity, view);
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
        else if(type=="edit")
            //do nothing
            ;
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
                printTrace(jse.getStackTrace());
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
                printTrace(jse.getStackTrace());
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
                printTrace(jse.getStackTrace());
            }
        }
    }

    public static void setEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
                printTrace(jse.getStackTrace());
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
                    Date date = dbFormat.parse(currentDate);
                    String dateStr = uiFormat.format(date);
                    keyMap.get(key).setText(dateStr);
                }
            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                Log.e(LOGTAG, "Parsing Exception: Could not parse date:"
                        + " Key: "+ key + " "
                        + keyMap.get(key).getText().toString());
                StackTraceElement ste [] = pe.getStackTrace();
                for(int i = 0; i< 9; i++) {
                    Log.e(LOGTAG, ste[i].toString());
                }
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
                    /*Date date = uiFormat.parse(currentDate);
                    String dateStr = dbFormat.format(date);
                    json.put(key, dateStr);*/
                    json.put(key, dbFormat.format(uiFormat.parse(currentDate)));
                }

            } catch (JSONException jse) {
                Log.e(LOGTAG, "The JSON key: '" + key + "' does not exist\n\t" + jse.getStackTrace());
            } catch (ParseException pe) {
                Log.e(LOGTAG, "Parsing Exception: Could not parse date:"
                        + " Key: " + key + " "
                        + keyMap.get(key).getText().toString());
                StackTraceElement ste [] = pe.getStackTrace();
                for(int i = 0; i< 9; i++) {
                    Log.e(LOGTAG, ste[i].toString());
                }
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
                printTrace(jse.getStackTrace());
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
                printTrace(jse.getStackTrace());
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

    public static void printTrace(StackTraceElement ste []) {
        printTrace(ste, 5); //default to first 3 lines
    }

    public static void printTrace(StackTraceElement ste [], int level) {
        //
        for(int i = 0; i< level; i++) {
            Log.e(LOGTAG, ste[i].toString());
        }
    }



    public static String ConvertNumberToBangla2(String givenNumber) throws NumberFormatException {

        char[] numberMap = {'0','1','2','3','4','5','6','7','8','9'}; //first 40 byte is wastage but its acceptable
        char[] banglaMap = {'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        String banglaResponse = "";

        for(int i = 0; i< givenNumber.length(); i++) {
            switch(givenNumber.charAt(i)) {
                case '0':
                    banglaResponse += banglaMap[0];
                break;
                case '1':
                    banglaResponse += banglaMap[1];
                    break;
                case '2':
                    banglaResponse += banglaMap[2];
                    break;
                case '3':
                    banglaResponse += banglaMap[3];
                    break;
                case '4':
                    banglaResponse += banglaMap[4];
                    break;
                case '5':
                    banglaResponse += banglaMap[5];
                    break;
                case '6':
                    banglaResponse += banglaMap[6];
                    break;
                case '7':
                    banglaResponse += banglaMap[7];
                    break;
                case '8':
                    banglaResponse += banglaMap[8];
                    break;
                case '9':
                    banglaResponse += banglaMap[9];
                    break;
                default:
                        throw new NumberFormatException("Character:" + givenNumber.charAt(i) + " is not convertible to bangla");

            }
        }

        return banglaResponse;
    }

    public static String ConvertNumberToBangla(String givenNumber) {

        char[] numberMap = {'0','1','2','3','4','5','6','7','8','9'}; //first 40 byte is wastage but its acceptable
        char[] banglaMap = {'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        String banglaResponse = "";

        for(int i = 0; i< givenNumber.length(); i++) {
            try {
                banglaResponse += banglaMap[givenNumber.charAt(i) - 48];
            } catch (ArrayIndexOutOfBoundsException aiob) {
                if(givenNumber.charAt(i) > 57 || givenNumber.charAt(i) < 48) {
                       banglaResponse += givenNumber.charAt(i);
                } else {
                    Log.e(LOGTAG, "Number out of range:" + givenNumber);
                }
            }
        }

        return banglaResponse;
    }

    public static void showBiggerToast(Context context, int stringId ) {
        Toast toast = Toast.makeText(context, stringId, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(20);
        toast.show();
    }
}
