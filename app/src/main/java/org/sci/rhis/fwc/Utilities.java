package org.sci.rhis.fwc;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
}
