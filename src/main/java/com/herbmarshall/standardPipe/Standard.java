package com.herbmarshall.standardPipe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A wrapper for the standard outputs.
 * The class exists to allow for easy testing of {@link System#out} and {@link System#err} usage.
 */
public final class Standard {

	private static final Set<Standard> knownInstances = ConcurrentHashMap.newKeySet();

	public static final Standard out = new Standard( "OUT", System.out );
	public static final Standard err = new Standard( "ERROR", System.err );

	private final String name;

	private final PrintStream defaultPipe;
	private PrintStream pipe;

	/** Exposed for testing. */
	Standard( String name, PrintStream defaultPipe ) {
		this.name = requireNonNull( name, "name" );
		this.defaultPipe = requireNonNull( defaultPipe, "defaultPipe" );
		knownInstances.add( this );
	}

	/** @see PrintStream#print(String) */
	public void print( String value ) {
		toStream().print( value );
	}

	/** @see PrintStream#println(String) */
	public void println( String value ) {
		toStream().println( value );
	}

	/** Returns the current active {@link PrintStream}. */
	public PrintStream toStream() {
		return Objects.requireNonNullElse( pipe, defaultPipe );
	}

	/**
	 * Replace the default {@link PrintStream} using a {@link java.io.ByteArrayOutputStream}.
	 * @throws IllegalStateException If an override is already in place
	 */
	public void override( ByteArrayOutputStream pipe ) {
		if ( this.pipe != null ) throw new IllegalStateException( doubleOverrideError( name ) );
		this.pipe = new PrintStream( requireNonNull( pipe, "pipe" ) );
	}

	/**
	 * Replace the default {@link PrintStream}.
	 * @throws IllegalStateException If an override is already in place
	 */
	public void override( PrintStream pipe ) {
		if ( this.pipe != null ) throw new IllegalStateException( doubleOverrideError( name ) );
		this.pipe = requireNonNull( pipe, "pipe" );
	}

	/** Use the default {@link PrintStream}. */
	public void reset() {
		this.pipe = null;
	}

	private <T> T requireNonNull( T value, String name ) {
		return Objects.requireNonNull( value, nullPointerError( name ) );
	}

	/** Will call reset of all known {@link Standard}. */
	public static void resetAll() {
		knownInstances.forEach( Standard::reset );
	}

	static String doubleOverrideError( String pipeName ) {
		return "Double override of standard pipe " + pipeName;
	}

	static String nullPointerError( String parameterName ) {
		return "Value of " + parameterName + " cannot be null";
	}

}
