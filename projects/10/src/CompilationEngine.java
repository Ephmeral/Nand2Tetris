import java.util.ArrayList;
import java.util.HashSet;

public class CompilationEngine {
    public static ArrayList<Token> tokens = new ArrayList<>();
    public static String answer = "";
    public static StringBuilder sbToken = new StringBuilder();
    public static int blackNum = 0; // 空格的数量
    private static int i = 0;      // 用于全局遍历tokens
    private static final HashSet<String> operations = new HashSet<>();

    static {
        operations.add("+");
//        operations.add("-");
        operations.add("*");
        operations.add("/");
        operations.add("&amp;");
        operations.add("|");
        operations.add("&lt;");
        operations.add("&gt;");
        operations.add("=");
    }

    public static String traversal() {
        compileClass();
        answer = sbToken.toString();
        System.out.println(answer);
        return answer;
    }

    public static void compileClass() {
        sbToken.append("<class>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            switch (token.getName()) {
                case "field", "static" -> compileClassVarDec();
                case "constructor", "method", "function" -> compileSubroutine();
                case ";" -> {
                    i++;
                }
                default -> {
                    sbToken.append(" ".repeat(Math.max(0, blackNum)));
                    sbToken.append(token);
                    i++;
                }
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</class>\n");
    }

    public static void compileClassVarDec() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<classVarDec>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(";")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
            }
            i++;
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</classVarDec>\n");
    }

    public static void compileSubroutine() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<subroutineDec>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("(")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileParameterList();
            } else if (token.getName().equals("{")) {
                compileSubroutineBody();
            } else if (token.getName().equals("}")) {
                i++;
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</subroutineDec>\n");
    }

    public static void compileSubroutineBody() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<subroutineBody>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("{")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                token = tokens.get(i);
                while (token.getName().equals("var")) {
                    compileVarDec();
                    i++;
                    token = tokens.get(i);
                }
                compileStatements();
            } else if (token.getName().equals("}")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</subroutineBody>\n");
    }

    public static void compileParameterList() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<parameterList>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(")")) {
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
            }
            i++;
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</parameterList>\n");
    }

    public static void compileVarDec() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<varDec>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(";")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</varDec>\n");
    }

    public static void compileStatements() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<statements>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("let")) {
                compileLet();
            } else if (token.getName().equals("var")) {
                compileVarDec();
            } else if (token.getName().equals("if")) {
                compileIf();
            } else if (token.getName().equals("while")) {
                compileWhile();
            } else if (token.getName().equals("do")) {
                compileDo();
            } else if (token.getName().equals("return")) {
                compileReturn();
            } else if (token.getName().equals("}")) {
                break;
            } else if (token.getName().equals(";")) {
                i++;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</statements>\n");
    }

    public static void compileDo() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<doStatement>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("(")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpressionList();
            } else if (token.getName().equals(";")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</doStatement>\n");
    }

    public static void compileLet() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<letStatement>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("=")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpression();
            } else if (token.getName().equals("[")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpression();
            } else if (token.getName().equals(";")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else if (token.getName().equals(")")) {
                i++;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</letStatement>\n");
    }

    public static void compileWhile() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<whileStatement>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("(")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpression();
            } else if (token.getName().equals("{")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileStatements();
            } else if (token.getName().equals("}")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</whileStatement>\n");
    }

    public static void compileReturn() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<returnStatement>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("return")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            } else if (token.getName().equals(";")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                compileExpression();
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</returnStatement>\n");
    }

    public static void compileIf() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<ifStatement>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("(")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpression();
            } else if (token.getName().equals("{")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileStatements();
            } else if (token.getName().equals("}")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                token = tokens.get(i);
                if (token.getName().equals("else")) {
                    compileElse();
                    i++;
                }
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</ifStatement>\n");
    }

    public static void compileElse() {
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals("{")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileStatements();
            } else if (token.getName().equals("}")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
    }

    public static void compileExpression() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<expression>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(";")) {
                break;
            } else if (token.getName().equals(")")) {
                if (operations.contains(tokens.get(i + 1).getName())) {
                    sbToken.append(" ".repeat(Math.max(0, blackNum)));
                    sbToken.append(token);
                    i++;
                } else {
                    break;
                }
            } else if (token.getName().equals(",")) {
                break;
            } else if (token.getName().equals("]")) {
                break;
            } else if (operations.contains(token.getName())) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            } /*else if (token.getName().equals("-")
                    || token.getName().equals("~")) {
                compileTerm();
            } */ else {
                compileTerm();
                if (operations.contains(tokens.get(i + 1).getName())) {
                    break;
                }
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</expression>\n");
    }

    private static int cnt = 0;   // 记录括号的个数
    // 用来判断是否有括号，无括号的时候 cnt == 0 也会成立
    private static boolean flag = false;

    public static void compileTerm() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<term>\n");
        blackNum += 2;

        // 取出下一个符号，判断是否为调用一个函数，数组等情况
        Token tk = tokens.get(i + 1);
        switch (tk.getName()) {
            case "." -> { // (className | varName)
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;    // .
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++; // subroutineName
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++; // (
//                sbToken.append("这是一个括号测试debug" + i);
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;
                compileExpressionList();
//                sbToken.append("这是一个测试debug" + i);
                // 下面为当前 ） 的输出
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;
            }
            case "[" -> {
                // varName
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;
                // [expression]
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;
                compileExpression();
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));
                i++;
            }
            case "(" -> {
                if (tokens.get(i).equals("(")) {
                    i++;
                    sbToken.append(" ".repeat(Math.max(0, blackNum)));
                    sbToken.append(tk);
                    sbToken.append("测试调用term========");
                    compileExpressionList();
                } else {
//                    sbToken.append("测试调用term");
                }
            }
        }

        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(";")
                    || token.getName().equals(",")
                    || token.getName().equals("]")
                    || operations.contains(token.getName())) {
                break;
            } else if (token.getName().equals(")")) {
                cnt -= 1;
                break;
            } else if (token.getName().equals("(")) {
                cnt++;
                flag = true;
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileExpression();
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(tokens.get(i));

            } else if (token.getName().equals("-")
                    || token.getName().equals("~")) {
                // 当前 - 或者 ~ 输出，如何创建调用一个 term
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
                compileTerm();
            } else if (token.getName().equals("[")) {
                i++;
                sbToken.append("这里在干啥" + i);
                compileExpression();
                break;
            } else {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</term>\n");
    }

    public static void compileExpressionList() {
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("<expressionList>\n");
        blackNum += 2;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.getName().equals(";")
                    || token.getName().equals(")")
                    || token.getName().equals("]")) {
                break;
            } else if (token.getName().equals(",")) {
                sbToken.append(" ".repeat(Math.max(0, blackNum)));
                sbToken.append(token);
                i++;
            } else {
                compileExpression();
            }
        }
        blackNum -= 2;
        sbToken.append(" ".repeat(Math.max(0, blackNum)));
        sbToken.append("</expressionList>\n");
    }

}
