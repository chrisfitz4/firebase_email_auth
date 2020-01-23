package com.illicitintelligence.android.mythoughts.model;

import androidx.annotation.NonNull;

public class User {

    private String email;
    private String password;

    public User(String userName, String password) {
        this.email = userName;
        this.password = password;
    }

    public void setEmail(String userName) {
        this.email = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    @NonNull
    @Override
    public String toString() {
        return email+","+password;
    }
}
