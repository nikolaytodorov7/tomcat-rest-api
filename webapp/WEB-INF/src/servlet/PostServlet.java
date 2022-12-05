package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.PostMapper;
import model.Post;
import model.Posts;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

import static util.NumberParser.parseInt;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class PostServlet extends HttpServlet {
    private static PostMapper mapper;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        File file = new File("C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\resources\\mybatis-config.xml");
        Properties properties = new Properties();
        String propertiesPath = "C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\src\\util\\jdbc.properties";
        try (InputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new FileReader(propertiesPath))) {
            properties.load(reader);
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            mapper = new PostMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Post post = gson.fromJson(body, Post.class);
        if (post == null)
            writeAsJsonNull(resp);

        mapper.updatePost(post);
        writeAsJson(resp, post);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
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
