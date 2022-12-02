package mapper;

import model.Comment;
import model.Post;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class CommentMapper {
    private SqlSessionFactory factory;

    public CommentMapper(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public List<Comment> getCommentByPostId(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getCommentByPostId", id);
        }
    }

    private void validateId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("Id must be positive!");
    }

    private void validateComment(Comment comment) {
        if (comment == null)
            throw new IllegalArgumentException("Comment can't be null!");
    }
}
