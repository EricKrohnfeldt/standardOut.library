package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class OverridePlanTest {

	@Nested
	class constructor {

		@Test
		void happyPath() {
			// Arrange
			DivergingOutputStream stream = buildDivergingStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();
			// Act
			OverridePlan output = new OverridePlan( stream, override );
			// Assert
			Assertions.assertNotNull( output );
		}

		@Test
		@SuppressWarnings( "resource" )
		void null_parameter() {
			DivergingOutputStream stream = buildDivergingStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();
			null_parameter( () -> new OverridePlan( null, override ) );
			null_parameter( () -> new OverridePlan( stream, null ) );
		}

		private void null_parameter( Supplier<OverridePlan> constructor ) {
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
	class execute_Runnable {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream pipe = buildDivergingStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			Runnable action = () -> pipe.print( valueB );
			OverridePlan plan = new OverridePlan( pipe, override );
			// Act
			pipe.print( valueA );
			plan.execute( action );
			pipe.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

	}

	@Nested
	class execute_Consumer {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream pipe = buildDivergingStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			Consumer<OutputStream> action = p -> {
				Assertions.assertSame( override, p );
				pipe.print( valueB );
			};
			OverridePlan plan = new OverridePlan( pipe, override );
			// Act
			pipe.print( valueA );
			plan.execute( action );
			pipe.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
		}

	}

	@Nested
	class execute_Supplier {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream pipe = buildDivergingStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			UUID expected = UUID.randomUUID();
			Supplier<UUID> action = () -> {
				pipe.print( valueB );
				return expected;
			};
			OverridePlan plan = new OverridePlan( pipe, override );
			// Act
			pipe.print( valueA );
			UUID output = plan.execute( action );
			pipe.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
			Assertions.assertSame( expected, output );
		}

	}

	@Nested
	class execute_Function {

		@Test
		void happyPath() {
			// Arrange
			ByteArrayOutputStream normal = new ByteArrayOutputStream();
			ByteArrayOutputStream override = new ByteArrayOutputStream();

			DivergingOutputStream pipe = buildDivergingStream( normal );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			UUID expected = UUID.randomUUID();

			Function<OutputStream, UUID> action = p -> {
				Assertions.assertSame( override, p );
				pipe.print( valueB );
				return expected;
			};
			OverridePlan plan = new OverridePlan( pipe, override );
			// Act
			pipe.print( valueA );
			UUID output = plan.execute( action );
			pipe.print( valueC );
			// Assert
			Assertions.assertEquals( valueA + valueC, normal.toString() );
			Assertions.assertEquals( valueB, override.toString() );
			Assertions.assertSame( expected, output );
		}

	}

	private DivergingOutputStream buildDivergingStream() {
		return buildDivergingStream( new ByteArrayOutputStream() );
	}

	private DivergingOutputStream buildDivergingStream( ByteArrayOutputStream stream ) {
		return new DivergingOutputStream( randomString(), stream );
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

}
