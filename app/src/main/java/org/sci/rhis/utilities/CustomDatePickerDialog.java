package org.sci.rhis.utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jamil.zaman on 9/2/2015.
 */
public class CustomDatePickerDialog implements DatePickerDialog.OnDateSetListener {
    /*public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }*/

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    EditText editTextFielId;

    public CustomDatePickerDialog(Context context,  int year, int monthOfYear, int dayOfMonth) {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        datePickerDialog = new DatePickerDialog(context, this, year, monthOfYear, dayOfMonth);
        editTextFielId = null;
    }

    public CustomDatePickerDialog(Context context) {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        datePickerDialog = new DatePickerDialog(
                context,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        editTextFielId = null;
    }

    public CustomDatePickerDialog(Context context, SimpleDateFormat simpleDateFormat) {
        calendar = Calendar.getInstance();
        dateFormat = simpleDateFormat;
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        datePickerDialog = new DatePickerDialog(
                context,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        editTextFielId = null;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        if(editTextFielId != null) {//if something is set
            editTextFielId.setText(dateFormat.format(calendar.getTime()));
        }
        editTextFielId = null;
    }

    public void show(EditText editTextFiedId ) {
        editTextFielId = editTextFiedId;
        datePickerDialog.show();
    }
}
