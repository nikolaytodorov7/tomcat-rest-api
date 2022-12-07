package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.UserMapper;
import model.StatusMessage;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static util.ServletUtility.*;

public class LoginServlet extends HttpServlet {
    private static UserMapper mapper = new UserMapper();
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login\\?user=\\w+&pass=\\w+");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register\\?user=\\w+&pass=\\w+");

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        String username = req.getParameter("user");
        String password = req.getParameter("pass");
        password = getEncryptedPassword(password);

        PrintWriter out = resp.getWriter();
        if (LOGIN_PATTERN.matcher(path).matches()) {
            User user = mapper.getUserByUsername(username);
            if (user == null) {
                StatusMessage msg = new StatusMessage(
                        403, "User with username: '" + username + "' does not exist.");
                writeAsJson(resp, msg);
                return;
            }

            if (!user.password.equals(password)) {
                StatusMessage msg = new StatusMessage(403, "Invalid password.");
                writeAsJson(resp, msg);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
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

            User user = new User();
            user.username = username;
            user.password = password;
            mapper.insertUser(user);
            out.println("Registration is successfully completed.");
            return;
        }

        StatusMessage msg = new StatusMessage(400, "Invalid link.");
        writeAsJson(resp, msg);
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
}
