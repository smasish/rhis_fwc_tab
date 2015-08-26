package org.sci.rhis.fwc;

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
    private int pregNo; //current pregnancy no
    private String husbandName;
    private DateFormat df;
    private Calendar edd_cal;
    private Calendar lmp_cal;



    public PregWoman( String name,
                      String guardianName,
                      int age,
                      String sex,
                      String lmp,
                      int pregNo) {
        super(name, guardianName, age, sex);

        df = new SimpleDateFormat("DD MMM yyyy");
        try {
            this.lmp = df.parse(lmp);
        } catch (ParseException PE) {
            System.out.println("Parsing Exception:");
            PE.printStackTrace();
        }
        //this.edd = new Date(lmp.getTime());
        //lmp.

        Calendar edd_cal = Calendar.getInstance();
        edd_cal.setTime(this.lmp);

        edd_cal.add(Calendar.DATE, 280);
        this.edd = edd_cal.getTime();

        this.pregNo = pregNo;

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
