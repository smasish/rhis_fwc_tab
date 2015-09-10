package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/30/2015.
 */
public class Utilities {

 /**
   public static void DisableTextFields(Activity activity, int id) {
        ViewGroup group = (ViewGroup)activity.findViewById(id);
        for (int i = 0,  count = group != null ? group.getChildCount(): 0; i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                //((EditText)view).setText("");//here it will be clear all the EditText field
                ((EditText)view).setFocusable(false);
            }
        }
    }
*/

    // This method added by Al Amin on 10/09/2015 (dd/MM/yyyy)
    public static void Disable(Activity activity, int id) {

        ViewGroup testgroup = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; i <count; i++) {
            View view = testgroup.getChildAt(i);
            if(view instanceof LinearLayout) {
                Disable(activity, view.getId());
            }
            else if (view instanceof EditText ||
                     view instanceof CheckBox ||
                     view instanceof RadioButton ||
                     view instanceof Spinner)
            {
                (view).setFocusable(false);
                (view).setEnabled(false);
                
                //( view).setCursorVisible(false);
                //(view).setKeyListener(null);
            }
     /*
            else if (view instanceof CheckBox) {
                ((CheckBox) view).setFocusable(false);
                ((CheckBox) view).setEnabled(false);
                ((CheckBox) view).setCursorVisible(false);
                ((CheckBox) view).setKeyListener(null);
            }
            else if (view instanceof RadioButton) {
                ((RadioButton) view).setFocusable(false);
                ((RadioButton) view).setEnabled(false);
                ((RadioButton) view).setCursorVisible(false);
                ((RadioButton) view).setKeyListener(null);
            }
      */


            else {
                System.out.print(testgroup);
            }
        }
    }

    public static void updateSpinners(HashMap<String,Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    spinner.setSelection((json.getInt(key) - 1));
                }
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void updateSpinners(HashMap<String,Spinner> keyMap, JSONObject json, Context context) {
        Spinner spinner;
        String compareValue;

        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.Blood_Group_Dropdown, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    compareValue = json.getString(key);
                    if (!compareValue.equals(null)) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        spinner.setSelection(spinnerPosition);
                    }
                }
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }


    }

    public static void updateCheckboxes(HashMap<String,CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setChecked((json.getInt(key) == 1));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void updateEditTexts(HashMap<String,EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void updateEditTextDates(HashMap<String,EditText> keyMap, JSONObject json) {

        SimpleDateFormat iformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat oformat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                currentDate = json.getString(key);
                if(!currentDate.equals("")) {
                    keyMap.get(key).setText(oformat.format(iformat.parse(json.getString(key))));
                }
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            } catch (ParseException pe) {
                System.out.println("Parsing Exception: Could not parse date");
            }
        }
    }


}
