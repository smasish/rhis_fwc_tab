package org.sci.rhis.fwc;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;


public class ClientInfoFragment extends Fragment implements OnClickListener {

    private ImageButton ib;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    private EditText et;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_info,
                container, false);

        ib = (ImageButton) view.findViewById(R.id.Date_Picker_Button);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        et = (EditText) view.findViewById(R.id.Set_Last_LMP_Date);
        ib.setOnClickListener(this);

        Spinner staticSpinner = (Spinner)  view.findViewById(R.id.ClientsPrivious_History_Dropdown);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.ClientsPrivious_History_Dropdown,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        return view;

    }

    @Override
    public void onClick(View v) {
        //showDialog(0);
    }

   // @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            et.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

}
