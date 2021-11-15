import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JackAnalyzer {
    public static void main(String[] args) throws IOException {
        String readFileName = "./Square/Square.jack";
        String parseRes = JackTokenizer.parseFile(readFileName);
//        System.out.println(parseRes);

        String xmlRes = CompilationEngine.traversal();
        String writeFileName = readFileName.substring(0,
                readFileName.lastIndexOf(".")) + "_My.xml";

        BufferedWriter bw = new BufferedWriter(new FileWriter(writeFileName));
        bw.write(xmlRes);
        bw.flush();
        bw.close();
    }
}
