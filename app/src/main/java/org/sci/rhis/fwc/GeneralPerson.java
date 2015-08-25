package org.sci.rhis.fwc;

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

    public GeneralPerson() {
    }

    public GeneralPerson(String name, String guardianName, int age, String sex) {
        this.name = name;
        this.guardianName = guardianName;
        this.age = age;
        this.sex = sex;
    }
}
