import java.io.*;

public class Parser {
    private static int romAddress = 0;
    public static int ramAddress = 16;

    public static void main(String[] args) throws IOException {
//        String parseRes = parseFile("./add/Add.asm");
//        // 写入文件中
//        String wirteFileName = "./add/Add.hack";

//        String parseRes = parseFile("./max/MaxL.asm");
//        String wirteFileName = "./max/MaxL.hack";
//
//        String parseRes = parseFile("./pong/PongL.asm");
//        String wirteFileName = "./pong/PongL.hack";
//
        String parseRes = parseFile("./rect/RectL.asm");
        String wirteFileName = "./rect/RectL.hack";
        BufferedWriter bw = new BufferedWriter(new FileWriter(wirteFileName));
        bw.write(parseRes);
        bw.flush();
        bw.close();
    }

    public static void parseFileInit(String filename) throws IOException {
        BufferedReader buff = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = buff.readLine()) != null) {
            if (line.equals("\n") || line.equals("")) {
                continue;
            }

            StringBuilder sb = null;
            int idx = line.indexOf("//");
            if (idx != -1) {
                sb = new StringBuilder(line.substring(0, idx).strip());
            } else {
                sb = new StringBuilder(line);
            }

            if (sb.isEmpty()) continue;
            if (sb.charAt(0) == '@') { // A 指令
                romAddress++;
            } else if (sb.charAt(0) == '('){        // L指令
                int last = sb.lastIndexOf(")");
                String sym = sb.toString().substring(1, last);
                SymbolTable.addEntry(sym, romAddress + 1);
            } else {    // C 指令 -> 直接跳过
                romAddress++;
            }
        }
        buff.close();
    }

    // 去掉注释，得到 String 文件
    public static String parseFile(String filename) throws IOException {
        parseFileInit(filename);        // 第一次预处理，将符号对应的下一个指令地址对应上
        StringBuilder res = new StringBuilder();
        BufferedReader buff = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = buff.readLine()) != null) {
            if (line.equals("\n") || line.equals("")) {
                continue;
            }

            StringBuilder sb = null;
            int idx = line.indexOf("//");
            if (idx != -1) {
                sb = new StringBuilder(line.substring(0, idx).strip());
            } else {
                sb = new StringBuilder(line);
            }

            if (sb.isEmpty()) continue;
            if (sb.charAt(0) == '@') { // A 指令
                String aCommand = sb.toString().substring(1);
                if (isNumeric(aCommand)) {
                    res.append(decToBinary(Integer.parseInt(aCommand))).append("\n");
                } else {
                    if (!SymbolTable.contains(aCommand)) {
                        SymbolTable.addEntry(aCommand, ramAddress++);
                    }
                    res.append(decToBinary(SymbolTable.getAddress(aCommand))).append("\n");
                }
            } else if (sb.charAt(0) == '('){        // L指令
                int last = sb.lastIndexOf(")");
                res.append(decToBinary(SymbolTable.getAddress(sb.toString().substring(1, last)))).append("\n");
            } else {    // C 指令
                // 调用Code中静态方法
                res.append(Code.code(sb.toString())).append("\n");
            }
        }
        buff.close();
        return res.toString();
    }

    // 将 A 指令中数字转换为16位的二进制表示
    public static String decToBinary(int num) {
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(num));
        int len = sb.length();
        return "0".repeat(Math.max(0, 16 - len)) + sb;
    }


    // 判断一个字符串是否全部为数字
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
