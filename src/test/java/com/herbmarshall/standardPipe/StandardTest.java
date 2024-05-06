/*
 * This file is part of herbmarshall.com: standardPipe.library  ( hereinafter "standardPipe.library" ).
 *
 * standardPipe.library is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 2 of the License,
 * or (at your option) any later version.
 *
 * standardPipe.library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with standardPipe.library.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;
import java.util.function.Consumer;

class StandardTest {

	@Nested
	class constructor {

		@Test
		void name_null() {
			// Arrange
			OutputStream pipe = new ByteArrayOutputStream();
			// Act
			try {
				new Standard( null, pipe );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
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
			catch ( NullPointerException ignored ) {
			}
		}

	}

	@Nested
	class print {

		@Test
		void using_default() {
			// Arrange
			ByteArrayOutputStream pipe = new ByteArrayOutputStream();
			Standard standard = buildStandard( pipe );
			String value = randomString();
			// Act
			standard.print( value );
			// Assert
			Assertions.assertEquals( value, pipe.toString() );
		}

		@Test
		void using_override() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = buildStandard( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			standard.print( valueA );
			standard.withOverride( override )
				.execute( () -> standard.print( valueB ) );
			standard.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

		@Test
		void value_null() {
			// Arrange
			ByteArrayOutputStream pipe = new ByteArrayOutputStream();
			Standard standard = buildStandard( pipe );
			// Act
			standard.print( null );
			// Assert
			Assertions.assertEquals( "null", pipe.toString() );
		}

	}

	@Nested
	class println {

		@Test
		void using_default() {
			// Arrange
			ByteArrayOutputStream pipe = new ByteArrayOutputStream();
			Standard standard = buildStandard( pipe );
			String value = randomString();
			// Act
			standard.println( value );
			// Assert
			Assertions.assertEquals( value + "\n", pipe.toString() );
		}

		@Test
		void using_override() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = buildStandard( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			standard.println( valueA );
			standard.withOverride( override )
				.execute( () -> standard.println( valueB ) );
			standard.println( valueC );
			// Assert
			Assertions.assertEquals( valueA + "\n" + valueC + "\n", normal.toString() );
			Assertions.assertEquals( valueB + "\n", override.toString() );
		}

		@Test
		void value_null() {
			// Arrange
			ByteArrayOutputStream pipe = new ByteArrayOutputStream();
			Standard standard = buildStandard( pipe );
			// Act
			standard.println( null );
			// Assert
			Assertions.assertEquals( "null\n", pipe.toString() );
		}

	}

	@Nested
	class toStream {

		@Test
		void using_default() {
			// Arrange
			String name = randomString();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Standard standard = new Standard( name, stream );
			// Act
			OutputStream output = standard.toStream();
			// Assert
			String value = randomString();
			new PrintStream( output ).print( value );
			Assertions.assertEquals( value, stream.toString() );
			Assertions.assertEquals( name, output.toString() );
		}

		@Test
		void using_override() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = buildStandard( normal );
			// Act
			OutputStream output = standard.toStream();

			// Assert
			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			PrintStream print = new PrintStream( output );

			print.print( valueA );
			standard.withOverride( override )
				.execute( () -> print.print( valueB ) );
			print.print( valueC );

			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

	}

	@Nested
	@Deprecated
	class override_ByteArrayOutputStream {

		@Test
		void happyPath() {
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
			Standard standard = new Standard(
				randomString(),
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( ByteArrayOutputStream ) null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

		@Test
		void double_call() {
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
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
		}

	}

	@Nested
	@Deprecated
	class override_ByteArrayOutputStream_Consumer {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = new Standard( randomString(), new PrintStream( normal ) );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			String valueD = randomString();
			// Act
			standard.print( valueA );
			standard.override( override, pipe -> {
				pipe.print( valueB );
				standard.print( valueC );
			} );
			standard.print( valueD );
			// Assert
			Assertions.assertEquals( valueA + valueD, normal.toString() );
			Assertions.assertEquals( valueB + valueC, override.toString() );
		}

		@Test
		void action_failure() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = new Standard( randomString(), new PrintStream( normal ) );

			String valueA = randomString();
			String valueB = randomString();
			String errorMessage = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( override, pipe -> {
					throw new RuntimeException( errorMessage );
				} );
				Assertions.fail();
			}
			catch ( RuntimeException e ) {
				Assertions.assertEquals( errorMessage, e.getMessage() );
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, normal.toString() );
		}

		@Test
		void pipe_null() {
			// Arrange
			Standard standard = new Standard(
				randomString(),
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( ByteArrayOutputStream ) null, failingConsumer() );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

		@Test
		void double_override() {
			// Arrange
			String name = randomString();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( stream );
			PrintStream overrideA = new PrintStream( new ByteArrayOutputStream() );
			PrintStream overrideB = new PrintStream( new ByteArrayOutputStream() );

			Standard standard = new Standard( name, normal );

			String valueA = randomString();
			String valueB = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( overrideA, pipe ->
					standard.override( overrideB )
				);
				Assertions.fail();
			}
			catch ( IllegalStateException e ) {
				Assertions.assertEquals(
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, stream.toString() );
		}

		@Test
		void double_call() {
			// Arrange
			String name = randomString();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( stream );
			PrintStream overrideA = new PrintStream( new ByteArrayOutputStream() );
			PrintStream overrideB = new PrintStream( new ByteArrayOutputStream() );

			Standard standard = new Standard( name, normal );

			String valueA = randomString();
			String valueB = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( overrideA, pipe ->
					standard.override( overrideB, failingConsumer() )
				);
				Assertions.fail();
			}
			catch ( IllegalStateException e ) {
				Assertions.assertEquals(
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, stream.toString() );
		}

	}

	@Nested
	@Deprecated
	class override_PrintStream {

		@Test
		void happyPath() {
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
			Standard standard = new Standard(
				randomString(),
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( PrintStream ) null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

		@Test
		void double_call() {
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
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
		}

	}

	@Nested
	@Deprecated
	class override_PrintStream_Consumer {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream streamA = new ByteArrayOutputStream();
			ByteArrayOutputStream streamB = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( streamA );
			PrintStream override = new PrintStream( streamB );

			Standard standard = new Standard( randomString(), normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			String valueD = randomString();
			// Act
			standard.print( valueA );
			standard.override( override, pipe -> {
				pipe.print( valueB );
				standard.print( valueC );
			} );
			standard.print( valueD );
			// Assert
			Assertions.assertEquals( valueA + valueD, streamA.toString() );
			Assertions.assertEquals( valueB + valueC, streamB.toString() );
		}

		@Test
		void action_failure() {
			// Arrange
			ByteArrayOutputStream streamA = new ByteArrayOutputStream();
			ByteArrayOutputStream streamB = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( streamA );
			PrintStream override = new PrintStream( streamB );

			Standard standard = new Standard( randomString(), normal );

			String valueA = randomString();
			String valueB = randomString();
			String errorMessage = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( override, pipe -> {
					throw new RuntimeException( errorMessage );
				} );
				Assertions.fail();
			}
			catch ( RuntimeException e ) {
				Assertions.assertEquals( errorMessage, e.getMessage() );
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, streamA.toString() );
		}

		@Test
		void pipe_null() {
			// Arrange
			Standard standard = new Standard(
				randomString(),
				new PrintStream( new ByteArrayOutputStream() )
			);
			// Act
			try {
				standard.override( ( PrintStream ) null, failingConsumer() );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

		@Test
		void double_override() {
			// Arrange
			String name = randomString();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( stream );
			PrintStream overrideA = new PrintStream( new ByteArrayOutputStream() );
			PrintStream overrideB = new PrintStream( new ByteArrayOutputStream() );

			Standard standard = new Standard( name, normal );

			String valueA = randomString();
			String valueB = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( overrideA, pipe ->
					standard.override( overrideB )
				);
				Assertions.fail();
			}
			catch ( IllegalStateException e ) {
				Assertions.assertEquals(
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, stream.toString() );
		}

		@Test
		void double_call() {
			// Arrange
			String name = randomString();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream normal = new PrintStream( stream );
			PrintStream overrideA = new PrintStream( new ByteArrayOutputStream() );
			PrintStream overrideB = new PrintStream( new ByteArrayOutputStream() );

			Standard standard = new Standard( name, normal );

			String valueA = randomString();
			String valueB = randomString();
			// Act
			standard.print( valueA );
			try {
				standard.override( overrideA, pipe ->
					standard.override( overrideB, failingConsumer() )
				);
				Assertions.fail();
			}
			catch ( IllegalStateException e ) {
				Assertions.assertEquals(
					DivergingOutputStream.doubleOverrideError( name ),
					e.getMessage()
				);
			}
			standard.print( valueB );
			// Assert
			Assertions.assertEquals( valueA + valueB, stream.toString() );
		}

	}

	@Test
	@Deprecated
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

	@Nested
	class withOverride {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			Standard standard = buildStandard( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			standard.print( valueA );
			standard.withOverride( override )
				.execute( () -> standard.print( valueB ) );
			standard.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

		@Test
		void null_stream() {
			// Arrange
			Standard pipe = buildStandard( new ByteArrayOutputStream() );
			// Act
			try {
				pipe.withOverride( null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

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
	@Deprecated
	void resetAll() {
		// Arrange
		// Act
		try {
			Standard.resetAll();
		}
		// Assert
		catch ( UnsupportedOperationException ignored ) {
		}
	}

	private Standard buildStandard( OutputStream pipe ) {
		return new Standard( randomString(), pipe );
	}

	private Consumer<PrintStream> failingConsumer() {
		return pipe -> {
			throw new UnsupportedOperationException();
		};
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

}
