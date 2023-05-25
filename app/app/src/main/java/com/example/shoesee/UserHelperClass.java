package com.example.shoesee;

public class UserHelperClass {

    String name, username, mail, phoneNo, password;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String username, String mail, String phoneNo, String password) {
        this.name = name;
        this.username = username;
        this.mail = mail;
        this.phoneNo = phoneNo;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
