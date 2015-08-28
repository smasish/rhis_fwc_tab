package org.sci.rhis.fwc;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jamil.zaman on 24/08/2015.
 */
public class GeneralPerson {

    private String name;
    private String guardianName;
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public GeneralPerson(JSONObject clientInfo) {
        try {
            //client = new PregWoman(JSONObject jso );
            name = clientInfo.getString("cName");
            guardianName = clientInfo.getString("cHusbandName");
            age = clientInfo.getInt("cAge");
                  //sex = clientInfo.getString("cSex") //TODO: Currently does not work
            sex = "F";
        } catch (JSONException JSE) {
            System.out.println("JSON Exception:");
            JSE.printStackTrace();
        }
    }

    public GeneralPerson(String name, String guardianName, int age, String sex) {
        this.name = name;
        this.guardianName = guardianName;
        this.age = age;
        this.sex = sex;
    }
}
