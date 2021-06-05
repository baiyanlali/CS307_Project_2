package reference.service;

import java.util.regex.Pattern;

public class Util {
    public static String getName(String first_name,String last_name){
        Pattern pattern=Pattern.compile("[a-zA-Z]*");
        boolean english_name = pattern.matcher(first_name).matches();
        if(english_name){
            return first_name.concat(" ").concat(last_name);
        }else{
            return first_name.concat(last_name);
        }
    }
}
