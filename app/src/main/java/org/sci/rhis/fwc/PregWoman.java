package org.sci.rhis.fwc;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class PregWoman extends GeneralPerson {
    private String lmp;
    private String edd;
    private int pregNo; //current pregnancy no
    private String husbandName;



    public PregWoman( String name,
                      String guardianName,
                      int age,
                      String sex,
                      String lmp,
                      int pregNo) {
        super(name, guardianName, age, sex);
        this.lmp = lmp;
        this.pregNo = pregNo;
    }

    public String getLmp() {
        return lmp;
    }

    public void setLmp(String lmp) {
        this.lmp = lmp;
    }

    public String getEdd() {
        return edd;
    }

    public void setEdd(String edd) {
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
