package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
			Standard standard = buildStandard();
			PrintStream pipe = buildPipe();
			// Act
			OverridePlan output = new OverridePlan( standard, pipe );
			// Assert
			Assertions.assertNotNull( output );
		}

		@Test
		@SuppressWarnings( "resource" )
		void null_parameter() {
			Standard standard = buildStandard();
			PrintStream pipe = buildPipe();
			null_parameter( () -> new OverridePlan( null, pipe ) );
			null_parameter( () -> new OverridePlan( standard, null ) );
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

			Standard pipe = buildStandard(  normal );
			PrintStream overridePipe = buildPipe( override );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			Runnable action = () -> pipe.print( valueB );
			OverridePlan plan = new OverridePlan( pipe, overridePipe );
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

			Standard pipe = buildStandard( normal );
			PrintStream overridePipe = buildPipe( override );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			Consumer<PrintStream> action = p -> {
				Assertions.assertSame( overridePipe, p );
				pipe.print( valueB );
			};
			OverridePlan plan = new OverridePlan( pipe, overridePipe );
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

			Standard pipe = buildStandard( normal );
			PrintStream overridePipe = buildPipe( override );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();

			UUID expected = UUID.randomUUID();
			Supplier<UUID> action = () -> {
				pipe.print( valueB );
				return expected;
			};
			OverridePlan plan = new OverridePlan( pipe, overridePipe );
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

			Standard pipe = buildStandard( normal );
			PrintStream overridePipe = buildPipe( override );

			String valueA = randomString();
			String valueB = randomString();
			String valueC = randomString();
			UUID expected = UUID.randomUUID();

			Function<PrintStream, UUID> action = p -> {
				Assertions.assertSame( overridePipe, p );
				pipe.print( valueB );
				return expected;
			};
			OverridePlan plan = new OverridePlan( pipe, overridePipe );
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

	private PrintStream buildPipe() {
		return buildPipe( new ByteArrayOutputStream() );
	}

	private PrintStream buildPipe( ByteArrayOutputStream stream ) {
		return new PrintStream( stream );
	}

	private Standard buildStandard() {
		return buildStandard( new ByteArrayOutputStream() );
	}

	private Standard buildStandard( ByteArrayOutputStream stream ) {
		return new Standard( randomString(), new PrintStream( stream ) );
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

}
