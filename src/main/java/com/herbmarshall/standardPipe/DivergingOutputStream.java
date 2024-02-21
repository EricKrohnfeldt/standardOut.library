package com.herbmarshall.standardPipe;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

@SuppressWarnings( "resource" )
class DivergingOutputStream extends OutputStream {

	private final String name;
	private final OutputStream defaultPipe;
	private OutputStream pipe;

	private final PrintStream printer;

	DivergingOutputStream( String name, OutputStream defaultPipe ) {
		this.name = Objects.requireNonNull( name );
		this.defaultPipe = Objects.requireNonNull( defaultPipe );
		this.printer = new PrintStream( this );
	}

	/** @see PrintStream#print(String) */
	public void print( String value ) {
		printer.print( value );
	}

	/** @see PrintStream#println(String) */
	public void println( String value ) {
		printer.println( value );
	}

	synchronized void override( OutputStream pipe ) {
		if ( this.pipe != null ) throw new IllegalStateException( doubleOverrideError( name ) );
		this.pipe = Objects.requireNonNull( pipe );
	}

	void reset() {
		this.pipe = null;
	}

	@Override
	public void write( int i ) throws IOException {
		resolvePipe().write( i );
	}

	@Override
	public void flush() throws IOException {
		defaultPipe.flush();
		if ( pipe != null ) pipe.flush();
	}

	@Override
	public void close() throws IOException {
		defaultPipe.close();
		if ( pipe != null ) pipe.close();
	}

	private OutputStream resolvePipe() {
		return Objects.requireNonNullElse( pipe, defaultPipe );
	}

	@Override
	public String toString() {
		return name;
	}

	static String doubleOverrideError( String pipeName ) {
		return "Double override of standard pipe " + pipeName;
	}

}
