package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static com.herbmarshall.standardPipe.Standard.doubleOverrideError;
import static com.herbmarshall.standardPipe.Standard.nullPointerError;

class StandardTest {

	@Nested
	class constructor {

		@Test
		void name_null() {
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
					nullPointerError( "name" ),
					e.getMessage()
				);
			}
		}

		@Test
		void defaultPipe_null() {
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
					nullPointerError( "defaultPipe" ),
					e.getMessage()
				);
			}
		}

	}

	@Nested
	class print {

		@Test
		void $using_default() {
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
		void $using_override() {
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
		void value_null() {
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

	}

	@Nested
	class println {

		@Test
		void $using_default() {
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
		void $using_override() {
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
		void value_null() {
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

	}

	@Nested
	class toStream {

		@Test
		void $using_default() {
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
		void $using_override() {
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
		void $after_reset() {
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

	}

	@Nested
	class override_byteArrayOutputStream {

		@Test
		void $happyPath() {
			// Arrange
			ByteArrayOutputStream streamA = new ByteArrayOutputStream();
			Standard standard = new Standard(
				randomString(),
				new PrintStream( streamA )
			);
			ByteArrayOutputStream streamB = new ByteArrayOutputStream();
			String valueA = randomString();
			String valueB = randomString();
			// Act
			standard.print( valueA );
			standard.override( streamB );
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA, streamA.toString() );
			Assertions.assertEquals( valueB, streamB.toString() );
		}

		@Test
		void pipe_null() {
			// Arrange
			String name = randomString();
			Standard standard = new Standard(
				name,
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( ByteArrayOutputStream ) null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException e ) {
				Assertions.assertEquals(
					nullPointerError( "pipe" ),
					e.getMessage()
				);
			}
		}

		@Test
		void $double_call() {
			// Arrange
			String name = randomString();
			Standard standard = new Standard(
				name,
				new PrintStream( new ByteArrayOutputStream() )
			);
			standard.override( new ByteArrayOutputStream() );
			ByteArrayOutputStream override = new ByteArrayOutputStream();
			// Act
			try {
				standard.override( override );
				Assertions.fail();
			}
			// Assert
			catch ( IllegalStateException e ) {
				Assertions.assertEquals(
					doubleOverrideError( name ),
					e.getMessage()
				);
			}
		}

	}

	@Nested
	class override_printStream {

		@Test
		void $happyPath() {
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
		void pipe_null() {
			// Arrange
			String name = randomString();
			Standard standard = new Standard(
				name,
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( PrintStream ) null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException e ) {
				Assertions.assertEquals(
					nullPointerError( "pipe" ),
					e.getMessage()
				);
			}
		}

		@Test
		void $double_call() {
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
					doubleOverrideError( name ),
					e.getMessage()
				);
			}
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
