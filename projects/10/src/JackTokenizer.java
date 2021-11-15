import java.io.*;
import java.util.ArrayList;

public class JackTokenizer {
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static String answer = "";

    public static void main(String[] args) throws IOException {
        String readFileName = "./Square/SquareGame.jack";
        parseFile(readFileName);

        String writeFileName = readFileName.substring(0,
                readFileName.lastIndexOf(".")) + "T1.xml";

        System.out.println(answer);

        BufferedWriter bw = new BufferedWriter(new FileWriter(writeFileName));
        bw.write(answer);
        bw.flush();
        bw.close();
    }

    // 去掉注释，得到 String 文件
    public static String parseFile(String filename) throws IOException {
        StringBuilder res = new StringBuilder("<tokens>\n");    // <tokens> 作为开头
        BufferedReader buff = new BufferedReader(new FileReader(filename));
        String line;
        boolean haveAnnotation = false;
        while ((line = buff.readLine()) != null) {
            // 去除单行注释及两边的空格
            int idx = line.indexOf("//");
            if (idx != -1) {
                line = line.substring(0, idx).strip();
            } else {
                line = line.strip();
            }

            // 去除多行注释
            idx = line.indexOf("/*");
            if (idx != -1) {
                // 同一行的情况 /* ************ */
                int idx1 = line.indexOf("*/");
                if (idx1 != -1) {
                    line = (line.substring(0, idx) + line.substring(idx1 + 2)).strip();
                } else {
                    haveAnnotation = true;
                }
            }

            if (haveAnnotation) {
                int idx1 = line.indexOf("*/");
                if (idx1 != -1) {
                    line = line.substring(idx1 + 2);
                    haveAnnotation = false;
                } else {
                    continue;
                }
            }
            // 如果预处理后的为空字符直接跳过
            if (line.equals("\n") || line.equals("")) {
                continue;
            }

            int i = 0;
            while (i < line.length()) {
                int j = i;
                // 找到一个不是字符的位置
                while (Character.isLetter(line.charAt(j))) {
                    j++;
                }
                String s;
                Token tk;
                if (i < j) {
                    s = line.substring(i, j);
                    tk = new Token(s, getTokenType(s));
                    tokens.add(tk);
                }

                if (line.charAt(j) == '\"') {
                    j++;
                    while(line.charAt(j) != '\"') {
                        j++;
                    }
                    s = line.substring(i + 1, j);
                    tk = new Token(s, TokenType.STRING_CONST);
                    tokens.add(tk);
                } else if (Character.isDigit(line.charAt(j))) {
                    while(Character.isDigit(line.charAt(j))) {
                        j++;
                    }
                    s = line.substring(i, j);
                    tk = new Token(s, TokenType.INT_CONST);
                    tokens.add(tk);
                }

                if (line.charAt(j) == '{'
                        || line.charAt(j) == '}'
                        || line.charAt(j) == '('
                        || line.charAt(j) == ')'
                        || line.charAt(j) == '['
                        || line.charAt(j) == ']'
                        || line.charAt(j) == '.'
                        || line.charAt(j) == ','
                        || line.charAt(j) == ';'
                        || line.charAt(j) == '+'
                        || line.charAt(j) == '-'
                        || line.charAt(j) == '*'
                        || line.charAt(j) == '/'
                        || line.charAt(j) == '|'
                        || line.charAt(j) == '='
                        || line.charAt(j) == '~') {
                    tk = new Token(Character.toString(line.charAt(j)), TokenType.SYMBOL);
                    tokens.add(tk);
                } else if (line.charAt(j) == '<') { // 三种特殊符号的处理
                    tk = new Token("&lt;", TokenType.SYMBOL);
                    tokens.add(tk);
                } else if (line.charAt(j) == '>') {
                    tk = new Token("&gt;", TokenType.SYMBOL);
                    tokens.add(tk);
                } else if (line.charAt(j) == '&') {
                    tk = new Token("&amp;", TokenType.SYMBOL);
                    tokens.add(tk);
                }
                i = j + 1;
            }
        }
        for (Token tk : tokens) {
            res.append(tk.toString());
        }
        res.append("</tokens>\n");      // </tokens> 作为结尾
        answer = res.toString();
        setCompilationEngineTokens();   // 设置CompilationEngineTokens，进行后面的处理
        buff.close();
        return answer;
    }

    public static void setCompilationEngineTokens() {
        CompilationEngine.tokens = tokens;
    }

    // 得到待处理token对象的类型
    public static String getTokenType(String s) {
        return switch (s) {
            case "class", "constructor", "function", "method", "field", "static",
                "var", "int", "char", "boolean", "void", "true", "false", "null",
                "this", "let", "do", "if", "else", "while", "return"
                    -> TokenType.KEYWORD;
            default -> TokenType.IDENTIFIER;
        };
    }

    public static String getKeyWordType(Token token) {
        return switch (token.getName()) {
            case "class" -> KeyWordType.CLASS;
            default -> "暂时未处理";
        };
    }
}

