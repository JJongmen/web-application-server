package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Collection;

import static util.HttpRequestUtils.parseCookies;

public class UserListController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogined(request)) {
            response.sendRedirect("/user/login.html");
            return;
        }
        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }

    private static boolean isLogined(HttpRequest request) {
        String logined = request.getCookie("logined");
        if (logined == null) {
            return false;
        }
        return Boolean.parseBoolean(logined);
    }
}
