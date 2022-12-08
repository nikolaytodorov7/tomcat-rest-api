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

    public int insertPost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession(true)) {
            return session.insert("insertPost", post);
        }
    }

    public int updatePost(Post post) {
        validatePost(post);
        try (SqlSession session = factory.openSession(true)) {
            return session.update("updatePost", post);
        }
    }

    public int deletePost(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            return session.delete("deletePost", id);
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
