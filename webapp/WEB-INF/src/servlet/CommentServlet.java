package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.CommentMapper;

import java.io.IOException;

import static util.NumberParser.parseInt;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class CommentServlet extends HttpServlet {
    private static CommentMapper mapper = new CommentMapper();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String servletPath = req.getServletPath();
        if (servletPath.equals("/comments")) {
            String queryString = req.getQueryString();
            String[] splitted = queryString.split("=");
            int id = parseInt(splitted[1]);
            if (id > 0)
                mapper.getCommentsByPostId(id).forEach((p) -> writeAsJson(resp, p));
            else
                writeAsJsonNull(resp);
        }
    }
}
