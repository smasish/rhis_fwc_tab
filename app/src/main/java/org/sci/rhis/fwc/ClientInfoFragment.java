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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private  final String LOGTAG    = "FWC-CLIENT-INFO";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_client_info, container, false);

        ib = (ImageButton) view.findViewById(R.id.Date_Picker_Button);
        ib.setOnClickListener(this);

        datePickerDialog = new CustomDatePickerDialog(getActivity(), new SimpleDateFormat("dd/MM/yyyy"));
        datePickerPair = new HashMap<Integer, EditText>();

        datePickerPair.put(R.id.Date_Picker_Button, (EditText) view.findViewById(R.id.lmpDate));
        datePickerPair.put(R.id.Clients_TT_Tika1, (EditText)view.findViewById(R.id.ttDate1));
        datePickerPair.put(R.id.Clients_TT_Tika2, (EditText)view.findViewById(R.id.ttDate2));
        datePickerPair.put(R.id.Clients_TT_Tika3, (EditText)view.findViewById(R.id.ttDate3));
        datePickerPair.put(R.id.Clients_TT_Tika4, (EditText)view.findViewById(R.id.ttDate4));
        datePickerPair.put(R.id.Clients_TT_Tika5, (EditText)view.findViewById(R.id.ttDate5));

        CheckBox ttArray [] = {
            getCheckbox(view, R.id.Clients_TT_Tika1),
            getCheckbox(view, R.id.Clients_TT_Tika2),
            getCheckbox(view, R.id.Clients_TT_Tika3),
            getCheckbox(view, R.id.Clients_TT_Tika4),
            getCheckbox(view, R.id.Clients_TT_Tika5)
        };

        for (int i = 0 ; i < ttArray.length; i++ ) {
            //ttArray[i].setOnCheckedChangeListener(this);
            ttArray[i].setOnClickListener(this);
        }

        //EditText eddEditText = (EditText) getActivity().findViewById(R.id.edd);
        setListeners(view);
        return view;

    }

    private void setListeners(final View view) {

        final EditText para = (EditText)view.findViewById(R.id.para);

        final int zeroInitList[] = {R.id.SonNum, R.id.DaughterNum, R.id.lastChildYear, R.id.lastChildMonth};

        para.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");

                    try {
                        String paraStr = para.getText().toString();
                        if(!paraStr.equals("")) {
                            int value = Integer.valueOf(paraStr);

                            for (int id:zeroInitList) {
                                ((EditText)view.findViewById(id)).setText(String.valueOf(0));
                            }

                            Utilities.SetVisibility(getActivity(), R.id.born_blood, (value == 0) ? View.GONE : View.VISIBLE);
                            Utilities.SetVisibility(getActivity(), R.id.age_lasr_child_height, (value == 0) ? View.GONE : View.VISIBLE);
                            Utilities.SetVisibility(getActivity(), R.id.Previous_Delivery, (value == 0) ? View.GONE : View.VISIBLE);
                        }

                    } catch (NumberFormatException NFE) {
                        Log.e(LOGTAG, NFE.getMessage());
                        Utilities.printTrace(NFE.getStackTrace());
                    }
                }
            }
        );
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.lmpDate || v.getId() == R.id.Date_Picker_Button) {
            datePickerDialog.show(datePickerPair.get(v.getId()));

        } else if(v.getTag() != null && v.getTag().equals("TT")) {
            if (datePickerPair.containsKey(v.getId()) ) {
                if(getCheckbox(v, v.getId()).isChecked()) {
                    datePickerDialog.show(datePickerPair.get(v.getId()));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientInfo", "OnActivityResult");
    }

    CheckBox getCheckbox(View view, int id) {
        return (CheckBox)view.findViewById(id);
    }
}

