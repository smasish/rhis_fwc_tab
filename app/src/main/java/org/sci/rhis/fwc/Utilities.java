package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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

    //update by position
    public static void setSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
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
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void setCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setChecked((json.getInt(key) == 1));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void getCheckboxes(HashMap<String, CheckBox> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setChecked((json.getInt(key) == 1));
                json.put(key, (keyMap.get(key).isChecked() ? 1 : 2));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void getEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                //keyMap.get(key).setText(json.getString(key));
                json.put(key, (keyMap.get(key).getText()));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void setEditTexts(HashMap<String, EditText> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }

    public static void setEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

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

    public static void getEditTextDates(HashMap<String, EditText> keyMap, JSONObject json) {

        SimpleDateFormat iformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat oformat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                /*currentDate = json.getString(key);
                if(!currentDate.equals("")) {
                    keyMap.get(key).setText(oformat.format(iformat.parse(json.getString(key))));
                }*/
                json.put(key, iformat.format(oformat.parse(keyMap.get(key).getText().toString())));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            } catch (ParseException pe) {
                System.out.println("Parsing Exception: Could not parse date");
            } catch (NullPointerException NP) {
                Log.i("Parse", NP.getMessage());
            }
        }
    }
}
