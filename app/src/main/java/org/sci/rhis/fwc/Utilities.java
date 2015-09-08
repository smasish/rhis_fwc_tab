package org.sci.rhis.fwc;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/30/2015.
 */
public class Utilities {
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
