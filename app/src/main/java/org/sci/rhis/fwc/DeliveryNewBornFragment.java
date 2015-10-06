package org.sci.rhis.fwc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DeliveryNewBornFragment extends Fragment {

    final private String SERVLET = "newborn";
    final private String ROOTKEY = "NewbornInfo";
    private PregWoman newborn;
    private MultiSelectionSpinner multiSelectionSpinner;

    public static DeliveryNewBornFragment newInstance(String param1, String param2) {
        DeliveryNewBornFragment fragment = new DeliveryNewBornFragment();

        return fragment;
    }

    public DeliveryNewBornFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_new_born,
                container, false);

        return view;
    }




}
