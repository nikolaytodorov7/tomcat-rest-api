package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.StatusMessage;

import java.io.IOException;

import static util.ServletUtility.writeAsJson;

public class AuthenticationFilter extends HttpFilter {
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (req.getHeader("token") == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active token, please log in.");
            writeAsJson(resp, msg);
            return;
        }

        chain.doFilter(req, resp);
    }
}