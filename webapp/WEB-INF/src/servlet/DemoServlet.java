package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.CommentMapper;
import mapper.PostMapper;
import model.Comment;
import model.Post;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DemoServlet extends HttpServlet {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static PostMapper postMapper;
    private static CommentMapper commentMapper;

    static {
        File file = new File("C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\resources\\mybatis-config.xml");
        Properties properties = new Properties();
        String propertiesPath = "C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\src\\util\\jdbc.properties";
        try (InputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new FileReader(propertiesPath))) {
            properties.load(reader);
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            postMapper = new PostMapper(factory);
            commentMapper = new CommentMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String servletPath = req.getPathInfo();
        processLinks(out, servletPath);
    }

    private void processLinks(PrintWriter out, String servletPath) {
        if (servletPath == null || servletPath.equals("/")) {
            getAllPosts(out, postMapper);
            return;
        }

        String[] partsPath = servletPath.substring(1).split("/");
        int splittedPathLen = partsPath.length;
        switch (splittedPathLen) {
            case 1 -> {
                int id = Integer.parseInt(partsPath[0]);
                getPostById(out, postMapper, id);
            }
            case 2 -> {
                System.out.println(Arrays.toString(partsPath));
                int id = Integer.parseInt(partsPath[0]);
                if (partsPath[1].equals("comments")) {
                    getCommentsByPostId(out, id);
                }
            }
        }

        if (servletPath.startsWith("comments?postId=")) {
            String[] pathParts = servletPath.substring(1).split("=");
            if (pathParts.length == 2) {
                int id = Integer.parseInt(partsPath[1]);
                getCommentsByPostId(out, id);
            }
        }
    }

    private void getCommentsByPostId(PrintWriter out, int id) {
        List<Comment> comments = commentMapper.getCommentByPostId(id);
        for (Comment comment : comments) {
            String jsonComment = gson.toJson(comment);
            out.println(jsonComment);
        }

    }

    private void getAllPosts(PrintWriter out, PostMapper mapper) {
        List<Post> posts = mapper.getAllPosts();
        for (Post post : posts) {
            String jsonPost = gson.toJson(post);
            out.println(jsonPost);
        }
    }

    private void getPostById(PrintWriter out, PostMapper mapper, int id) {
        Post post = mapper.getPostById(id);
        String jsonPost = gson.toJson(post);
        out.println(jsonPost);
    }
}
