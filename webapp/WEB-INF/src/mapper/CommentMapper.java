package mapper;

import model.Comment;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class CommentMapper {
    private static SqlSessionFactory factory;

    public CommentMapper() {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(in, properties);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    public List<Comment> getCommentsByPostId(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            return session.selectList("getCommentByPostId", id);
        }
    }

    private void validateId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("Id must be positive!");
    }
}
