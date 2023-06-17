package com.example.myfarm.config;

public class Network {

    private static final String END_API="http://192.168.1.51:5000/";

    public static String getEndApi(String end){
        return END_API+end;
    }
}

