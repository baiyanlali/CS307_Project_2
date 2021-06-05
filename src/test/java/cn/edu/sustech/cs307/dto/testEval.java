package cn.edu.sustech.cs307.dto;
import org.checkerframework.checker.units.qual.A;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.regex.*;
import java.util.ArrayList;

public class testEval {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager=new ScriptEngineManager();
        ScriptEngine s=manager.getEngineByName("js");
        String s1="%d && (%d || %d)";
        ArrayList<Integer> arr=new ArrayList<>();
        arr.add(1);
        arr.add(0);
        arr.add(0);
        String s2= String.format(s1, arr.toArray());

        int r=(Integer) s.eval(s2);
        System.out.println(r);
//        String ss="((MA101A || MA101B) && MA103A)";
//        String sss=ss.replaceAll("\\|\\||\\&\\&|\\(|\\)", "");
//        String[] ssss=sss.split(" ");
//        System.out.println(ssss[0]);
    }

}
