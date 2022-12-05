package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class PrintWriterJson {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void writeAsJson(HttpServletResponse resp, Object obj) {
        try {
            PrintWriter out = resp.getWriter();
            String parsed = gson.toJson(obj);
            out.println(parsed);
        } catch (IOException e) { //if the getOutputStream method has already been called for this response object
            System.err.println("Error: Output method has already been called for this response object!");
        }
    }

    public static void writeAsJsonNull(HttpServletResponse resp) {
        try {
            PrintWriter out = resp.getWriter();
            out.println("{}");
        } catch (IOException e) { //if the getOutputStream method has already been called for this response object
            System.err.println("Error: Output method has already been called for this response object!");
        }
    }
}
