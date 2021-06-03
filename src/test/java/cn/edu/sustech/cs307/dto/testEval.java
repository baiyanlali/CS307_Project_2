package cn.edu.sustech.cs307.dto;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class testEval {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager=new ScriptEngineManager();
        ScriptEngine s=manager.getEngineByName("js");
        String s1="1 && (0 || 0)";
        int r=(Integer) s.eval(s1);
        System.out.println(r);
    }

}
