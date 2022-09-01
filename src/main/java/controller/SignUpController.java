package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class SignUpController extends AbstractController implements Controller {
    private final Logger log = getLogger(SignUpController.class);

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        DataBase.addUser(user);
        log.debug("Add User to DataBase : {}", user);

        response.sendRedirect("/index.html");
    }
}
