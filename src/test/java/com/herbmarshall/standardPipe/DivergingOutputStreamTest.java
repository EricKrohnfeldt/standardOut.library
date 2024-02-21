package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.herbmarshall.standardPipe.DivergingOutputStream.doubleOverrideError;

@SuppressWarnings( "resource" )
class DivergingOutputStreamTest {

	@Nested
	class constructor {

		@Test
		void null_parameter() {
			String name = randomString();
			OutputStream buffer = new ByteArrayOutputStream();
			null_parameter( () -> new DivergingOutputStream( null, buffer ) );
			null_parameter( () -> new DivergingOutputStream( name, null ) );
		}

		private void null_parameter( Supplier<DivergingOutputStream> constructor ) {
			// Arrange
			// Act
			try {
				constructor.get();
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
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( buffer );
			String value = randomString();
			// Act
			stream.print( value );
			// Assert
			Assertions.assertEquals( value, buffer.toString() );
		}

		@Test
		void using_override() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream stream = buildStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			stream.print( valueA );
			stream.override( override );
			stream.print( valueB );
			stream.reset();
			stream.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

		@Test
		void value_null() {
			// Arrange
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( buffer );
			// Act
			stream.print( null );
			// Assert
			Assertions.assertEquals( "null", buffer.toString() );
		}

	}

	@Nested
	class println {

		@Test
		void using_default() {
			// Arrange
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( buffer );
			String value = randomString();
			// Act
			stream.println( value );
			// Assert
			Assertions.assertEquals( value + "\n", buffer.toString() );
		}

		@Test
		void using_override() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream stream = buildStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			stream.println( valueA );
			stream.override( override );
			stream.println( valueB );
			stream.reset();
			stream.println( valueC );
			// Assert
			Assertions.assertEquals( valueA + "\n" + valueC + "\n", normal.toString() );
			Assertions.assertEquals( valueB + "\n", override.toString() );
		}

		@Test
		void value_null() {
			// Arrange
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( buffer );
			// Act
			stream.println( null );
			// Assert
			Assertions.assertEquals( "null\n", buffer.toString() );
		}

	}


	@Nested
	class override_reset {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream bufferA = new ByteArrayOutputStream();
			ByteArrayOutputStream bufferB = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( bufferA );
			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			stream.print( valueA );
			stream.override( bufferB );
			stream.print( valueB );
			stream.reset();
			stream.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, bufferA.toString() );
			Assertions.assertEquals( valueB, bufferB.toString() );
		}

		@Test
		void pipe_null() {
			// Arrange
			DivergingOutputStream stream = buildStream();
			// Act
			try {
				stream.override( null );
				Assertions.fail();
			}
			// Assert
			catch ( NullPointerException ignored ) {
			}
		}

		@Test
		void override_double_call() {
			// Arrange
			String name = randomString();
			DivergingOutputStream stream = buildStream( name );

			ByteArrayOutputStream override = new ByteArrayOutputStream();
			ByteArrayOutputStream doubleOverride = new ByteArrayOutputStream();

			stream.override( override );
			// Act
			try {
				stream.override( doubleOverride );
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

		@Test
		void reset_double_call() {
			// Arrange
			ByteArrayOutputStream bufferA = new ByteArrayOutputStream();
			ByteArrayOutputStream bufferB = new ByteArrayOutputStream();
			DivergingOutputStream stream = buildStream( bufferA );
			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			// Act
			stream.print( valueA );
			stream.override( bufferB );
			stream.reset();
			stream.print( valueB );
			stream.reset();
			stream.reset();
			stream.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueB + valueC, bufferA.toString() );
			Assertions.assertTrue( bufferB.toString().isEmpty() );
		}

	}

	@Nested
	class flush {

		@Test
		void normal() throws IOException {
			// Arrange
			AtomicBoolean called = new AtomicBoolean( false );
			ByteArrayOutputStream buffer = buildBufferWithFlushMock( called );
			DivergingOutputStream stream = buildStream( buffer );
			// Act
			stream.flush();
			// Assert
			Assertions.assertTrue( called.get() );
		}

		@Test
		void override() throws IOException {
			// Arrange
			AtomicBoolean normalCalled = new AtomicBoolean( false );
			AtomicBoolean overrideCalled = new AtomicBoolean( false );
			ByteArrayOutputStream normal = buildBufferWithFlushMock( normalCalled );
			ByteArrayOutputStream override = buildBufferWithFlushMock( overrideCalled );
			DivergingOutputStream stream = buildStream( normal );
			stream.override( override );
			// Act
			stream.flush();
			// Assert
			Assertions.assertTrue( normalCalled.get() );
			Assertions.assertTrue( overrideCalled.get() );
		}

		@Test
		void after_override() throws IOException {
			// Arrange
			AtomicBoolean normalCalled = new AtomicBoolean( false );
			AtomicBoolean overrideCalled = new AtomicBoolean( false );
			ByteArrayOutputStream normal = buildBufferWithFlushMock( normalCalled );
			ByteArrayOutputStream override = buildBufferWithFlushMock( overrideCalled );
			DivergingOutputStream stream = buildStream( normal );
			stream.override( override );
			stream.reset();
			// Act
			stream.flush();
			// Assert
			Assertions.assertTrue( normalCalled.get() );
			Assertions.assertFalse( overrideCalled.get() );
		}

	}

	@Nested
	class close {

		@Test
		void normal() throws IOException {
			// Arrange
			AtomicBoolean bufferClose = new AtomicBoolean( false );
			ByteArrayOutputStream buffer = buildBufferWithCloseMock( bufferClose );
			DivergingOutputStream stream = buildStream( buffer );
			// Act
			stream.close();
			// Assert
			Assertions.assertTrue( bufferClose.get() );
		}

		@Test
		void override() throws IOException {
			// Arrange
			AtomicBoolean normalClose = new AtomicBoolean( false );
			AtomicBoolean overrideClose = new AtomicBoolean( false );
			ByteArrayOutputStream normal = buildBufferWithCloseMock( normalClose );
			ByteArrayOutputStream override = buildBufferWithCloseMock( overrideClose );
			DivergingOutputStream stream = buildStream( normal );
			stream.override( override );
			// Act
			stream.close();
			// Assert
			Assertions.assertTrue( normalClose.get() );
			Assertions.assertTrue( overrideClose.get() );
		}

		@Test
		void after_override() throws IOException {
			// Arrange
			AtomicBoolean normalClose = new AtomicBoolean( false );
			AtomicBoolean overrideClose = new AtomicBoolean( false );
			ByteArrayOutputStream normal = buildBufferWithCloseMock( normalClose );
			ByteArrayOutputStream override = buildBufferWithCloseMock( overrideClose );
			DivergingOutputStream stream = buildStream( normal );
			stream.override( override );
			stream.reset();
			// Act
			stream.close();
			// Assert
			Assertions.assertTrue( normalClose.get() );
			Assertions.assertFalse( overrideClose.get() );
		}

	}

	private ByteArrayOutputStream buildBufferWithFlushMock( AtomicBoolean called ) {
		return new ByteArrayOutputStream() {
			@Override
			public void flush() throws IOException {
				called.set( true );
				super.flush();
			}
		};
	}

	private ByteArrayOutputStream buildBufferWithCloseMock( AtomicBoolean called ) {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				called.set( true );
				super.close();
			}
		};
	}

	private DivergingOutputStream buildStream() {
		return buildStream( new ByteArrayOutputStream() );
	}

	private DivergingOutputStream buildStream( String name ) {
		return buildStream( name, new ByteArrayOutputStream() );
	}

	private DivergingOutputStream buildStream( OutputStream stream ) {
		return buildStream( randomString(), stream );
	}

	private DivergingOutputStream buildStream( String name, OutputStream stream ) {
		return new DivergingOutputStream( name, stream );
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

}
