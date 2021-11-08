import java.io.*;
import java.util.ArrayList;

public class Parser {
    private static ArrayList<Command> commands = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        String filename = "./ProgramFlow/BasicLoop/BasicLoop.vm";
//        String filename = "./ProgramFlow/FibonacciSeries/FibonacciSeries.vm";
//        String filename = "./FunctionCalls/SimpleFunction/SimpleFunction.vm";
        String file1 = "./FunctionCalls/StaticsTest/Sys.vm";
        parseFile(file1);

        String file2 = "./FunctionCalls/StaticsTest/Class1.vm";
        parseFile(file2);
//
        String file3 = "./FunctionCalls/StaticsTest/Class2.vm";
        parseFile(file3);

        CodeWriter.setCommands(commands);
//        CodeWriter.setFileName("./FunctionCalls/FibonacciElement/FibonacciElement.vm");
        CodeWriter.setFileName("./FunctionCalls/StaticsTest/StaticsTest.vm");

    }

    // 去掉注释，得到 String 文件
    public static void parseFile(String filename) throws IOException {
        StringBuilder res = new StringBuilder();
        BufferedReader buff = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = buff.readLine()) != null) {
            // 剔除为空的行
            if (line.equals("\n") || line.equals("")) {
                continue;
            }

            String[] s = null;
            // 去除注释及两边的空格
            int idx = line.indexOf("//");
            if (idx != -1) {
                s = line.substring(0, idx).strip().split(" ");
            } else {
                s = line.strip().split(" ");
            }
            // 创建cmd对象，并设置其参数，然后放入 cmds 数组中
            Command cmd = new Command();
            String type = getCommandType(s[0]);
            //这里是剔除了注释在开头的情况
            if (type.equals("isNullCMD")) {
                continue;
            }
            cmd.setType(type);
            // 设置cmd对象的参数
            int len = s.length;
            switch (len) {
                case 1 -> cmd.setArg1(s[0]);
                case 2 -> {
                    cmd.setArg1(s[0]);
                    cmd.setArg2(s[1]);
                }
                case 3 -> {
                    cmd.setArg1(s[0]);
                    cmd.setArg2(s[1]);
                    cmd.setArg3(s[2]);
                }
            }
            commands.add(cmd);
        }
        buff.close();
    }

    // 得到待处理cmd对象的类型
    public static String getCommandType(String s) {
        return switch (s) {
            case "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not" -> CommandType.C_ARITHMETIC;
            case "push" -> CommandType.C_PUSH;
            case "pop" -> CommandType.C_POP;
            case "label" -> CommandType.C_LABEL;
            case "goto" -> CommandType.C_GOTO;
            case "if-goto" -> CommandType.C_IF;
            case "function" -> CommandType.C_FUNCTION;
            case "return" -> CommandType.C_RETURN;
            case "call" -> CommandType.C_CALL;
            case "" -> "isNullCMD";// 注释在开始的情况
            default -> "未处理";
        };
    }

}

