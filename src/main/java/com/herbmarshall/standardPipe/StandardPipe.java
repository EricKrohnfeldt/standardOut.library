package com.herbmarshall.standardPipe;

import java.io.PrintStream;
import java.util.Objects;

/** A central location for determining process output. */
public final class StandardPipe {

	static final PrintStream DEFAULT_OUT = System.out;
	static final PrintStream DEFAULT_ERROR = System.err;

	private static final StandardPipe GLOBAL = new StandardPipe( DEFAULT_OUT, DEFAULT_ERROR );

	private final NamedPrintStreamContainer defaultOut;
	private final NamedPrintStreamContainer defaultError;

	private PrintStream outPipe;
	private PrintStream errorPipe;

	StandardPipe( PrintStream defaultOut, PrintStream defaultError ) {
		this.defaultOut = new NamedPrintStreamContainer( "OUT", Objects.requireNonNull( defaultOut ) );
		this.defaultError = new NamedPrintStreamContainer( "ERROR", Objects.requireNonNull( defaultError ) );
		this.outPipe = this.defaultOut.pipe;
		this.errorPipe = this.defaultError.pipe;
	}

	/** @return The current standard out stream */
	PrintStream out() {
		return outPipe;
	}

	/** @return The current standard error stream */
	PrintStream error() {
		return errorPipe;
	}

	/**
	 * Set the current standard out stream.
	 * Passing in a null value will set the stream to the default.
	 * @return Self reference
	 */
	StandardPipe setOut( PrintStream pipe ) {
		this.outPipe = resolvePipe( pipe, defaultOut );
		return this;
	}

	/**
	 * Set the current standard error stream.
	 * Passing in a null value will set the stream to the default.
	 * @return Self reference
	 */
	StandardPipe setError( PrintStream pipe ) {
		this.errorPipe = resolvePipe( pipe, defaultError );
		return this;
	}

	private PrintStream resolvePipe( PrintStream pipe, NamedPrintStreamContainer alternative ) {
		if ( pipe == null ) {
			pipe = alternative.pipe;
			System.err.println( "WARNING: null value used for pipe " + alternative + " with " + StandardPipe.class );
		}
		return pipe;
	}

	/** @return The current global standard out stream */
	public static PrintStream getOutPipe() {
		return GLOBAL.outPipe;
	}

	/** @return The current global standard error stream */
	public static PrintStream getErrorPipe() {
		return GLOBAL.errorPipe;
	}

	/**
	 * Set the current global standard out stream.
	 * Passing in a null value will set the stream to the default.
	 */
	public static void setOutPipe( PrintStream pipe ) {
		GLOBAL.setOut( pipe );
	}

	/**
	 * Set the current global standard error stream.
	 * Passing in a null value will set the stream to the default.
	 */
	public static void setErrorPipe( PrintStream pipe ) {
		GLOBAL.setError( pipe );
	}

	private record NamedPrintStreamContainer( String name, PrintStream pipe ) {
		NamedPrintStreamContainer( String name, PrintStream pipe ) {
			this.name = Objects.requireNonNull( name );
			this.pipe = Objects.requireNonNull( pipe );
		}
		@Override
		public String toString() {
			return name;
		}
	}

}
