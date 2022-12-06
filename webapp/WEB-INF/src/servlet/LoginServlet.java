package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.UserMapper;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static util.PathBuilder.buildPath;
import static util.PrintWriterJson.writeAsJsonNull;

public class LoginServlet extends HttpServlet {
    private static UserMapper mapper = new UserMapper();
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login\\?user=\\w+&pass=\\w+");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register\\?user=\\w+&pass=\\w+");

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        String username = req.getParameter("user");
        String password = req.getParameter("pass");
        PrintWriter out = resp.getWriter();
        if (LOGIN_PATTERN.matcher(path).matches()) {
            User user = mapper.getUserByUsername(username);
            if (user == null || !user.password.equals(password)) {
                resp.setStatus(SC_UNAUTHORIZED);
                out.println("UNAUTHORIZED");
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            out.println("WELCOME");
            return;
        }

        if (REGISTER_PATTERN.matcher(path).matches()) {
            if (username == null || password == null) {
                resp.sendError(SC_BAD_REQUEST);
                out.println("REGISTRATION UNSUCCESSFUL");
                return;
            }

            User user = new User();
            user.username = username;
            user.password = password;
            mapper.insertUser(user);
            out.println("SUCCESSFUL REGISTRATION");
            return;
        }

        resp.sendError(SC_BAD_REQUEST);
        writeAsJsonNull(resp);
    }
}
