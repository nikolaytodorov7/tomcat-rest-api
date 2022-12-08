package mapper;

import model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class UserMapper {
    private SqlSessionFactory factory;

    public UserMapper(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public User getUserByUsername(String username) {
        validateUsername(username);
        try (SqlSession session = factory.openSession(true)) {
            return session.selectOne("getUserByUsername", username);
        }
    }

    public void insertUser(User user) {
        validateUser(user);
        try (SqlSession session = factory.openSession(true)) {
            session.insert("insertUser", user);
        }
    }

    private void validateUsername(String username) {
        if (username == null)
            throw new IllegalArgumentException("Username can't be null!");
    }

    private void validateUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("User can't be null!");
    }
}
