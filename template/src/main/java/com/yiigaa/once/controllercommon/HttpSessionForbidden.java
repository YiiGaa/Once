package com.yiigaa.once.controllercommon;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

//If you want to forbidden session, open ../xxxApplication.java annotation
public class HttpSessionForbidden extends HttpServletRequestWrapper{
    public HttpSessionForbidden(HttpServletRequest request) {
        super(request);
    }

    @Override
    public HttpSession getSession() {
        return Mockito.mock(HttpSession.class);
    }

    @Override
    public HttpSession getSession(boolean create) {
        return Mockito.mock(HttpSession.class);
    }

}
