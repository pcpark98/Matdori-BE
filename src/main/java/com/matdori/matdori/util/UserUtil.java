package com.matdori.matdori.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {

    public static boolean isValidEmailFormat(String email) { return email.matches("[0-9]{8}@((inha.ac.kr)|(inha.edu))"); }
    public static boolean isValidPasswordFormat(String password) { return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$"); }
}
