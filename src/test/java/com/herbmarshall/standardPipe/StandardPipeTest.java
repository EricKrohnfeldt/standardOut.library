package com.herbmarshall.standardPipe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class StandardPipeTest {

	@AfterEach
	void tearDown() {
		StandardPipe.setOutPipe( StandardPipe.DEFAULT_OUT );
		StandardPipe.setErrorPipe( StandardPipe.DEFAULT_ERROR );
	}

	@Test
	void constructor_defaultOut_null() {
		// Arrange
		PrintStream error = System.err;
		// Act
		try {
			new StandardPipe( null, error );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException ignored ) {
		}
	}

	@Test
	void constructor_defaultError_null() {
		// Arrange
		PrintStream out = System.out;
		// Act
		try {
			new StandardPipe( out, null );
			Assertions.fail();
		}
		// Assert
		catch ( NullPointerException ignored ) {
		}
	}

	@Test
	void out() {
		// Arrange
		PrintStream outPipe = new PrintStream( new ByteArrayOutputStream() );
		StandardPipe pipe = new StandardPipe(
			outPipe,
			new PrintStream( new ByteArrayOutputStream() )
		);
		// Act
		PrintStream output = pipe.out();
		// Assert
		Assertions.assertSame( outPipe, output );
	}


	@Test
	void err() {
		// Arrange
		PrintStream errorPipe = new PrintStream( new ByteArrayOutputStream() );
		StandardPipe pipe = new StandardPipe(
			new PrintStream( new ByteArrayOutputStream() ),
			errorPipe
		);
		// Act
		PrintStream output = pipe.error();
		// Assert
		Assertions.assertSame( errorPipe, output );
	}

	@Test
	void setOut() {
		// Arrange
		StandardPipe pipe = new StandardPipe(
			new PrintStream( new ByteArrayOutputStream() ),
			new PrintStream( new ByteArrayOutputStream() )
		);
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		// Act
		StandardPipe output = pipe.setOut( printer );
		// Assert
		Assertions.assertSame( pipe, output );
		Assertions.assertSame( printer, pipe.out() );
	}

	@Test
	void setOut_pipe_null() {
		// Arrange
		PrintStream out = new PrintStream( new ByteArrayOutputStream() );
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		StandardPipe pipe = new StandardPipe(
			out,
			new PrintStream( new ByteArrayOutputStream() )
		)
			.setOut( printer );
		Assertions.assertSame( printer, pipe.out() );
		// Act
		StandardPipe output = pipe.setOut( null );
		// Assert
		Assertions.assertSame( pipe, output );
		Assertions.assertSame( out, pipe.out() );
	}

	@Test
	void setError() {
		// Arrange
		StandardPipe pipe = new StandardPipe(
			new PrintStream( new ByteArrayOutputStream() ),
			new PrintStream( new ByteArrayOutputStream() )
		);
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		// Act
		StandardPipe output = pipe.setError( printer );
		// Assert
		Assertions.assertSame( pipe, output );
		Assertions.assertSame( printer, pipe.error() );
	}

	@Test
	void setError_pipe_null() {
		// Arrange
		PrintStream error = new PrintStream( new ByteArrayOutputStream() );
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		StandardPipe pipe = new StandardPipe(
			new PrintStream( new ByteArrayOutputStream() ),
			error
		)
			.setError( printer );
		Assertions.assertSame( printer, pipe.error() );
		// Act
		StandardPipe output = pipe.setError( null );
		// Assert
		Assertions.assertSame( pipe, output );
		Assertions.assertSame( error, pipe.error() );
	}

	@Test
	void getOutPipe() {
		// Arrange
		// Act
		PrintStream output = StandardPipe.getOutPipe();
		// Assert
		Assertions.assertSame( StandardPipe.DEFAULT_OUT, output );
	}


	@Test
	void getErrorPipe() {
		// Arrange
		// Act
		PrintStream output = StandardPipe.getErrorPipe();
		// Assert
		Assertions.assertSame( StandardPipe.DEFAULT_ERROR, output );
	}

	@Test
	void setOutPipe() {
		// Arrange
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		Assertions.assertNotSame( StandardPipe.DEFAULT_OUT, printer );
		Assertions.assertSame( StandardPipe.DEFAULT_OUT, StandardPipe.getOutPipe() );
		// Act
		StandardPipe.setOutPipe( printer );
		// Assert
		Assertions.assertSame( printer, StandardPipe.getOutPipe() );
	}

	@Test
	void setOutPipe_pipe_null() {
		// Arrange
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		Assertions.assertNotSame( StandardPipe.DEFAULT_OUT, printer );
		StandardPipe.setOutPipe( printer );
		Assertions.assertSame( printer, StandardPipe.getOutPipe() );
		// Act
		StandardPipe.setOutPipe( null );
		// Assert
		Assertions.assertSame( StandardPipe.DEFAULT_OUT, StandardPipe.getOutPipe() );
	}

	@Test
	void setErrorPipe() {
		// Arrange
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		Assertions.assertNotSame( StandardPipe.DEFAULT_ERROR, printer );
		Assertions.assertSame( StandardPipe.DEFAULT_ERROR, StandardPipe.getErrorPipe() );
		// Act
		StandardPipe.setErrorPipe( printer );
		// Assert
		Assertions.assertSame( printer, StandardPipe.getErrorPipe() );
	}

	@Test
	void setErrorPipe_pipe_null() {
		// Arrange
		PrintStream printer = new PrintStream( new ByteArrayOutputStream() );
		Assertions.assertNotSame( StandardPipe.DEFAULT_ERROR, printer );
		StandardPipe.setErrorPipe( printer );
		Assertions.assertSame( printer, StandardPipe.getErrorPipe() );
		// Act
		StandardPipe.setErrorPipe( null );
		// Assert
		Assertions.assertSame( StandardPipe.DEFAULT_ERROR, StandardPipe.getErrorPipe() );
	}

}
