package org.sci.rhis.fwc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;


public class DeliveryNewBornFragment extends Fragment {

    final private String SERVLET = "newborn";
    final private String ROOTKEY = "NewbornInfo";

    AsyncDeliveryInfoUpdate newbornInfoQueryTask;
    AsyncDeliveryInfoUpdate newbornInfoUpdateTask;

    private MultiSelectionSpinner multiSelectionSpinner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_new_born,
                container, false);

        final List<String> newbornreferreasonlist = Arrays.asList(getResources().getStringArray(R.array.Delivery_Newborn_Refer_Reason_DropDown));
        multiSelectionSpinner = (MultiSelectionSpinner) multiSelectionSpinner.findViewById(R.id.deliveryChildReferReasonSpinner);
        multiSelectionSpinner.setItems(newbornreferreasonlist);
        multiSelectionSpinner.setSelection(new int[]{});
        return view;
    }




}
