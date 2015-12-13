package org.sci.rhis.fwc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;


public class MinimumDeliveryInfoFragment extends Fragment {

    public interface DeliverySavedListener {
        public void onDeliverySaved(String result);
    }

    private final String LOGTAG = "FWC-MIN-DELIVERY";
    final private String SERVLET = "delivery";
    final private String ROOTKEY = "deliveryInfo";


    private View masterView = null;
    private PregWoman woman = null;
    private ProviderInfo provider = null;
    private Activity parentActivity = null;
    private AsyncMinDeliveryInfoUpdate infoUpdate = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_minimal_delivery,
                container, false);


        masterView.findViewById(R.id.idSaveAbobortion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMinimumDelivery(v);
            }
        });

        masterView.findViewById(R.id.idCancelAbobortion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMinimumDelivery(v);
            }
        });

        woman = getActivity().getIntent().getParcelableExtra("PregWoman");
        provider = getActivity().getIntent().getParcelableExtra("Provider");

        infoUpdate = new AsyncMinDeliveryInfoUpdate(new AsyncCallback() {
            @Override
            public void callbackAsyncTask(String result) {
                handleAbortionSaveResponse(result);
            }
        });

        return masterView;
    }

    private void handleAbortionSaveResponse(String result) {
        //pass hte call back the listener
        ((DeliverySavedListener)parentActivity).onDeliverySaved(result);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;

    }

    @Override
    public void onStart() {
        super.onStart();

        //Fragment fragment = getFragmentManager().findFragmentById(R.id.pachistory_fragment)
        Spinner abortionPlace = (Spinner)masterView.findViewById(R.id.idAbortionPlaceDropdown);

        abortionPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    //Utilities.MakeInvisible(parentActivity, R.id.idAbortionFacility);
                    masterView.findViewById( R.id.idAbortionFacility).setVisibility(View.GONE);
                } else if(position ==2 ) {
                    //Utilities.MakeVisible(parentActivity, R.id.idAbortionFacility);
                    masterView.findViewById( R.id.idAbortionFacility).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void saveDelivery() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientIntro", "OnActivityResult");
    }

    private void saveMinimumDelivery(View view) {
        Log.d(LOGTAG, "Handled Button Click");
        //Utilities.MakeInvisible(parentActivity, R.id.idMinDeliveryFragmentHolder);
        try {
            JSONObject saveDelivery = buildQueryHeader(false);
            getSpecialCases(saveDelivery);
            infoUpdate.execute(saveDelivery.toString(), SERVLET, ROOTKEY);

        } catch (JSONException jse) {

        }
    }

    private JSONObject buildQueryHeader(boolean isRetrieval) throws JSONException {
        //get info from database
        String queryString =   "{" +
                "healthid:" + woman.getHealthId() + "," +
                (isRetrieval ? "": "providerid:\""+String.valueOf(provider.getProviderCode())+"\",") +
                "pregno:" + woman.getPregNo() + "," +
                "deliveryLoad:" + (isRetrieval? "retrieve":"\"\"") +
                "}";

        return new JSONObject(queryString);
    }

    public void getSpecialCases(JSONObject json) {
        try {
            json.put("dOther", ""); //other delivery complicacies
            json.put("dOtherReason", ""); //other delivery complicacies

            json.put("dPlace","2");
            json.put("dCenterName","3");
            json.put("dAdmissionDate","");
            json.put("dWard", "");
            json.put("dBed","");
            json.put("dDate","2015-11-25");
            json.put("dTime","");
            json.put("dType","3");
            json.put("dNoLiveBirth","");
            json.put("dNoStillBirth","");
            json.put("dStillFresh","");
            json.put("dStillMacerated","");
            json.put("dAbortion","");
            json.put("dNewBornBoy","");
            json.put("dNewBornGirl","");
            json.put("dNewBornUnidentified","");
            json.put("dOxytocin", "2");
            json.put("dTraction","2");
            json.put("dUMassage","2");
            json.put("dEpisiotomy","2");
            json.put("dMisoprostol","2");
            json.put("dAttendantName", "");
            json.put("dAttendantDesignation","");
            json.put("dBloodLoss","2");
            json.put("dLateDelivery","2");
            json.put("dBlockedDelivery","2");
            json.put("dPlacenta","2");
            json.put("dHeadache","2");
            json.put("dBVision","2");
            json.put("dOBodyPart","2");
            json.put("dConvulsions","2");
            json.put("dOthers","2");
            json.put("dOthersReason","");
            json.put("dTreatment","[]");
            json.put("dAdvice","[]"); //it should always return some value due to multipleSelect.js plugin
            json.put("dRefer","2");
            json.put("dReferCenter","");
            json.put("dReferReason","[]");

            ///////////
        } catch (JSONException jse) {
            Log.e(LOGTAG, "Error Building dummy keys for delivery");
        }
    }
}

