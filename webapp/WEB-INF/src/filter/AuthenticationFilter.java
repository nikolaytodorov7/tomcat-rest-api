package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StatusMessage;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.regex.Pattern;

import static util.ServletUtility.buildPath;
import static util.ServletUtility.writeAsJson;

public class AuthenticationFilter extends HttpFilter {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login\\?user=\\w+&pass=\\w+");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register\\?user=\\w+&pass=\\w+");
    private static HttpSession session = null;
    private static String user = null;
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class.getName());


    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String method = req.getMethod();
        String servletPath = req.getServletPath();
        String path = buildPath(req);
        if (session == null && (LOGIN_PATTERN.matcher(path).matches() || REGISTER_PATTERN.matcher(path).matches())) {
            chain.doFilter(req, resp);
            session = req.getSession();
            User sessionUser = (User) session.getAttribute("user");
            user = sessionUser.username;
            String logMessage = String.format("%s %s %s", user, method, path);
            LOGGER.info(logMessage);
            return;
        }

        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(resp, msg);
            String logMessage = String.format("%s %s %s", user, method, path);
            LOGGER.info(logMessage);
            return;
        }

        chain.doFilter(req, resp);
        String logMessage = String.format("%s %s %s", user, method, path);
        LOGGER.info(logMessage);
    }
}