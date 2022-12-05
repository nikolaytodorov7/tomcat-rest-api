package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.CommentMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.Properties;

import static util.NumberParser.parseInt;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class CommentServlet extends HttpServlet {
    private static CommentMapper mapper;

    public void init() {
        File file = new File("C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\resources\\mybatis-config.xml");
        Properties properties = new Properties();
        String propertiesPath = "C:\\Projects\\IdeaProjects\\TomApp\\src\\main\\webapp\\WEB-INF\\src\\util\\jdbc.properties";
        try (InputStream in = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new FileReader(propertiesPath))) {
            properties.load(reader);
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            mapper = new CommentMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String servletPath = req.getServletPath();
        if (servletPath.equals("/comments")) {
            String queryString = req.getQueryString();
            String[] splitted = queryString.split("=");
            int id = parseInt(splitted[1]);
            if (id > 0)
                mapper.getCommentsByPostId(id).forEach((p) -> writeAsJson(resp, p));
            else
                writeAsJsonNull(resp);
        }
    }
}
