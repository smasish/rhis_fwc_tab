package org.sci.rhis.fwc;

import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by jamil.zaman on 8/29/2015.
 */
public abstract class FWCServiceActivity extends AppCompatActivity implements AsyncCallback{
    protected CheckBox getCheckbox(int id) {
        return (CheckBox)findViewById(id);
    }

    protected Spinner getSpinner(int id) {
        return (Spinner)findViewById(id);
    }

    protected RadioGroup getRadioGroup(int id) {
        return (RadioGroup)findViewById(id);
    }

    protected RadioButton getRadioButton(int id) {
        return (RadioButton)findViewById(id);
    }

    protected EditText getEditText(int id) {
        return (EditText)findViewById(id);
    }

    protected TextView getTextView(int id) {
        return (TextView)findViewById(id);
    }
}
