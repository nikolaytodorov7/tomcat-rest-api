package mapper;

import model.Post;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class PostMapper {
    private SqlSessionFactory factory;

    public PostMapper(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public List<Post> getAllPosts() {
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getAllPosts");
        }
    }

    public Post getPostById(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession()) {
            return session.selectOne("getPostById", id);
        }
    }

    public void insertPost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession()) {
            session.insert("insertPost", post);
        }
    }

    public void updatePost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession()) {
            session.update("updatePost", post);
        }
    }

    public void deletePost(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession()) {
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
