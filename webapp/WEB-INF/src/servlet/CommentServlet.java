package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.CommentMapper;

import java.io.IOException;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static util.NumberParser.parseInt;
import static util.PathBuilder.buildPath;
import static util.PrintWriterJson.writeAsJson;
import static util.PrintWriterJson.writeAsJsonNull;

public class CommentServlet extends HttpServlet {
    private static CommentMapper mapper = new CommentMapper();
    private static final Pattern COMMENTS_OF_POST_WITH_ID = Pattern.compile("/comments\\?postId=\\d+");

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(SC_UNAUTHORIZED);
            return;
        }

        String path = buildPath(req);
        if (COMMENTS_OF_POST_WITH_ID.matcher(path).matches()) {
            String[] split = path.split("=");
            int id = parseInt(split[1]);
            if (id > 0) {
                mapper.getCommentsByPostId(id).forEach((p) -> writeAsJson(resp, p));
                return;
            }

            resp.sendError(SC_BAD_REQUEST);
            writeAsJsonNull(resp);
        }
    }
}
