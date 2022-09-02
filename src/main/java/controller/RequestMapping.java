package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/user/list", new UserListController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/create", new SignUpController());
    }

    public static Controller getController(String path) {
        return controllerMap.get(path);
    }
}
