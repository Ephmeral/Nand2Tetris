import java.util.HashMap;

public class Code {
    public static HashMap<String, String> destMap = new HashMap<>() {
        {
            put("M", "001");
            put("D", "010");
            put("MD", "011");
            put("A", "100");
            put("AM", "101");
            put("AD", "110");
            put("AMD", "111");
        }
    };

    public static HashMap<String, String> jumpMap = new HashMap<>() {
        {
            put("JGT", "001");
            put("JEQ", "010");
            put("JGE", "011");
            put("JLT", "011");
            put("JNE", "101");
            put("JLE", "110");
            put("JMP", "111");
        }
    };

    public static HashMap<String, String> cmpMap = new HashMap<>() {
        {
            put("0", "0101010");
            put("1", "0111111");
            put("-1", "0111010");
            put("D", "0001100");
            put("A", "0110000");
            put("!D", "0001101");
            put("!A", "0110001");
            put("-D", "0001111");
            put("-A", "0110011");
            put("D+1", "0011111");
            put("A+1", "0110111");
            put("D-1", "0001110");
            put("A-1", "0110010");
            put("D+A", "0000010");
            put("D-A", "0010011");
            put("A-D", "0000111");
            put("D&A", "0000000");
            put("D|A", "0010101");

            put("M", "1110000");
            put("!M", "1110001");
            put("-M", "1110011");
            put("M+1", "1110111");
            put("M-1", "1110010");
            put("D+M", "1000010");
            put("D-M", "1010011");
            put("M-D", "1000111");
            put("D&M", "1000000");
            put("D|M", "1010101");
        }
    };

    public static String dest(String des) {
        if (des.equals("000")) {
            return des;
        }
        return destMap.get(des);
    }

    public static String jump(String jmp) {
        if (jmp.equals("000")) {
            return jmp;
        }
        return jumpMap.get(jmp);
    }

    public static String comp(String cmp) {
        return cmpMap.get(cmp);
    }

    public static String code(String cd) {
        StringBuilder sb = new StringBuilder("111"); // C指令前三个为 111
        int equalId = cd.indexOf("=");  //等号的位置
        int comId = cd.indexOf(";");    //分号的位置

        String des = null, cmp = null, jmp = null;
        if (equalId == -1 && comId == -1) {
            des = "000";
            cmp = cd;
            jmp = "000";
        } else if (equalId == -1) {        // 说明没有等号，等号被省略
            des = "000";
            cmp = cd.substring(0, comId);
            jmp = cd.substring(comId + 1);
        } else if (comId == -1) {
            des = cd.substring(0, equalId);
            cmp = cd.substring(equalId + 1);
            jmp = "000";
        }  else  {
            des = cd.substring(0, equalId);
            cmp = cd.substring(equalId + 1, comId);
            jmp = cd.substring(comId + 1);
        }

        sb.append(comp(cmp));
        sb.append(dest(des));
        sb.append(jump(jmp));

        return sb.toString();
    }
}
