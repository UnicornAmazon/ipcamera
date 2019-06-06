package com.afscope.ipcamera.beans;

public class LoginBean {
    String ip ;
    int port;
    Login login;

    public LoginBean(String ip, int port, Login login) {
        this.ip = ip;
        this.port = port;
        this.login = login;
    }
}
