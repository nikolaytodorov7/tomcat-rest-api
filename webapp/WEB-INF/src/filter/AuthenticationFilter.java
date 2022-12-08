package filter;

import jakarta.servlet.*;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StatusMessage;
import model.User;

import java.io.IOException;
import java.util.logging.*;
import java.util.regex.Pattern;

import static util.FileLogger.log;
import static util.ServletUtility.buildPath;
import static util.ServletUtility.writeAsJson;

public class AuthenticationFilter implements Filter {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("/login\\?user=\\w+&pass=\\w+");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("/register\\?user=\\w+&pass=\\w+");
    private static HttpSession session = null;
    private static String user = null;

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String method = ((HttpServletRequest) req).getMethod();
        String servletPath = ((HttpServletRequest) req).getServletPath();
        String path = buildPath(request);
        if (session == null && (LOGIN_PATTERN.matcher(path).matches() || REGISTER_PATTERN.matcher(path).matches())) {
            chain.doFilter(request, response);
            session = request.getSession();
            User sessionUser = (User) session.getAttribute("user");
            user = sessionUser.username;
            sendLog(Level.INFO, user, method, servletPath);
            return;
        }

        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(response, msg);
            sendLog(Level.WARNING, user, method, path);
            return;
        }

        chain.doFilter(request, response);
        sendLog(Level.INFO, user, method, path);
    }

    private void sendLog(Level level, String user, String method, String path) {
        String logMessage = String.format("%s %s %s", user, method, path);
        log(level, logMessage);
    }
}