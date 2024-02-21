package com.herbmarshall.standardPipe;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A wrapper for the standard outputs.
 * The class exists to allow for easy testing of {@link System#out} and {@link System#err} usage.
 */
public final class Standard {

	public static final Standard out = new Standard( "OUT", System.out );
	public static final Standard err = new Standard( "ERROR", System.err );

	private final DivergingOutputStream pipe;

	/** Exposed for testing. */
	Standard( String name, OutputStream pipe ) {
		this.pipe = new DivergingOutputStream( name, Objects.requireNonNull( pipe ) );
	}

	/** @see PrintStream#print(String) */
	public void print( String value ) {
		pipe.print( value );
	}

	/** @see PrintStream#println(String) */
	public void println( String value ) {
		pipe.println( value );
	}

	/** Returns the internal {@link OutputStream}. */
	public OutputStream toStream() {
		return pipe;
	}

	/**
	 * Replace the default {@link PrintStream} using a {@link java.io.ByteArrayOutputStream}.
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(OutputStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( ByteArrayOutputStream buffer ) {
		override( new PrintStream( buffer ) );
	}

	/**
	 * Replace the default {@link PrintStream} with {@link ByteArrayOutputStream} while executing {@code action}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Consumer} that will accept the updated {@link PrintStream}
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(OutputStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( ByteArrayOutputStream buffer, Consumer<PrintStream> action ) {
		override( new PrintStream( buffer ), action );
	}

	/**
	 * Replace the default {@link PrintStream}.
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(OutputStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( PrintStream pipe ) {
		this.pipe.override( pipe );
	}

	/**
	 * Replace the default {@link PrintStream} while executing {@code action}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Consumer} that will accept {@code pipe}
	 * @throws IllegalStateException If an override is already in place
	 * @deprecated Please use {@link Standard#withOverride(OutputStream)}
	 */
	@Deprecated( since = "1.7", forRemoval = true )
	public void override( PrintStream pipe, Consumer<PrintStream> action ) {
		override( pipe );
		try {
			action.accept( new PrintStream( this.pipe ) );
		}
		finally {
			reset();
		}
	}

	/**
	 * Will create an {@link OverridePlan} for simple overriding of this {@link Standard}.
	 * @param stream The {@link OutputStream} to use for the override.
	 * @return A new {@link OverridePlan}.
	 * @throws AssertionError if {@code stream} is null
	 */
	public OverridePlan withOverride( OutputStream stream ) {
		return new OverridePlan( pipe, stream );
	}

	/**
	 * Use the default {@link PrintStream}.
	 * @deprecated Please use {@link Standard#withOverride(OutputStream)}
	 */
	@Deprecated( since = "1.8", forRemoval = true )
	public void reset() {
		this.pipe.reset();
	}

	/**
	 * Will call reset of all known {@link Standard}.
	 * @throws UnsupportedOperationException Everytime
	 * @deprecated Just don't
	 */
	@Deprecated( since = "1.8", forRemoval = true )
	public static void resetAll() {
		throw new UnsupportedOperationException();
	}

}
