package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ServletUtility {
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

    public static int parseInt(String str) {
        try {
            int num = Integer.parseInt(str);
            return num > 0 ? num : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String buildPath(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String queryString = req.getQueryString();
        StringBuilder path = new StringBuilder();
        if (servletPath != null)
            path.append(servletPath);

        if (pathInfo != null)
            path.append(pathInfo);

        if (queryString != null)
            path.append("?").append(queryString);

        return path.toString();
    }


}
