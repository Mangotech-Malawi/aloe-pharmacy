/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

/**
 *
 * @author Senzenjani
 */
public class Person {
    protected String firstname;
    protected String lastname;
    protected String fullname;
    private int id;
    private String gender;
    private String dob;
    private String homeVillage;
    protected String residence;
    private String nationality;
    protected String contact;
    protected String email;
    private String postalAddress;
    private JFXButton delete;
    private JFXButton update;
    private JFXCheckBox select;
    private JFXButton restore;

    public Person(String firstname, String lastname, int id, String gender,
            String dob, String homeVillage, String residence, String nationality,
            String contact, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.id = id;
        this.gender = gender;
        this.dob = dob;
        this.homeVillage = homeVillage;
        this.residence = residence;
        this.nationality = nationality;
        this.contact = contact;
        this.email = email;
    }
    
    public Person(){}

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setHomeVillage(String homeVillage) {
        this.homeVillage = homeVillage;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public void setDelete(JFXButton delete) {
        this.delete = delete;
    }

    public void setUpdate(JFXButton update) {
        this.update = update;
    }
    
    
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getHomeVillage() {
        return homeVillage;
    }

    public String getResidence() {
        return residence;
    }

    public String getNationality() {
        return nationality;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public JFXButton getDelete() {
        return delete;
    }

    public JFXButton getUpdate() {
        return update;
    }

    public void setSelect(JFXCheckBox select) {
        this.select = select;
    }

    public void setRestore(JFXButton restore) {
        this.restore = restore;
    }

    public JFXCheckBox getSelect() {
        return select;
    }

    public JFXButton getRestore() {
        return restore;
    }
    
}
