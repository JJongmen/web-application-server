package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import session.HttpSession;

public class LoginController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User findUser = DataBase.findUserById(request.getParameter("userId"));
        if (correctLoginInfo(request, findUser)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", findUser);
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/user/login_failed.html");
    }

    private static boolean correctLoginInfo(HttpRequest request, User findUser) {
        return findUser != null && findUser.getPassword().equals(request.getParameter("password"));
    }
}
