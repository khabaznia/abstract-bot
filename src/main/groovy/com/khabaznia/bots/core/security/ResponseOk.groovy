package com.khabaznia.bots.core.security

import javax.servlet.http.HttpServletResponse

trait ResponseOk {

    void setOk(HttpServletResponse response) {
        response.addHeader "HTTP Status OK", "OK"
        response.setStatus HttpServletResponse.SC_OK
    }
}