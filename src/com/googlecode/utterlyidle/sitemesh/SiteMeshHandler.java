package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class SiteMeshHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final Decorators decorators;

    public SiteMeshHandler(final HttpHandler httpHandler, final Decorators decorators) {
        this.httpHandler = httpHandler;
        this.decorators = decorators;
    }

    public void handle(final Request request, final Response response) throws Exception {
        SiteMeshResponse siteMeshResponse = new SiteMeshResponse(request, response, decorators);
        httpHandler.handle(request, siteMeshResponse);
        siteMeshResponse.flush();
    }

//    public Response handle(Request request){
//        Response response = httpHandler.handle(request);
//        if(shouldDecorate(request, resposne)){
//            return decorate(response);
//        }else{
//            return response;
//        }
//    }
//
//    private Response decorate(Request request, Response response) {
//        Decorator decorator = decorators.getDecoratorFor(request, response);
//        byte[] output = response.output(.);
//        String result = decorator.decorate(output);
//
//    }
}