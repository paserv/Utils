package it.pipe.utils;

import java.net.*;

public class ProxyAuthenticator extends Authenticator {

    private String userName, password;

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }

    public ProxyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}