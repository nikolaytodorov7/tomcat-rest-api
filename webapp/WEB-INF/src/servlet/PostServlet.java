package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.PostMapper;
import model.Post;
import model.Posts;

import java.io.*;
import java.util.stream.Collectors;

import static util.NumberParser.parseInt;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class PostServlet extends HttpServlet {
    private static PostMapper mapper = new PostMapper();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            mapper.getAllPosts().forEach((p) -> writeAsJson(resp, p));
            return;
        }

        String[] pathInfoParts = pathInfo.substring(1).split("/");
        int numberOfParts = pathInfoParts.length;
        switch (numberOfParts) {
            case 1 -> {
                int id = parseInt(pathInfoParts[0]);
                if (id > 0)
                    writeAsJson(resp, mapper.getPostById(id));
                else
                    writeAsJsonNull(resp);
            }
            case 2 -> {
                if (pathInfoParts[1].equals("comments")) {
                    int id = parseInt(pathInfoParts[0]);
                    if (id < 0) {
                        writeAsJsonNull(resp);
                        return;
                    }

                    RequestDispatcher dispatcher = req.getRequestDispatcher("/comments?postId=" + id);
                    dispatcher.forward(req, resp);
                }
            }
            default -> writeAsJsonNull(resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Posts posts = gson.fromJson(body, Posts.class);
        if (posts == null) {
            writeAsJsonNull(resp);
            return;
        }

        posts.posts.forEach((p) -> {
            mapper.insertPost(p);
            writeAsJson(resp, p);
        });
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Post post = gson.fromJson(body, Post.class);
        if (post == null)
            writeAsJsonNull(resp);

        mapper.updatePost(post);
        writeAsJson(resp, post);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            writeAsJsonNull(resp);
            return;
        }

        String[] pathInfoParts = pathInfo.substring(1).split("/");
        if (pathInfoParts.length != 1) {
            writeAsJsonNull(resp);
            return;
        }

        int id = parseInt(pathInfoParts[0]);
        if (id > 0) {
            Post post = mapper.getPostById(id);
            mapper.deletePost(id);
            writeAsJson(resp, post);
        } else
            writeAsJsonNull(resp);
    }
}
