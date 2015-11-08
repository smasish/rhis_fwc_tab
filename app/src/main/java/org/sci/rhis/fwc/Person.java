package org.sci.rhis.fwc;

/**
 * Created by jamil.zaman on 05/11/15.
 */
public class Person {


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public int getIcon() {
        return icon;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public Person(){
        super();
    }

    public Person(String name, String fatherName, String healthId, int idIndex, int icon) {
        this.name = name;
        this.fatherName = fatherName;
        this.healthId = healthId;
        this.idIndex = idIndex;
        this.icon = icon;
    }

    private String name;
    private String fatherName;
    private String healthId;
    private int idIndex;
    private int icon;
}
