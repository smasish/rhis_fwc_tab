package org.sci.rhis.utilities;


import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jamil.zaman on 07/09/15.
 */
public class CustomTimePickerDialog implements TimePickerDialog.OnTimeSetListener{

    private EditText hourEditTextField;
    private EditText minuteEditTextField;
    private Spinner  amPm;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;

    public CustomTimePickerDialog(Context context) {
        calendar = Calendar.getInstance();
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date

        timePickerDialog = new TimePickerDialog(context, this, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false); //not 24 hour clock
    }

    @Override
    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
        //deliveryHour = selectedHour; deliveryMinute = selectedMinute;
        //EditText tpTime;
        hourEditTextField.setText(String.valueOf(selectedHour%12));
        minuteEditTextField.setText(String.valueOf(selectedMinute));
        if(selectedHour > 12) {
            amPm.setSelection(1, true);
        } else {
            amPm.setSelection(2, true);
        }
    }

    public void show(EditText hourField, EditText minuteField, Spinner amOrPm ) {
        hourEditTextField = hourField;
        minuteEditTextField = minuteField;
        amPm = amOrPm;
        timePickerDialog.show();
    }
}
