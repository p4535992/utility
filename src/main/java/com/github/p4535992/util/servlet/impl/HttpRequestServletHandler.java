package com.github.p4535992.util.servlet.impl;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 4535992 on 01/12/2015.
 */
public class HttpRequestServletHandler {

    public void doSomething() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        doSomething(request);
    }

    void doSomething(HttpServletRequest request) {
        // put your business logic here, and test this method
    }
}
