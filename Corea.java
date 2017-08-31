import java.lang.Character;
import java.lang.StringBuilder;
import java.lang.Integer;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.HashMap;

public class Corea {
    String contents = "";
    ArrayList<String> stack = new ArrayList<String>();
    HashMap<Character, String> fields = new HashMap<Character, String>();
    Scanner input;
    boolean implicitField = true;
    
    static HashMap<String, String> constants = null;
    
    public static String getConstant(String key) {
        if(constants == null) {
            constants = new HashMap<String, String>();
            constants.put("0", "\"");
            constants.put("1", "`");
            constants.put("2", "---");
            constants.put("3", "***");
            constants.put("4", "Hello, World!");
            constants.put("5", "\"5C\"");
            constants.put("6", "<>");
            constants.put("7", "[]");
            constants.put("8", "()");
            constants.put("9", "{}");
        }
        return constants.get(key);
    }
    
    // public Corea() { }
    
    public void push(Object x) {
        stack.add(x.toString());
    }
    
    public String pop() {
        return stack.remove(stack.size() - 1);
    }
    
    public static String mirrorHorizontal(String s) {
        String res = "";
        for(int i = s.length() - 1; i >= 0; --i) {
            char c = s.charAt(i);
            String search = "[{(</\\>)}]";
            int ind = search.indexOf(c);
            if(ind >= 0) {
                res += search.charAt(search.length() - ind - 1);
            }
            else {
                res += c;
            }
        }
        return res;
    }
    
    public static String reverseString(String s) {
        return new StringBuilder(s).reverse().toString();
    }
    
    public static String mirrorVertical(String s) {
        String res = "";
        for(String e : s.split("\\r?\\n")) {
            // System.out.println("::"+e+"::");
            String build = "";
            for(int i = 0; i < e.length(); i++) {
                if(e.charAt(i) == '\\')
                    build += '/';
                else if(e.charAt(i) == '/')
                    build += '\\';
                else
                    build += e.charAt(i);
            }
            
            res += "\n" + build;
        }
        // System.out.println(".."+res+"..");
        return res.substring(1);
    }
    
    public static String mirrorVerticalHorizontal(String s) {
        String res = "";
        for(String e : s.split("\\r?\\n")) {
            // System.out.println("::"+e+"::");
            String build = "";
            for(int i = e.length() - 1; i >= 0; --i) {
                // if(e.charAt(i) == '\\')
                    // build += '/';
                // else if(e.charAt(i) == '/')
                    // build += '\\';
                // else
                    // build += e.charAt(i);
                // ---
                char c = e.charAt(i);
                String search = "[{(</\\>)}]";
                int ind = search.indexOf(c);
                if(ind >= 0) {
                    // System.out.println("compl of " + c + " = " + 
                    char compl = search.charAt(search.length() - ind - 1);
                    build += compl;
                }
                else {
                    build += c;
                }
            }
            
            res += "\n" + build;
        }
        // System.out.println(".."+res+"..");
        return res.substring(1);
    }
    
    public int pushString(String source, int i, char delim) {
        String build = "";
        i++;
        do {
            boolean curIsDelim = source.charAt(i) == delim;
            boolean nextIsNotDelim = i + 1 < source.length()
                ? source.charAt(i + 1) != delim
                : true;
            if(curIsDelim) {
                if(nextIsNotDelim) {
                    break;
                } else {
                    i++;
                }
            }
            build += source.charAt(i++);
        } while(i < source.length());
        push(build);
        return i;
    }
    
    public void executeSequence(String cmds) {
        for(int i = 0; i < cmds.length(); i++) {
            char cmd = cmds.charAt(i);
            if(Character.isDigit(cmd)) {
                String build = "";
                while(Character.isDigit(cmds.charAt(i))) {
                    build += cmds.charAt(i++);
                }
                --i;
                push(build);
            }
            
            else if(cmd == '\'') {
                push(cmds.charAt(++i));
            }
            
            else if(cmd == '`') {
                i = pushString(cmds, i, '`');
                // String build = "";
                // i++;
                // while(cmds.charAt(i) != '`') {
                    // build += cmds.charAt(i);
                    // i++;
                // }
                // push(build);
                // i--;
            }
            
            else if(cmd == ':') {
                String t = pop();
                push(t);
                push(t);
            }
            
            else if(cmd == '~') {
                String a, b;
                b = pop();
                a = pop();
                push(a + b);
            }
            
            else if(cmd == '<') {
                push(contents);
            }
            
            else if(cmd == '@') {
                contents = "";
            }
            
            else if(cmd == '>') {
                contents += pop();
            }
            
            else if(cmd == 'i') {
                push(input.nextLine());
            }
            
            else if(cmd == 'r') {
                String s, search, repl;
                repl = pop();
                search = pop();
                s = pop();
                push(s.replace(search, repl));
            }
            
            else if(cmd == 'R') {
                String s, search, repl;
                repl = pop();
                search = pop();
                s = pop();
                push(s.replaceAll(search, repl));
            }
            
            else if(cmd == 'F') {
                char field = cmds.charAt(++i);
                fields.put(field, pop());
            }
            
            else if(cmd == 'f') {
                fields.put(pop().charAt(0), pop());
            }
            
            else if(cmd == 'L') {
                push(Corea.reverseString(pop()));
            }
            
            else if(cmd == 'g') {
                implicitField = false;
                execFields();
            }
            
            else if(cmd == 'x') {
                String str, rep;
                rep = pop();
                str = pop();
                push(new String(new char[Integer.parseInt(rep)]).replace("\0", str));
            }
            
            else if(cmd == 'u') {
                char toRepeat = cmds.charAt(++i);
                int amount = (int)cmds.charAt(++i) - 31;
                executeSequence("'" + toRepeat + amount + 'x');
            }
            
            else if(cmd == '!') {
                executeSequence("1d");
            }
            
            else if(cmd == 'X') {
                char next = cmds.charAt(++i);
                executeSequence("'" + next + "\\x");
            }
            
            else if(cmd == '\\') {
                String a, b;
                b = pop();
                a = pop();
                push(b);
                push(a);
            }
            
            else if(cmd == 'E') {
                try {
                    push(Corea.readFile(pop().trim()));
                } catch(IOException e) {
                    
                }
            }
            
            else if(cmd == '&') {
                String a, b, c;
                c = pop();
                b = pop();
                a = pop();
                push(c);
                push(a);
                push(b);
            }
            
            else if(cmd == '$') {
                pop();
            }
            
            else if(cmd == 'l') {
                push(pop().length());
            }
            
            else if(cmd == 'S') {
                executeSequence(":&~~");
            }
            
            else if(cmd == 's') {
                char delim = cmds.charAt(++i);
                i = pushString(cmds, i, delim);
            }
            
            else if(cmd == '+') {
                String a, b;
                b = pop();
                a = pop();
                push(new BigInteger(a).add(new BigInteger(b)));
            }
            
            else if(cmd == 'c') {
                push("" + ((char) new BigInteger(pop()).intValue()));
            }
            
            else if(cmd == 'C') {
                push(Corea.getConstant(pop()));
            }
            
            else if(cmd == '-') {
                String a, b;
                b = pop();
                a = pop();
                push(new BigInteger(a).subtract(new BigInteger(b)));
            }
            
            else if(cmd == '/') {
                String a, b;
                b = pop();
                a = pop();
                push(new BigInteger(a).divide(new BigInteger(b)));
            }
            
            else if(cmd == '*') {
                String a, b;
                b = pop();
                a = pop();
                push(new BigInteger(a).multiply(new BigInteger(b)));
            }
            
            else if(cmd == 'h') {
                push(Corea.mirrorHorizontal(pop()));
            }
            
            else if(cmd == 'H') {
                push(mirrorVerticalHorizontal(pop()));
            }
            
            else if(cmd == 'v') {
                push(Corea.mirrorVertical(pop()));
            }
            
            else if(cmd == 'd') {
                String f, n;
                n = pop();
                f = pop();
                BigInteger k = new BigInteger(n);
                while(!k.equals(BigInteger.ZERO)) {
                    executeSequence(f);
                    k = k.subtract(BigInteger.ONE);
                }
            }
            
            else if(cmd == 'z') {
                for(String s : stack.toArray(new String[stack.size()])) {
                    System.out.println(s);
                }
            }
            
            else if(cmd == '|') {
                char next = cmds.charAt(++i);
                contents += next;
            }
            
            else if(cmd == '(') {
                executeSequence("1-");
            }
            
            else if(cmd == ')') {
                executeSequence("1+");
            }
            
            else if(cmd == '.') {
                char subcmd = cmds.charAt(++i);
                if(subcmd == 'j') {
                    String str, joiner;
                    joiner = pop();
                    str = pop();
                    push(str.replaceAll(".", "$0" + joiner));
                }
            }
        }
    }
    
    public void run(String s) {
        for(int i = 0; i < s.length(); i++) {
            int start = i;
            while(i < s.length() && s.charAt(i) != '"') {
                i++;
            }
            contents += s.substring(start, i);
            // System.out.println("~"+contents);
            if(i >= s.length())
                break;
            start = ++i;
            while(i < s.length() && s.charAt(i) != '"') {
                i++;
            }
            executeSequence(s.substring(start, i));
        }
    }
    
    public void execFields() {
        String res = "";
        char[] charArray = contents.toCharArray();
        for(char el : charArray) {
            // System.out.println("iterate: " + el);
            if(fields.containsKey(el)) {
                // System.out.println("replacing: " + el + "...");
                res += fields.get(el);
            }
            
            else {
                res += el;
            }
        }
        contents = res;
    }
    
    public void end() {
        if(implicitField)
            execFields();
        System.out.print(contents);
    }
    
    public static String readFile(String name) throws IOException {
        String contents;
        contents = new String(Files.readAllBytes(Paths.get(name)));
        return contents;
    }
    
    public static void main(String args[]) {
        if(args.length == 0) {
            System.err.println("Error: insufficient arguments given.");
            System.exit(-1);
        }
        Corea inst = new Corea();
        inst.input = new Scanner(System.in);
        String contents = "";
        try {
            contents = readFile(args[0]);
        } catch(IOException e) {
            System.err.println("Error: no such file " + args[0]);
            System.exit(-2);
        }
        inst.run(contents);
        // System.out.println("ending");
        inst.end();
        // System.out.print(inst.contents);
        // for(String s : inst.stack.toArray(new String[inst.stack.size()])) {
            // System.out.println(s);
        // }
    }
}