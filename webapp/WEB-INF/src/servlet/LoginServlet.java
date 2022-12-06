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
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class LoginServlet extends HttpServlet {
    private static UserMapper mapper = new UserMapper();
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login");
    private static final String HTML_LOGIN_FORM = """
            <html>
              <body>
                <form action="login" method="post">
                  <div class="container">
                    <label for="user"><b>Username</b></label>
                    <input type="text" placeholder="Enter Username" name="user" required>
                    
                    <label for="pass"><b>Password</b></label>
                    <input type="text" placeholder="Enter Password" name="pass" required>
                    
                    <button type="submit">Login</button>
                  </div>
                </form>
              </body>
            </html>
            """;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println(HTML_LOGIN_FORM);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = buildPath(req);
        if (!LOGIN_PATTERN.matcher(path).matches()) {
            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
            return;
        }

        String username = req.getParameter("user");
        String password = req.getParameter("pass");

        PrintWriter out = resp.getWriter();
        User user = mapper.getUserByUsername(username);
        if (user == null || !user.password.equals(password)) {
            resp.setStatus(SC_UNAUTHORIZED);
            out.println("UNAUTHORIZED");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        out.println("WELCOME");
    }
}
