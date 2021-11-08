public class Command implements CommandType {
    private String arg1;
    private String arg2;
    private String arg3;
    private String type;


    public Command() {
    }


    public Command(String arg1) {
        this.arg1 = arg1;
    }

    public Command(String arg1, String arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public Command(String arg1, String arg2, String arg3) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    public Command(String arg1, String arg2, String arg3, String type) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.type = type;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Command{" +
                "arg1='" + arg1 + '\'' +
                ", arg2='" + arg2 + '\'' +
                ", arg3='" + arg3 + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
