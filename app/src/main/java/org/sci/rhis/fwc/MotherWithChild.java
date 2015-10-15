package org.sci.rhis.fwc;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mohammod.alamin on 10/14/2015.
 */

public class MotherWithChild extends GeneralPerson implements Parcelable {

    private Date actualDelivery;
    private String deliveryPlace;
    private String deliveryType;
    private String deliveryTime;
    private String deliveryCenterName;
    private int pregNo; //current pregnancy no
    private String husbandName;
    private DateFormat df;
    private Calendar edd_cal;
    private JSONObject Jso;

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


        dest.writeLong(getHealthId());
        dest.writeString(df.format(actualDelivery));
        dest.writeInt(getPregNo());
    }

    public static final Creator<MotherWithChild> CREATOR= new Creator<MotherWithChild>() {

        @Override
        public MotherWithChild createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new MotherWithChild(source);  //using parcelable constructor
        }

        @Override
        public MotherWithChild[] newArray(int size) {
            // TODO Auto-generated method stub
            return new MotherWithChild[size];
        }
    };

    //Parcel Constructor
    public MotherWithChild(Parcel data) {
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
            actualDelivery = df.parse(data.readString()); //actualDelivery
        } catch (ParseException pe) {
            System.out.println("Parsing Exception:");
            pe.printStackTrace();
        }
        setPregNo(data.readInt()); //pregnancyNo
        setDeliveryPlace(data.readString()); // Delivery Place
        setDeliveryType(data.readString());

    }

    //create from JSON
    public MotherWithChild(JSONObject clientInfo) {
        super(clientInfo);
        try {

            initialize(
                    // for newborn
                    clientInfo.getString("dTime"),
                    clientInfo.getString("dPlace"),
                    clientInfo.getString("dType")
            );
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    public MotherWithChild( String name,
                      String guardianName,
                      int age,
                      String sex
    ) {
        super(name, guardianName, age, sex);

    }
    void initialize(
            String _deliveryPlace,
            String _deliveryType,
            String  _deliveryTime
            ){
            // for new born
            // actualDelivery = _actualDelivery;
        deliveryTime = _deliveryTime;
        deliveryPlace=_deliveryPlace;
        deliveryType=_deliveryType;
            {
        df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.actualDelivery = df.parse(String.valueOf(actualDelivery));

        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }

    }
    }

    private Date addDateOffset(Date given, int days) {
        edd_cal = Calendar.getInstance();
        edd_cal.setTime(given);

        edd_cal.add(Calendar.DATE, days);
        return edd_cal.getTime();
    }


    public Date getActualDelivery() {
        return actualDelivery;
    }

    public void setActualDelivery(String actualDelivery) {
        try {
            this.actualDelivery = df.parse(actualDelivery);
        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
    }


    public int getPregNo() {
        return pregNo;
    }

    public void setPregNo(int pregNo) {
        this.pregNo = pregNo;
    }

    public void setDeliveryPlace(String deliveryPlace){
        this.deliveryPlace = deliveryPlace;
    }
    public String getDeliveryPlace(){return deliveryPlace;}

    public void setDeliveryType(String deliveryType){
        this.deliveryType = deliveryType;
    }
    public String getDeliveryType(){return deliveryType;}



    public  void setDeliveryTime(String deliveryTime){
        this.deliveryTime = deliveryTime;
    }
    public String getDeliveryTime(){return deliveryTime;}

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }
}
