package booking;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Functions {

    public static String readJSON(String path) throws IOException {
        return new String (Files.readAllBytes(Paths.get(path)));
    }

    /* public static void makeFile(String path, String data, String filename) {
        
    } */

} 