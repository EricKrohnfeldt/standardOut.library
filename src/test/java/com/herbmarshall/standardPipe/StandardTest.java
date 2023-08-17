package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static com.herbmarshall.standardPipe.Standard.DOUBLE_OVERRIDE_ERROR_TEMPLATE;
import static com.herbmarshall.standardPipe.Standard.NULL_POINTER_ERROR_TEMPLATE;

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
		catch ( NullPointerException e ) {
			Assertions.assertEquals(
				NULL_POINTER_ERROR_TEMPLATE.formatted( "name" ),
				e.getMessage()
			);
		}
	}

	@Test
	void constructor_defaultPipe_null() {
		// Arrange
		String name = randomString();
		// Act
		try {
			new Standard( name, null );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException e ) {
			Assertions.assertEquals(
				NULL_POINTER_ERROR_TEMPLATE.formatted( "defaultPipe" ),
				e.getMessage()
			);
		}
	}

	@Test
	void print_default() {
		// Arrange
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Standard standard = new Standard(
			randomString(),
			new PrintStream( stream )
		);
		String value = randomString();
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
			randomString(),
			new PrintStream( new ByteArrayOutputStream() )
		);
		String value = randomString();
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
			randomString(),
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
			randomString(),
			new PrintStream( stream )
		);
		String value = randomString();
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
			randomString(),
			new PrintStream( new ByteArrayOutputStream() )
		);
		standard.override( new PrintStream( stream ) );
		String value = randomString();
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
			randomString(),
			new PrintStream( stream )
		);
		// Act
		standard.println( null );
		// Assert
		Assertions.assertEquals( "null\n", stream.toString() );
	}

	@Test
	void toStream_default() {
		// Arrange
		PrintStream stream = new PrintStream( new ByteArrayOutputStream() );
		Standard standard = new Standard(
			randomString(),
			stream
		);
		// Act
		PrintStream output = standard.toStream();
		// Assert
		Assertions.assertSame( stream, output );
	}

	@Test
	void toStream_override() {
		// Arrange
		PrintStream stream = new PrintStream( new ByteArrayOutputStream() );
		Standard standard = new Standard(
			randomString(),
			new PrintStream( new ByteArrayOutputStream() )
		);
		standard.override( stream );
		// Act
		PrintStream output = standard.toStream();
		// Assert
		Assertions.assertSame( stream, output );
	}

	@Test
	void toStream_reset() {
		// Arrange
		PrintStream stream = new PrintStream( new ByteArrayOutputStream() );
		Standard standard = new Standard(
			randomString(),
			stream
		);
		standard.override( new PrintStream( new ByteArrayOutputStream() ) );
		standard.reset();
		// Act
		PrintStream output = standard.toStream();
		// Assert
		Assertions.assertSame( stream, output );
	}

	@Test
	void override() {
		// Arrange
		ByteArrayOutputStream streamA = new ByteArrayOutputStream();
		Standard standard = new Standard(
			randomString(),
			new PrintStream( streamA )
		);
		ByteArrayOutputStream streamB = new ByteArrayOutputStream();
		PrintStream override = new PrintStream( streamB );
		String valueA = randomString();
		String valueB = randomString();
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
		String name = randomString();
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
		catch ( NullPointerException e ) {
			Assertions.assertEquals(
				NULL_POINTER_ERROR_TEMPLATE.formatted( "pipe" ),
				e.getMessage()
			);
		}
	}

	@Test
	void override_doubleCall() {
		// Arrange
		String name = randomString();
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
			randomString(),
			new PrintStream( streamA )
		);
		ByteArrayOutputStream streamB = new ByteArrayOutputStream();
		PrintStream override = new PrintStream( streamB );
		String valueA = randomString();
		String valueB = randomString();
		String valueC = randomString();
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

	@Test
	void resetAll() {
		// Arrange
		ByteArrayOutputStream streamDefaultA = new ByteArrayOutputStream();
		ByteArrayOutputStream streamDefaultB = new ByteArrayOutputStream();
		Standard standardA = new Standard( randomString(), new PrintStream( streamDefaultA ) );
		Standard standardB = new Standard( randomString(), new PrintStream( streamDefaultB ) );
		ByteArrayOutputStream streamOverrideA = new ByteArrayOutputStream();
		ByteArrayOutputStream streamOverrideB = new ByteArrayOutputStream();
		standardA.override( new PrintStream( streamOverrideA ) );
		standardB.override( new PrintStream( streamOverrideB ) );
		String overrideValue = randomString();
		String defaultValue = randomString();
		standardA.print( overrideValue );
		standardB.print( overrideValue );
		// Act
		Standard.resetAll();
		// Assert
		standardA.print( defaultValue );
		standardB.print( defaultValue );
		Assertions.assertEquals( overrideValue, streamOverrideA.toString() );
		Assertions.assertEquals( overrideValue, streamOverrideB.toString() );
		Assertions.assertEquals( defaultValue, streamDefaultA.toString() );
		Assertions.assertEquals( defaultValue, streamDefaultB.toString() );
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

}
