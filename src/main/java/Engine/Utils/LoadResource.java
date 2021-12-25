package Engine.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoadResource {

    public static String loadResource(String path) throws IOException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        StringBuilder res = new StringBuilder();

        while(sc.hasNextLine())
        {
            res.append(sc.nextLine()).append("\n");
        }
        return res.toString();

    }

    public static List<String> readAllLines(String path) throws IOException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        List<String> output = new ArrayList<>();
        while(sc.hasNextLine())
        {
            output.add(sc.nextLine());
        }
        return output;
    }
}
