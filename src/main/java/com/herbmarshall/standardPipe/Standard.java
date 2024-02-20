package com.herbmarshall.standardPipe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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
		return resolvePipe();
	}

	/**
	 * Replace the default {@link PrintStream} using a {@link java.io.ByteArrayOutputStream}.
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(ByteArrayOutputStream)} or
	 *      {@link Standard#withOverride(PrintStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )  // Will be moved to package-private
	public void override( ByteArrayOutputStream buffer ) {
		override( createPipe( buffer ) );
	}

	/**
	 * Replace the default {@link PrintStream} with {@link ByteArrayOutputStream} while executing {@code action}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Consumer} that will accept the updated {@link PrintStream}
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(ByteArrayOutputStream)} or
	 *      {@link Standard#withOverride(PrintStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( ByteArrayOutputStream buffer, Consumer<PrintStream> action ) {
		override( createPipe( buffer ), action );
	}

	/**
	 * Replace the default {@link PrintStream}.
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(ByteArrayOutputStream)} or
	 *      {@link Standard#withOverride(PrintStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )  // Will be moved to package-private
	public void override( PrintStream pipe ) {
		if ( this.pipe != null ) throw new IllegalStateException( doubleOverrideError( name ) );
		this.pipe = requireNonNull( pipe, "pipe" );
	}

	/**
	 * Replace the default {@link PrintStream} while executing {@code action}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Consumer} that will accept {@code pipe}
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(ByteArrayOutputStream)} or
	 *      {@link Standard#withOverride(PrintStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( PrintStream pipe, Consumer<PrintStream> action ) {
		override( pipe );
		try {
			action.accept( this.pipe );
		}
		finally {
			reset();
		}
	}

	/**
	 * Will create an {@link OverridePlan} for simple overriding of this {@link Standard}.
	 * @param stream The {@link ByteArrayOutputStream} to use for the override.
	 * @return A new {@link OverridePlan}.
	 * @throws AssertionError if {@code stream} is null
	 * @see #withOverride(PrintStream)
	 */
	public OverridePlan withOverride( ByteArrayOutputStream stream ) {
		return withOverride( new PrintStream( stream ) );
	}

	/**
	 * Will create an {@link OverridePlan} for simple overriding of this {@link Standard}.
	 * @param pipe The {@link PrintStream} to use for the override.
	 * @return A new {@link OverridePlan}.
	 * @throws AssertionError if {@code pipe} is null
	 */
	public OverridePlan withOverride( PrintStream pipe ) {
		return new OverridePlan( this, pipe );
	}

	/** Use the default {@link PrintStream}. */
	public void reset() {
		this.pipe = null;
	}

	private PrintStream resolvePipe() {
		return Objects.requireNonNullElse( pipe, defaultPipe );
	}

	private PrintStream createPipe( ByteArrayOutputStream buffer ) {
		return new PrintStream( requireNonNull( buffer, "buffer" ) );
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
