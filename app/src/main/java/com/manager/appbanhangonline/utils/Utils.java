package com.manager.appbanhangonline.utils;

import com.manager.appbanhangonline.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL = "http://192.168.56.1/banhang/"; // Virtual mobile
    //public static final String BASE_URL = "http:// 192.168.1.15/banhang/"; // Real mobile
    public static User user_current = new User();
    public static String ID_RECEIVE;
    public static final String IDSEND = "idsend";
    public static final String IDRECEIVE = "idreceive";
    public static final String MESS = "message";
    public static final String DATETIME = "datetime";
    public static final String PATH_CHAT = "chat";
}
