import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeWriter {
    // 由parser传递的指令集合
    private static ArrayList<Command> commands = new ArrayList<>();
    private static String writeToFileString;
    private static int ramStatic = 16;
    // 符号表 用来处理Static段
    private static HashMap<String, Integer> ramStaticMap = new HashMap<>();

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static void setCommands(ArrayList<Command> commands) {
        CodeWriter.commands = commands;
    }

    public static void setFileName(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Command command : commands) {
            switch (command.getType()) {
                case CommandType.C_ARITHMETIC:
                    sb.append(writeArithmetic(command));
                    break;
                case CommandType.C_POP:
                case CommandType.C_PUSH:
                    sb.append(writePushPop(command));
                    break;
                default:
                    System.out.println("未处理1");
            }
        }
        writeToFileString = sb.toString();
        System.out.println(writeToFileString);

        String writeFileName = filename.substring(0, filename.lastIndexOf(".")) + ".asm";
        BufferedWriter bw = new BufferedWriter(new FileWriter(writeFileName));
        bw.write(writeToFileString);
        bw.flush();
        bw.close();
    }

    private static final String op;

    static {
        op = "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n";
    }

    // 用来区分不同的判断运算，防止标签相同的情况
    private static int id = 0;
    // 算术运算
    public static String writeArithmetic(Command cmd) {
        StringBuilder res = new StringBuilder();
        switch (cmd.getArg1()) {
            case "add" -> res.append(op).append("M=M+D\n");
            case "sub" -> res.append(op).append("M=M-D\n");
            case "neg" -> res.append("@SP\nAM=M-1\nM=-M\n@SP\nM=M+1\n");
            case "eq" -> res.append(writeJudge("JEQ"));
            case "gt" -> res.append(writeJudge("JGT"));
            case "lt" -> res.append(writeJudge("JLT"));
            case "and" -> res.append(op).append("M=M&D\n");
            case "or" -> res.append(op).append("M=M|D\n");
            case "not" -> res.append("@SP\nAM=M-1\nM=!M\n@SP\nM=M+1\n");
        }
        return res.toString();
    }

    private static String writeJudge(String judge) {
        id++;
        return op + "D=M-D\n" +
                "@LabelTrue" + id + "\n" +
                "D;" + judge +"\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@LabelEND" + id + "\n" +
                "0;JMP\n" +
                "(LabelTrue" + id + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(LabelEND" + id + ")\n";
    }

    // 写入栈中
    private static final String op2 = """
            @SP
            A=M
            M=D
            @SP
            M=M+1
            """;

    // 通过一个 R13 把数值写入栈中
    private static final String op3 = """
            @R13
            M=D
            @SP
            AM=M-1
            D=M
            @R13
            A=M
            M=D
            """;

    public static String writePushPop(Command cmd) {
        StringBuilder res = new StringBuilder();
        if (cmd.getType().equals(CommandType.C_PUSH)) {
            switch (cmd.getArg2()) {
                case "constant" -> res.append("@").append(cmd.getArg3()).append("\nD=A\n").append(op2);
                case "local" -> res.append(createPush("LCL", cmd.getArg3()));
                case "argument" -> res.append(createPush("ARG", cmd.getArg3()));
                case "this" -> res.append(createPush("THIS", cmd.getArg3()));
                case "that" -> res.append(createPush("THAT", cmd.getArg3()));
                case "temp" -> res.append("@" + cmd.getArg3() + "\n" +
                        "D=A\n" +
                        "@5\n" +
                        "A=D+A\n" +
                        "D=M\n" + op2);
                case "pointer" -> res.append("@" + cmd.getArg3() + "\n" +
                        "D=A\n" +
                        "@3\n" +
                        "A=D+A\n" +
                        "D=M\n" + op2);
                case "static" -> res.append("@" + findRamStatic(cmd.getArg3()) + "\n" + "D=M\n" + op2);
                default -> res.append("未处理111\n");
            }
        } else {
            switch (cmd.getArg2()) {
                case "local" -> res.append(createPop("LCL", cmd.getArg3()));
                case "argument" -> res.append(createPop("ARG", cmd.getArg3()));
                case "this" -> res.append(createPop("THIS", cmd.getArg3()));
                case "that" -> res.append(createPop("THAT", cmd.getArg3()));
                case "temp" -> res.append("@" + cmd.getArg3() + "\n" +
                        "D=A\n" +
                        "@5\n" +
                        "D=D+A\n" + op3);
                case "pointer" -> res.append("@" + cmd.getArg3() + "\n" +
                        "D=A\n" +
                        "@3\n" +
                        "D=D+A\n" + op3);
                case "static" -> res.append("@" + findRamStatic(cmd.getArg3()) + "\n" + "D=A\n" + op3);
                default -> res.append("未处理222\n");
            }
        }
        return res.toString();
    }
    // 查找Static变量对应的Ram的值，不存在则重新映射一个，从16开始
    private static int findRamStatic(String staticName) {
        int res;
        if (ramStaticMap.containsKey(staticName)) {
            res = ramStaticMap.get(staticName);
        } else {
            res = ramStatic;
            ramStaticMap.put(staticName, ramStatic++);
        }
        return res;
    }
    // 构建push代码，处理local，argument，this，that段
    private static String createPush(String type, String num) {
        return "@" + type + "\n" +
                "D=M\n" +
                "@" + num + "\n" +
                "A=D+A\n" +
                "D=M\n" + op2;
    }

    // 构建pop代码，处理local，argument，this，that段
    private static String createPop(String type, String num) {
        return "@" + type + "\n" +
                "D=M\n" +
                "@" + num + "\n" +
                "D=D+A\n" +op3;
    }

}
