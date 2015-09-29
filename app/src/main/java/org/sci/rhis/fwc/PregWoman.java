package org.sci.rhis.fwc;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
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
public class PregWoman extends GeneralPerson implements Parcelable{
    private Date lmp;
    private Date edd;
    private Date actualDelivery;
    private int pregNo; //current pregnancy no
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

    //constants
    private static enum PREG_STATUS {NEW, ANC, DELIVERING, PNC, NOT_PREGNANT};
    public static enum PREG_SERVICE {NEW, ANC, DELIVERY, PNC};

    final static int PREG_PERIOD    = 280; //We are only considering 280 days now
    final static int PNC_THRESHOLD  = 42;
    final static int ANC_THRESHOLD  = 294; //  PREG_PERIOD +  (2*7); -> 42 weeks from LMP

    //Parcelable overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (df == null) {
             df = new SimpleDateFormat("yyyy-MM-dd");
        }

        //Never ever change the order, if it absolutely necessary reflect the changes also in
        //the constructor that takes in a Parcel.
        dest.writeString(getName());
        dest.writeString(getGuardianName());
        dest.writeInt(getAge());
        dest.writeString(getSex());
        dest.writeLong(getHealthId());
        dest.writeString(df.format(lmp));
        dest.writeInt(getPregNo());
    }

    public static final Parcelable.Creator<PregWoman> CREATOR= new Parcelable.Creator<PregWoman>() {

        @Override
        public PregWoman createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new PregWoman(source);  //using parcelable constructor
        }

        @Override
        public PregWoman[] newArray(int size) {
            // TODO Auto-generated method stub
            return new PregWoman[size];
        }
    };

    //Parcel Constructor
    public PregWoman(Parcel data) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is accessed

        super(
                data.readString(),      //name
                data.readString(),      //guardian name
                data.readInt(),         //age
                data.readString()       //sex
        );
        setHealthId(data.readLong());   //healthId
        if(df == null) {
            df = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {
            lmp = df.parse(data.readString()); //lmp
        } catch (ParseException pe) {
            System.out.println("Parsing Exception:");
            pe.printStackTrace();
        }
        setPregNo(data.readInt()); //pregnancyNo
        UpdateEdd();
    }

    public static PregWoman CreatePregWoman(JSONObject clientInfo) throws JSONException {

        //Only create PregWOman when it is confirmed she is pregnant
        //meaning pregnancy related information i s present
        if(!clientInfo.getString("cNewMCHClient").equals("False")) {
            client = new PregWoman(clientInfo);
        } else {
            client = null;
        }

        return client;
    }
/*
    @Deprecated
    public static void DeletePregWoman()  {

        client = null;
    }

    @Deprecated
    public static PregWoman GetPregWoman() {

        if (client != null) {
            return client;
        }

        client = new PregWoman();

        return client;
    }*/

    //default
    public PregWoman() {
        super("","",0,"F");
    }

    //create from JSON
    public PregWoman(JSONObject clientInfo) {
        super(clientInfo);
        try {
            //client = new PregWoman(JSONObject jso );
            initialize( clientInfo.getString("cLMP"),
                        clientInfo.getString("cEDD"),
                        clientInfo.getInt("cPregNo"),
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
                      String sex
                      ) {
        super(name, guardianName, age, sex);
        //initialize(_lmp, "", pregNo);
    }

    void initialize(
                    String _lmp,
                    String _edd,
                    int    _pregNo,
                    String _para,
                    String _gravida,
                    String _nBoy,
                    String _nGirl,
                    int    _lastChildAge,
                    int    _height,
                    String _bloodGroup,
                    String _historyComplicated,
                    String _historyContent
                    ) {
        df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.lmp = df.parse(_lmp);
            UpdateEdd();
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

    private Date addDateOffset(Date given, int days) {
        edd_cal = Calendar.getInstance();
        edd_cal.setTime(given);

        edd_cal.add(Calendar.DATE, days);
        return edd_cal.getTime();
    }

    private void UpdateEdd() {
        try{
            if(edd == null) {
                edd = addDateOffset(lmp, PREG_PERIOD);
            }
        } catch (Exception PE) {
            System.out.println("Exception:");
            PE.printStackTrace();
        }
    }

    public Date getAncThreshold() {
        return addDateOffset(lmp, ANC_THRESHOLD);
    }

    public Date getPncThreshold() {
        return addDateOffset(lmp, ANC_THRESHOLD);
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

    public boolean isEligibleFor(PREG_SERVICE service) {
        boolean eligible = false;
        Date today = new Date();
        switch(service) {
            case NEW:
            case ANC:
            case DELIVERY:
                if(today.before(getAncThreshold()))
                    eligible = true;
                break;
            case PNC:
                if(today.before(getAncThreshold()))
                    eligible = true;
                break;
            default:
                eligible = false;
        }

        return eligible;

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

    public int getPregNo() {
        return pregNo;
    }

    public void setPregNo(int pregNo) {
        this.pregNo = pregNo;
    }

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }
}
