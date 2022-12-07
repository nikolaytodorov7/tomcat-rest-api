package servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mapper.CommentMapper;
import model.StatusMessage;

import java.util.regex.Pattern;

import static util.ServletUtility.*;

public class CommentServlet extends HttpServlet {
    private static CommentMapper mapper = new CommentMapper();
    private static final Pattern COMMENTS_OF_POST_WITH_ID = Pattern.compile("/comments\\?postId=\\d+");

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(resp, msg);
            return;
        }

        String path = buildPath(req);
        if (!COMMENTS_OF_POST_WITH_ID.matcher(path).matches()) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
        }

        String[] split = path.split("=");
        int id = parseInt(split[1]);
        if (id > 0)
            mapper.getCommentsByPostId(id).forEach((p) -> writeAsJson(resp, p));
        else {
            StatusMessage msg = new StatusMessage(400, "Invalid id given in link.");
            writeAsJson(resp, msg);
        }
    }
}
