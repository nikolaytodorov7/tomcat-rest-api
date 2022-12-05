package mapper;

import model.Post;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class PostMapper {
    private static SqlSessionFactory factory;

    public PostMapper() {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(in, properties);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    public List<Post> getAllPosts() {
        try (SqlSession session = factory.openSession(true)) {
            return session.selectList("getAllPosts");
        }
    }

    public Post getPostById(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            return session.selectOne("getPostById", id);
        }
    }

    public void insertPost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession(true)) {
            session.insert("insertPost", post);
        }
    }

    public void updatePost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession(true)) {
            session.update("updatePost", post);
        }
    }

    public void deletePost(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            session.delete("deletePost", id);
        }
    }

    private void validateId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("Id must be positive!");
    }

    private void validatePost(Post post) {
        if (post == null)
            throw new IllegalArgumentException("Post can't be null!");
    }
}
