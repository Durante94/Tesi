package com.fabrizio.tesi.socket.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpResponseMessages {

    public void returnAuthError(HttpServletResponse response) {

        try {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autorizzato");
        } catch (IOException e) {
            log.error("Response error", e);
        }
    }

}
