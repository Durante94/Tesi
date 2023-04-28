package com.algowatt.wams.auth.exception;

public class MyAuthenticationException extends Exception {

	private static final long serialVersionUID = 1L;

	private boolean missingRoles = false;
	
	public MyAuthenticationException() {
		super();
	}

	public MyAuthenticationException(boolean missingRoles) {
		super();
		this.missingRoles = missingRoles;
	}

	public boolean isMissingRoles() {
		return missingRoles;
	}
}
