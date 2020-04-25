package com.chrisprimes.compilertest;

public class CompilationException extends Exception {
	private static final long serialVersionUID = -8643923682218924453L;

	public CompilationException(Exception e) {
		super(e);
	}
	
	public CompilationException(String message) {
		super(message);
	}
	
	public CompilationException(String message, Exception e) {
		super(message, e);
	}

}
