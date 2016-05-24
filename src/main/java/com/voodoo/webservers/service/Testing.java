package com.voodoo.webservers.service;

import javax.jws.WebMethod;

/**
 * Created by voodoo on 25/5/16.
 */
public interface Testing {

    @WebMethod
    String voodooTest(String input);
}
