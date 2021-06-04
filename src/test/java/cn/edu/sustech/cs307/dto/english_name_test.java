package cn.edu.sustech.cs307.dto;

import java.util.regex.Pattern;

public class english_name_test {
    public static void main(String[] args) {
        String first_name="agasSA";
        Pattern pattern=Pattern.compile("[a-zA-Z]*");
        boolean english_name = pattern.matcher(first_name).matches();
        System.out.println(english_name);
    }
}
