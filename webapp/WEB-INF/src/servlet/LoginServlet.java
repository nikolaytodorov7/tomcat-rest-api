package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.*;
import mapper.TokenMapper;
import mapper.UserMapper;
import model.StatusMessage;
import model.Token;
import model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import static util.ServletUtility.*;

public class LoginServlet extends HttpServlet {
    private static UserMapper userMapper;
    private static TokenMapper tokenMapper;
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login\\?user=\\w+&pass=\\w+");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register\\?user=\\w+&pass=\\w+");

    public void init(ServletConfig config) {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            userMapper = new UserMapper(factory);
            tokenMapper = new TokenMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        String username = req.getParameter("user");
        String password = req.getParameter("pass");

        PrintWriter out = resp.getWriter();
        if (LOGIN_PATTERN.matcher(path).matches()) {
            User user = userMapper.getUserByUsername(username);
            if (user == null) {
                StatusMessage msg = new StatusMessage(
                        403, "User with username: '" + username + "' does not exist.");
                writeAsJson(resp, msg);
                return;
            }

            password = getEncryptedPassword(password);
            password += user.salt;
            if (!user.password.equals(password)) {
                StatusMessage msg = new StatusMessage(403, "Invalid password.");
                writeAsJson(resp, msg);
                return;
            }

            Token token = tokenMapper.getTokenByUserId(user.id);
            if (token == null || token.expirationDate.getTime() < System.currentTimeMillis()) {
                if (token != null)
                    tokenMapper.deleteToken(user.id);

                String tokenStr = createToken(username);
                token = new Token(tokenStr, user.id);
                tokenMapper.insertToken(token);
            }

            resp.setHeader("Authorization", "Bearer{" + token.token + "}");
//            HttpSession oldSession = req.getSession(false);
//            if (oldSession != null)
//                oldSession.invalidate();
//
//            HttpSession session = req.getSession(true);
//            session.setAttribute("user", user);
            out.println("Welcome " + username);
            return;
        }

        if (REGISTER_PATTERN.matcher(path).matches()) {
            if (username == null) {
                StatusMessage msg = new StatusMessage(403, "Invalid username.");
                writeAsJson(resp, msg);
                return;
            }
            if (password == null) {
                StatusMessage msg = new StatusMessage(403, "Invalid password.");
                writeAsJson(resp, msg);
                return;
            }

            int salt = new Random().nextInt(0, Integer.MAX_VALUE);
            password += salt;
            password = getEncryptedPassword(password);
            User user = new User();
            user.username = username;
            user.password = password;
            user.salt = salt;
            userMapper.insertUser(user);
            out.println("Registration is successfully completed.");
        }
    }

    private String getEncryptedPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(Integer.toHexString(0xFF & b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException ignored) {
        } // ignored because exception is thrown if no Provider supports implementation for the specified algorithm. SHA-1 is supported.

        return null;
    }

    private String createToken(String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            int salt = new Random().nextInt(0, Integer.MAX_VALUE);
            String token = username + salt;
            md.update(token.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(Integer.toHexString(0xFF & b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException ignored) {
        } // ignored because exception is thrown if no Provider supports implementation for the specified algorithm. SHA-1 is supported.

        return null;
    }
}
