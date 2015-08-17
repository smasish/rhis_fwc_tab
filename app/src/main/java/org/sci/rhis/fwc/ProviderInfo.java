package org.sci.rhis.fwc;

/**
 * Created by jamil.zaman on 16/08/2015.
 */
public class ProviderInfo {
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

    private String mProviderCode;
    private String mProviderName;
    private String mProviderFacility;
}
