package com.fabrizio.tesi.gateway.auth.exception;

public class WamsAuthenticationException extends Exception {

	private static final long serialVersionUID = 1L;

	private boolean missingRoles = false;
	
	public WamsAuthenticationException() {
		super();
	}

	public WamsAuthenticationException(boolean missingRoles) {
		super();
		this.missingRoles = missingRoles;
	}

	public boolean isMissingRoles() {
		return missingRoles;
	}
}
