package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.UserMapper;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {
    private static UserMapper mapper = new UserMapper();
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
        String username = req.getParameter("user");
        String password = req.getParameter("pass");

        PrintWriter out = resp.getWriter();
        User user = mapper.getUserByUsername(username);
        if (user == null || !user.password.equals(password)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("<html><body>UNAUTHORIZED</body></html>");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        out.println("<html><body>WELCOME</body></html>");
    }
}
