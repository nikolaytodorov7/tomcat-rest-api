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
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static util.NumberParser.parseInt;
import static util.PathBuilder.buildPath;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class PostServlet extends HttpServlet {
    private static PostMapper mapper = new PostMapper();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern ALL_POSTS_PATTERN = Pattern.compile("/posts");
    private static final Pattern POST_WITH_ID_PATTERN = Pattern.compile("/posts/\\d+");
    private static final Pattern COMMENTS_OF_POST_WITH_ID = Pattern.compile("/posts/\\d+/comments");

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(SC_UNAUTHORIZED);
            return;
        }

        String path = buildPath(req);
        if (ALL_POSTS_PATTERN.matcher(path).matches()) {
            mapper.getAllPosts().forEach((p) -> writeAsJson(resp, p));
            return;
        }

        String[] pathParts = path.substring(1).split("/");
        if (POST_WITH_ID_PATTERN.matcher(path).matches()) {
            int id = parseInt(pathParts[1]);
            if (id > 0)
                writeAsJson(resp, mapper.getPostById(id));
            else {
                resp.sendError(SC_BAD_REQUEST);
                writeAsJsonNull(resp);
            }

            return;
        }

        if (COMMENTS_OF_POST_WITH_ID.matcher(path).matches()) {
            int id = parseInt(pathParts[1]);
            if (id < 0) {
                resp.sendError(SC_BAD_REQUEST);
                writeAsJsonNull(resp);
                return;
            }

            RequestDispatcher dispatcher = req.getRequestDispatcher("/comments?postId=" + id);
            dispatcher.forward(req, resp);
            return;
        }

        writeAsJsonNull(resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(SC_UNAUTHORIZED);
            writeAsJsonNull(resp);
            return;
        }

        String path = buildPath(req);
        if (!ALL_POSTS_PATTERN.matcher(path).matches()) {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
            return;
        }

        Collector<CharSequence, ?, String> joining = Collectors.joining(System.lineSeparator());
        String body = req.getReader().lines().collect(joining);
        Post post = gson.fromJson(body, Post.class);
        if (post == null) {
            writeAsJsonNull(resp);
            return;
        }

        mapper.insertPost(post);
        writeAsJson(resp, post);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(SC_UNAUTHORIZED);
            writeAsJsonNull(resp);
            return;
        }

        String path = buildPath(req);
        if (!POST_WITH_ID_PATTERN.matcher(path).matches()) {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
            return;
        }

        Collector<CharSequence, ?, String> joining = Collectors.joining(System.lineSeparator());
        String body = req.getReader().lines().collect(joining);
        Post post = gson.fromJson(body, Post.class);
        if (post == null)
            writeAsJsonNull(resp);

        mapper.updatePost(post);
        writeAsJson(resp, post);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(SC_UNAUTHORIZED);
            writeAsJsonNull(resp);
            return;
        }

        String path = buildPath(req);
        if (!POST_WITH_ID_PATTERN.matcher(path).matches()) {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
            return;
        }

        String[] pathParts = path.substring(1).split("/");
        if (pathParts.length != 2) {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
            return;
        }

        int id = parseInt(pathParts[0]);
        if (id > 0) {
            Post post = mapper.getPostById(id);
            mapper.deletePost(id);
            writeAsJson(resp, post);
        } else {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
        }
    }
}
