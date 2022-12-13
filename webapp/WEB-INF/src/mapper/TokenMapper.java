package mapper;

import model.Post;
import model.Token;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class TokenMapper {
    private SqlSessionFactory factory;

    public TokenMapper(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public Token getTokenByUserId(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            return session.selectOne("getTokenByUserId", id);
        }
    }

    public int insertToken(Token token) {
        validateToken(token);
        try (SqlSession session = factory.openSession(true)) {
            return session.insert("insertToken", token);
        }
    }

    public int deleteToken(int id) {
        validateId(id);
        try (SqlSession session = factory.openSession(true)) {
            return session.delete("deleteToken", id);
        }
    }

    private void validateId(int id) {
        if (id < 0)
            throw new IllegalArgumentException("Id must be positive!");
    }

    private void validateToken(Token token) {
        if (token == null)
            throw new IllegalArgumentException("Token can't be null!");
    }
}
