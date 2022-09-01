package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractController implements Controller {
    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        User findUser = DataBase.findUserById(request.getParameter("userId"));
        if (correctLoginInfo(request, findUser)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/user/login_failed.html");
    }

    private static boolean correctLoginInfo(HttpRequest request, User findUser) {
        return findUser != null && findUser.getPassword().equals(request.getParameter("password"));
    }
}
