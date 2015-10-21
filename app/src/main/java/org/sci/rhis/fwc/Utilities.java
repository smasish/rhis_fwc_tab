package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Context;
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
import java.util.HashMap;

/**
 * Created by jamil.zaman on 8/30/2015.
 */
public class Utilities {

    // This method added by Al Amin on 10/09/2015 (dd/MM/yyyy)
    public static void Disable(Activity activity, int id) {

        ViewGroup testgroup = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = testgroup != null ? testgroup.getChildCount(): 0; i <count; i++) {
            View view = testgroup.getChildAt(i);

            if(view instanceof LinearLayout) {
                Disable(activity, view.getId());
            }
            else if (view instanceof EditText )
            {
                (view).setFocusable(false);
               // (view).setEnabled(false);
            }
            else if (view instanceof ImageButton)
            {
                (view).setClickable(false);

            }
            else if (view instanceof Button)
            {
                (view).setClickable(false);

            }
            else if (view instanceof CheckBox) {

                ((CheckBox) view).setCursorVisible(false);
                ((CheckBox) view).setKeyListener(null);
            }
            else if (view instanceof RadioButton) {

                ((RadioButton) view).setCursorVisible(false);
                ((RadioButton) view).setKeyListener(null);
            }
            else if (view instanceof Spinner) {

                ((Spinner) view).setClickable(false);
                //((Spinner) view).setKeyListener(null);
            }

            else {
                System.out.print(testgroup);
            }
        }
    }
public static void Visibility(Activity activity,int id)
{
    ViewGroup visibility = (ViewGroup)activity.findViewById(id);
    int view =visibility.getId();

            visibility.setVisibility(View.GONE);
}

    public static void Enable(Activity activity, int id) {

        ViewGroup group = (ViewGroup)activity.findViewById(id);
        for(int i = 0, count = group != null ? group.getChildCount(): 0; i <count; i++) {
            View view = group.getChildAt(i);

            if(view instanceof LinearLayout) {
                Disable(activity, view.getId());
            }
            else if (view instanceof EditText )
            {
                (view).setFocusable(true);
                ((EditText)view).setText("");
            }

            else if (view instanceof CheckBox) {

                ((CheckBox) view).setCursorVisible(true);

            }
            else if (view instanceof RadioButton) {

                ((RadioButton) view).setCursorVisible(true);

            }
            else if (view instanceof Spinner) {

                ((Spinner) view).setClickable(true);

            }

            else {
                System.out.print(group);
            }
        }
    }

    public static void getSpinners(HashMap<String, Spinner> keyMap, JSONObject json) {
        Spinner spinner;
        for (String key: keyMap.keySet()) {
            try {
                spinner = keyMap.get(key);
                if(spinner != null) {
                    //spinner.setSelection((json.getInt(key) - 1));
                    json.put(key, String.valueOf(spinner.getSelectedItemPosition() + 1));
                }
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
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
                keyMap.get(key).setChecked((json.getString(key).equals("1")));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            }
        }
    }
    public static void setTextViews(HashMap<String, TextView> keyMap, JSONObject json) {
        for (String key: keyMap.keySet()) {
            try {
                keyMap.get(key).setText(json.getString(key));
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
        SimpleDateFormat oformat = new SimpleDateFormat("yyyy-MM-dd");
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

        SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate;
        for (String key: keyMap.keySet()) {
            try {
                /*currentDate = json.getString(key);
                if(!currentDate.equals("")) {
                    keyMap.get(key).setText(oformat.format(iformat.parse(json.getString(key))));
                }*/
                json.put(key, dbFormat.format(uiFormat.parse(keyMap.get(key).getText().toString())));
            } catch (JSONException jse) {
                System.out.println("The JSON key: '" + key+ "' does not exist");
            } catch (ParseException pe) {
                System.out.println("Parsing Exception: Could not parse date:\n" + pe.toString());
            } catch (NullPointerException NP) {
                Log.i("Parse", NP.getMessage());
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
                Log.i("Utility", "'" + key + "' does not exist");
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
                Log.i("Utility",  "'"+ key + "' does not exist");
            } catch (NullPointerException NP) {
                Log.e("Null Pointer", NP.getMessage());
            }
        }
    }
}
