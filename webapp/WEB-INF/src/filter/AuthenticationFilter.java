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
        String path = buildPath(req);
        if (LOGIN_PATTERN.matcher(path).matches() || REGISTER_PATTERN.matcher(path).matches()) {
            chain.doFilter(req, resp);
            session = req.getSession();
            User sessionUser = (User) session.getAttribute("user");
            user = sessionUser.username;
            String logMessage = String.format("%s %s", user, req.getServletPath());
            LOGGER.info(logMessage);
            return;
        }

        if (session == null) {
            String logMessage = String.format("%s %s", user, path);
            LOGGER.error(logMessage);
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(resp, msg);
            return;
        }

        String logMessage = String.format("%s %s", user, path);
        LOGGER.info(logMessage);
        chain.doFilter(req, resp);
    }
}
