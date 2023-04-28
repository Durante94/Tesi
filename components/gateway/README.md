# REST API
prefisso /gateway
Lista delle REST API.

## /lang

	GET /gateway/lang/{lang}

Imposta all'utente loggato la lingua indicata nel parametro "lang" alla fine del path della richiesta

## /auth

	GET /gateway/auth

Endpoint richiamato dal provider di autenticazione che iniziaizza la navigazione sul sistema

## /auth/refresh

	GET /gateway/auth/refresh

Richiesta che, eseguita ciclicamente, mantiene la sessione attiva

## /auth/logout

	POST /gateway/auth/logout

Esegue la logout dell'utente loggato dal sistema