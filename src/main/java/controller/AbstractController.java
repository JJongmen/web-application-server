package controller;

import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller {
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getMethod()) {
            case "GET" :
                doGet(request, response);
                break;
            case "POST":
                doPost(request, response);
                break;
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);
    protected abstract void doPost(HttpRequest request, HttpResponse response);
}
