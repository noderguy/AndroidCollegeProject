package com.example.seung.visioncollege;

public class User {

    String username;
    String password;

    public User() {
    }

    // 유저 정보 설정.
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }


    // 유저 계정에 대한 조회자 및 설정자
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
}