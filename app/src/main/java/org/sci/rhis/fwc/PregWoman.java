package org.sci.rhis.fwc;

import android.app.Activity;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class PregWoman extends GeneralPerson {
    private Date lmp;
    private Date edd;
    private String pregNo; //current pregnancy no
    private String husbandName;
    private String para;
    private String gravida;
    private String nBoy;
    private String nGirl;
    private int lastChildAge;
    private int height;
    private String bloodGroup;
    private String historyComplicated;
    private String historyContent;
    private DateFormat df;
    private Calendar edd_cal;
    private Calendar lmp_cal;
    private JSONObject Jso;

    private static PregWoman client;

    private static enum PREG_STATUS {NEW, ANC, DELIVERING, PNC, NOT_PREGNANT};


    final static int PREG_PERIOD = 280; //We are only considering 280 days now

    public static PregWoman CreatePregWoman(JSONObject clientInfo)  {

        if (client != null) {
            return client;
        }

        client = new PregWoman(clientInfo);

        return client;
    }

    public PregWoman(JSONObject clientInfo) {
        super(clientInfo);
        try {
            //client = new PregWoman(JSONObject jso );
            initialize( clientInfo.getString("cLMP"),
                        clientInfo.getString("cEDD"),
                        clientInfo.getString("cPregNo"),
                        clientInfo.getString("cPara"),
                        clientInfo.getString("cGravida"),
                        clientInfo.getString("cBoy"),
                        clientInfo.getString("cGirl"),
                        clientInfo.getInt("cLastChildAge"),
                        clientInfo.getInt("cHeight"),
                        clientInfo.getString("cBloodGroup"),
                        clientInfo.getString("cHistoryComplicated"),
                        clientInfo.getString("cHistoryComplicatedContent")
                    );
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    public PregWoman( String name,
                      String guardianName,
                      int age,
                      String sex,
                      String _lmp,
                      int pregNo
                      ) {
        super(name, guardianName, age, sex);
        //initialize(_lmp, "", pregNo);
    }

    void initialize(
                    String _lmp,
                    String _edd,
                    String _pregNo,
                    String _para,
                    String _gravida,
                    String _nBoy,
                    String _nGirl,
                    int _lastChildAge,
                    int _height,
                    String _bloodGroup,
                    String _historyComplicated,
                    String _historyContent
                    ) {
        df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.lmp = df.parse(_lmp);
            if(!_edd.equals("")) {
               edd = df.parse(_edd);
            } else {
                edd_cal = Calendar.getInstance();
                edd_cal.setTime(this.lmp);

                edd_cal.add(Calendar.DATE, PREG_PERIOD);
                this.edd = edd_cal.getTime();
            }
        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
        pregNo = _pregNo;
        para = _para;
        gravida = _gravida;
        nBoy = _nBoy;
        nGirl = _nGirl;
        lastChildAge = _lastChildAge;
        height = _height;
        bloodGroup =_bloodGroup;
        historyComplicated = _historyComplicated;
        historyContent = _historyContent;

        this.pregNo = pregNo;
    }

    public void UpdateUIField(Activity activity) {

        ((EditText)activity.findViewById(R.id.lmpDate)).setText(df.format(lmp));
        ((EditText)activity.findViewById(R.id.edd)).setText(df.format(edd));
        ((EditText)activity.findViewById(R.id.gravida)).setText(gravida);
        ((EditText)activity.findViewById(R.id.para)).setText(para);
        ((EditText)activity.findViewById(R.id.SonNum)).setText(nBoy);
        ((EditText)activity.findViewById(R.id.DaughterNum)).setText(nGirl);
        ((EditText)activity.findViewById(R.id.lastChildYear)).setText(String.valueOf(lastChildAge / 12));
        ((EditText)activity.findViewById(R.id.lastChildMonth)).setText(String.valueOf(lastChildAge%12));
        ((EditText)activity.findViewById(R.id.heightFeet)).setText(String.valueOf(height/12));
        ((EditText)activity.findViewById(R.id.heightInch)).setText(String.valueOf(height%12));
    }

    public Date getLmp() {
        return lmp;
    }

    public void setLmp(String lmp) {
        try {
            this.lmp = df.parse(lmp);
        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
    }

    public Date getEdd() {
        return edd;
    }

    public void setEdd(Date edd) {
        this.edd = edd;
    }

    public String getPregNo() {
        return pregNo;
    }

    public void setPregNo(String pregNo) {
        this.pregNo = pregNo;
    }

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }
}
