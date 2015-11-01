package org.sci.rhis.fwc;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.sci.rhis.utilities.CustomDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class ClientInfoFragment extends Fragment implements OnClickListener {

    private ImageButton ib;
    private CustomDatePickerDialog datePickerDialog;
    private HashMap<Integer, EditText> datePickerPair;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_client_info, container, false);

        ib = (ImageButton) view.findViewById(R.id.Date_Picker_Button);
        ib.setOnClickListener(this);

        datePickerDialog = new CustomDatePickerDialog(getActivity(), new SimpleDateFormat("dd/MM/yyyy"));
        datePickerPair = new HashMap<Integer, EditText>();

        datePickerPair.put(R.id.Date_Picker_Button, (EditText) view.findViewById(R.id.lmpDate));

        //EditText eddEditText = (EditText) getActivity().findViewById(R.id.edd);




        return view;

    }
      @Override
    public void onClick(View v) {
               if(v.getId() == R.id.lmpDate || v.getId() == R.id.Date_Picker_Button) {
            datePickerDialog.show(datePickerPair.get(v.getId()));

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientInfo", "OnActivityResult");
    }
}
