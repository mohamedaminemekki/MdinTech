package org.example.mdintech.entities;

import org.example.mdintech.utils.UserRole;

import java.util.Date;

public class User {
    private int CIN;
    private String Name;
    private String Email;
    private String Password;
    private UserRole Role;
    private String Phone;
    private String Address;
    private String City;
    private String State;
    private boolean status;
    private String pathtopic;
    private Date birthday;

    public User(String name, int CIN, String email, String password, UserRole role, String phone, String address, String city, String state,String pathtopic, Date birthday) {
        Name = name;
        this.CIN = CIN;
        Email = email;
        Password = password;
        Role = role;
        Phone = phone;
        Address = address;
        City = city;
        State = state;
        status = false;
        this.pathtopic = pathtopic;
        this.birthday = birthday;
    }

    public User(String name, int CIN, String email, String password, UserRole role, String phone, String address, String city, String state, boolean status,String pathtopic, Date birthday) {
        Name = name;
        this.CIN = CIN;
        Email = email;
        Password = password;
        Role = role;
        Phone = phone;
        Address = address;
        City = city;
        State = state;
        this.status = status;
        this.pathtopic = pathtopic;
        this.birthday = birthday;
    }

    public User(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPathtopic() {
        return pathtopic;
    }

    public void setPathtopic(String pathtopic) {
        this.pathtopic = pathtopic;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public UserRole getRole() {
        return Role;
    }

    public void setRole(UserRole role) {
        Role = role;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCIN() {
        return CIN;
    }

    @Override
    public String toString() {
        return "User{" +
                "CIN=" + CIN +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Role=" + Role +
                ", Phone='" + Phone + '\'' +
                ", Address='" + Address + '\'' +
                ", City='" + City + '\'' +
                ", State='" + State + '\'' +
                ", status=" + status +
                '}';
    }
}
