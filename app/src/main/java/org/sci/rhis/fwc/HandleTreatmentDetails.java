package org.sci.rhis.fwc;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jamil.zaman on 21/10/15.
 */
public class HandleTreatmentDetails {

    final static int SYMPTOM_INDEX      = 0;
    final static int PROBLEM_INDEX      = 1;
    final static int DESEASE_INDEX      = 2;
    final static int TREATMENT_INDEX    = 3;
    final static int REFER_REASON_INDEX = 4;
    final static int REFER_CENTER_INDEX = 5;

    private HashMap<String, MultiSelectionSpinner> treatmentSpinnerMap;

    HandleTreatmentDetails(HashMap<String, MultiSelectionSpinner> spinnerMap/*, JSONObject json*/) {
        treatmentSpinnerMap = spinnerMap;
    }

    JSONObject getMultiSelectSpinnerValues(JSONObject json) {
        for (String key: treatmentSpinnerMap.keySet()) {
            try {
                json.put(key, "[" + TextUtils.join(",", treatmentSpinnerMap.get(key).getSelectedIndices()) + "]");
            } catch (JSONException JSE) {
                Log.e("HandleTreatmentDetails", JSE.toString());
            }
        }
        return json;
    }
}
