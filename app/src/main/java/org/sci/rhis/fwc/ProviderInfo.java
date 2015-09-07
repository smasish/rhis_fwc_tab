package org.sci.rhis.fwc;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class ProviderInfo implements Parcelable {


    //Parcelable overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is written
        dest.writeString(mProviderCode);
        dest.writeString(mProviderName);
        dest.writeString(mProviderFacility);
    }

    public static final Parcelable.Creator<ProviderInfo> CREATOR= new Parcelable.Creator<ProviderInfo>() {

        @Override
        public ProviderInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ProviderInfo(source);  //using parcelable constructor
        }

        @Override
        public ProviderInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ProviderInfo[size];
        }
    };

    //Parcel Constructor
    public ProviderInfo(Parcel data) {
        //"IMPORTANT" ->Do not change the order by which the 'data' is accessed
        mProviderCode       = data.readString();
        mProviderName       = data.readString();
        mProviderFacility   = data.readString();
    }

    public ProviderInfo() {
    }

    public String getProviderCode() {
        return mProviderCode;
    }

    public void setProviderCode(String mProviderCode) {
        this.mProviderCode = mProviderCode;
    }

    public String getProviderName() {
        return mProviderName;
    }

    public void setProviderName(String mProviderName) {
        this.mProviderName = mProviderName;
    }

    public String getProviderFacility() {
        return mProviderFacility;
    }

    public void setProviderFacility(String mProviderFacility) {
        this.mProviderFacility = mProviderFacility;
    }

    static ProviderInfo getProvider() {
        if ( provider == null ) {
            provider = new ProviderInfo();
        }
        return provider;
    }

    private String mProviderCode;
    private String mProviderName;
    private String mProviderFacility;
    private static ProviderInfo provider;
}
