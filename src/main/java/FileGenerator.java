import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileGenerator {

    private final static String dataOutputFilePath = "D:\\Informatyka\\Politechnika\\Oculus\\Dane\\";

    public static void saveArticleToFile(WikipediaPageData pageData) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(dataOutputFilePath.concat(pageData.getTitle()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(pageData.getTitle().concat("\n"));
        printWriter.print(pageData.getContent());
        printWriter.close();
    }
}
