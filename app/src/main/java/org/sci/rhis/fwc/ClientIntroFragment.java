package org.sci.rhis.fwc;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ClientIntroFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_intro,
                container, false);
        return view;


        String EditText output;
        output = (EditText) output.findViewById(R.id.Client_name);

        String strJson = "{cBloodGroup:none, cAge:28, cVill:madhoppur bazar, cDist:Hobigonj, cHeight:0, cUpz:Madhoppur, cMobileNo:01711223344,cUnion:madhoppur, cName:MARUPA BEGUM, cMouza:010, cHusbandName:HATEM ALI}";
        String data = "";
        try {
            JSONObject jsonObj = new JSONObject(strJson);


            output.setText(data);

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

