package com.herbmarshall.standardPipe;

import java.io.PrintStream;
import java.util.Objects;

/**
 * A wrapper for the standard outputs.
 * The class exists to allow for easy testing of {@link System#out} and {@link System#err} usage.
 */
public final class Standard {

	static final String DOUBLE_OVERRIDE_ERROR_TEMPLATE = "Double override of standard pipe: %s";

	public static final Standard out = new Standard( "OUT", System.out );
	public static final Standard err = new Standard( "ERROR", System.err );

	private final String name;

	private final PrintStream defaultPipe;
	private PrintStream pipe;

	/** Exposed for testing. */
	Standard( String name, PrintStream defaultPipe ) {
		this.name = Objects.requireNonNull( name );
		this.defaultPipe = Objects.requireNonNull( defaultPipe );
	}

	/** @see PrintStream#print(String) */
	public void print( String value ) {
		resolve().print( value );
	}

	/** @see PrintStream#println(String) */
	public void println( String value ) {
		resolve().println( value );
	}

	private PrintStream resolve() {
		return Objects.requireNonNullElse( pipe, defaultPipe );
	}

	/**
	 * Replace the default {@link PrintStream}.
	 * @throws IllegalStateException If an override is already in place
	 */
	public void override( PrintStream pipe ) {
		if ( this.pipe != null ) throw new IllegalStateException( DOUBLE_OVERRIDE_ERROR_TEMPLATE.formatted( name ) );
		this.pipe = Objects.requireNonNull( pipe );
	}

	/** Use the default {@link PrintStream}. */
	public void reset() {
		this.pipe = null;
	}

}
