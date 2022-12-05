package mapper;

import model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UserMapper {
    private static SqlSessionFactory factory;

    public UserMapper() {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(in, properties);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    public User getUserByUsername(String username) {
        validateUsername(username);
        try (SqlSession session = factory.openSession(true)) {
            return session.selectOne("getUserByUsername", username);
        }
    }


    private void validateUsername(String username) {
        if (username == null)
            throw new IllegalArgumentException("Username can't be null!");
    }
}
