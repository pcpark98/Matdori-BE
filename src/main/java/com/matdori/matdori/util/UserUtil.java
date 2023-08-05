package com.matdori.matdori.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UserUtil {

    public static boolean isValidEmailFormat(String email) { return email.matches("[A-Za-z0-9]+@((inha.ac.kr)|(inha.edu))"); }
    public static boolean isValidPasswordFormat(String password) { return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@^~$!%*#?&])[A-Za-z\\d@^~$!%*#?&]{8,16}$"); }

    public static String getRandomNickname() { return UUID.randomUUID().toString().substring(0,10); }// 형용사 붙인 맛도리로..
}
