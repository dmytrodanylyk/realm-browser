package com.dd.realmsample.data;

import io.realm.RealmList;
import io.realm.RealmObject;

public class User extends RealmObject {

    private int age;
    private boolean isBlocked;
    private String name;
    private Address address;
    private RealmList<RealmString> emailList;
    private RealmList<Contact> contactList;

    public RealmList<RealmString> getEmailList() {
        return emailList;
    }

    public void setEmailList(RealmList<RealmString> emailList) {
        this.emailList = emailList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public RealmList<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(RealmList<Contact> contactList) {
        this.contactList = contactList;
    }
}
