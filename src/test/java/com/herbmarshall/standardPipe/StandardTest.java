package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static com.herbmarshall.standardPipe.Standard.DOUBLE_OVERRIDE_ERROR_TEMPLATE;

class StandardTest {

	@Test
	void constructor_name_null() {
		// Arrange
		PrintStream error = System.err;
		// Act
		try {
			new Standard( null, error );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException ignored ) {
		}
	}

	@Test
	void constructor_defaultPipe_null() {
		// Arrange
		String name = UUID.randomUUID().toString();
		// Act
		try {
			new Standard( name, null );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException ignored ) {
		}
	}

	@Test
	void print_default() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( stream )
		);
		String value = UUID.randomUUID().toString();
		// Act
		standard.print( value );
		// Assert
		Assertions.assertEquals( value, stream.toString() );
	}

	@Test
	void print_override() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( new ByteArrayOutputStream() )
		);
		String value = UUID.randomUUID().toString();
		standard.override( new PrintStream( stream ) );
		// Act
		standard.print( value );
		// Assert
		Assertions.assertEquals( value, stream.toString() );
	}

	@Test
	void print_null() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( stream )
		);
		// Act
		standard.print( null );
		// Assert
		Assertions.assertEquals( "null", stream.toString() );
	}

	@Test
	void println_default() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( stream )
		);
		String value = UUID.randomUUID().toString();
		// Act
		standard.println( value );
		// Assert
		Assertions.assertEquals( value + "\n", stream.toString() );
	}

	@Test
	void println_override() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( new ByteArrayOutputStream() )
		);
		standard.override( new PrintStream( stream ) );
		String value = UUID.randomUUID().toString();
		// Act
		standard.println( value );
		// Assert
		Assertions.assertEquals( value + "\n", stream.toString() );
	}

	@Test
	void println_null() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( stream )
		);
		// Act
		standard.println( null );
		// Assert
		Assertions.assertEquals( "null\n", stream.toString() );
	}

	@Test
	void override() {
		// Arrange
		ByteArrayOutputStream streamA = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( streamA )
		);
		ByteArrayOutputStream streamB = new ByteArrayOutputStream();
		PrintStream override = new PrintStream( streamB );
		String valueA = UUID.randomUUID().toString();
		String valueB = UUID.randomUUID().toString();
		// Act
		standard.print( valueA );
		standard.override( override );
		standard.print( valueB );
		// Assert
		Assertions.assertEquals( valueA, streamA.toString() );
		Assertions.assertEquals( valueB, streamB.toString() );
	}

	@Test
	void override_null() {
		// Arrange
		String name = UUID.randomUUID().toString();
		Standard standard = new Standard(
			name,
			new PrintStream( new ByteArrayOutputStream() )
		);
		// Act
		try {
			standard.override( null );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException ignored ) {
		}
	}

	@Test
	void override_doubleCall() {
		// Arrange
		String name = UUID.randomUUID().toString();
		Standard standard = new Standard(
			name,
			new PrintStream( new ByteArrayOutputStream() )
		);
		standard.override( new PrintStream( new ByteArrayOutputStream() ) );
		PrintStream override = new PrintStream( new ByteArrayOutputStream() );
		// Act
		try {
			standard.override( override );
			Assertions.fail();
		}
		// Assert
		catch ( IllegalStateException e ) {
			Assertions.assertEquals(
				DOUBLE_OVERRIDE_ERROR_TEMPLATE.formatted( name ),
				e.getMessage()
			);
		}
	}

	@Test
	void reset() {
		// Arrange
		ByteArrayOutputStream streamA = new ByteArrayOutputStream();
		Standard standard = new Standard(
			UUID.randomUUID().toString(),
			new PrintStream( streamA )
		);
		ByteArrayOutputStream streamB = new ByteArrayOutputStream();
		PrintStream override = new PrintStream( streamB );
		String valueA = UUID.randomUUID().toString();
		String valueB = UUID.randomUUID().toString();
		String valueC = UUID.randomUUID().toString();
		// Act
		standard.print( valueA );
		standard.override( override );
		standard.print( valueB );
		standard.reset();
		standard.print( valueC );
		// Assert
		Assertions.assertEquals( valueA + valueC, streamA.toString() );
		Assertions.assertEquals( valueB, streamB.toString() );
	}

	@Test
	void standardOut() {
		Standard.out.println( "Standard Out" );
	}

	@Test
	void standardErr() {
		Standard.err.println( "Standard ERR" );
	}

}
