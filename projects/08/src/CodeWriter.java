import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"all"})
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
        // 构造sb的时候，进行一个初始化代码 writeInit()
        StringBuilder sb = new StringBuilder(writeInit());
        for (Command command : commands) {
            switch (command.getType()) {
                case CommandType.C_ARITHMETIC -> sb.append(writeArithmetic(command));
                case CommandType.C_POP, CommandType.C_PUSH -> sb.append(writePushPop(command));
                case CommandType.C_LABEL -> sb.append(writeLabel(command));
                case CommandType.C_GOTO -> sb.append(writeGoto(command));
                case CommandType.C_IF -> sb.append(writeIF(command));
                case CommandType.C_FUNCTION -> sb.append(writeFunction(command));
                case CommandType.C_RETURN -> sb.append(writeReturn(command));
                case CommandType.C_CALL -> sb.append(writeCall(command));
                default -> System.out.println("未处理1");
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
                case "static" -> res.append("@" + cmd.getArg2() + "." + cmd.getArg3() + "\n" + "D=M\n" + op2);
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
                case "static" -> res.append("@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@" + cmd.getArg2() + "." + cmd.getArg3() + "\n" +
                        "M=D\n");
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

    // label标签的处理
    public static String writeLabel(Command cmd) {
        return "(" + cmd.getArg2() + ")\n";
    }

    // goto语句的处理
    public static String writeGoto(Command cmd) {
        return "@" + cmd.getArg2() + "\n0;JMP\n";
    }

    // if-goto语句的处理
    public static String writeIF(Command cmd) {
        return "@SP\n"  +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + cmd.getArg2() + "\n" +
                "D;JNE\n";
    }

    // function 语句的处理
    public static String writeFunction(Command cmd) {
        StringBuilder sb = new StringBuilder("(" + cmd.getArg2() + ")\n");
        int k = Integer.parseInt(cmd.getArg3());        // k 个局部变量
        for (int i = 0; i < k; i++) {
            // 创建一个 cmd 对象，然后调用已经实现的 push 0
            Command cmd1 = new Command("push", "constant", "0");
            cmd1.setType(CommandType.C_PUSH);
            sb.append(writePushPop(cmd1));
        }
        return sb.toString();
    }

    private static int callNum = 0;
    public static String writeCall(Command cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("@" + cmd.getArg2() + "return_address" + callNum + "\n" +
                "D=A\n" +op2);
        // push {"LCL", "ARG", "THIS", "THAT"}
        String[] arr = {"LCL", "ARG", "THIS", "THAT"};
        for (String s : arr) {
            sb.append("@" + s + "\n" +
                    "D=M\n" + op2);
        }
        // ARG = SP - n - 5
        sb.append("@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" +
                "@" + cmd.getArg3() + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n");
        // LCL = SP
        sb.append("@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n");
        // goto f
        sb.append("@" + cmd.getArg2() + "\n0;JMP\n");
        // (return-address)
        sb.append("(" + cmd.getArg2() + "return_address" + callNum++ + ")\n");
        return sb.toString();
    }

    // return 语句的处理
    public static String writeReturn(Command cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                @LCL
                D=M
                @R13
                M=D
                @5
                A=D-A
                D=M
                @R14
                M=D
                @SP
                AM=M-1
                D=M
                @ARG
                A=M
                M=D
                @ARG
                D=M+1
                @SP
                M=D
                @R13
                D=M
                @1
                A=D-A
                D=M
                @THAT
                M=D
                @R13
                D=M
                @2
                A=D-A
                D=M
                @THIS
                M=D
                @R13
                D=M
                @3
                A=D-A
                D=M
                @ARG
                M=D
                @R13
                D=M
                @4
                A=D-A
                D=M
                @LCL
                M=D
                @R14
                A=M
                0;JMP
                """);
        return sb.toString();
    }

    // 初始化代码 SP -> RAM[256]
    public static String writeInit() {
        StringBuilder sb = new StringBuilder();
        sb.append("@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n");
        // 创建一个初始化调用 Sys.init 命令
        Command cmd = new Command("call", "Sys.init", "0", CommandType.C_CALL);
        sb.append(writeCall(cmd));
        return sb.toString();
    }

}
