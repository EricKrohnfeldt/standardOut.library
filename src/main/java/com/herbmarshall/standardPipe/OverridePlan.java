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

import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Safely override the {@link OutputStream} of a {@link Standard}. */
public class OverridePlan {

	private final DivergingOutputStream stream;
	private final OutputStream override;

	OverridePlan( DivergingOutputStream stream, OutputStream override ) {
		this.stream = Objects.requireNonNull( stream );
		this.override = Objects.requireNonNull( override );
	}

	/**
	 * Execute {@code action} using the override {@link OutputStream}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Runnable} code to execute.
	 * @throws IllegalStateException If an override is already in place
	 * @throws NullPointerException if {@code action} is null
	 */
	public void execute( Runnable action ) {
		Objects.requireNonNull( action );
		execute( pipe -> {
			action.run();
			return null;
		} );
	}

	/**
	 * Execute {@code action} using the override {@link OutputStream}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Consumer} that will accept the updated {@link OutputStream}
	 * @throws IllegalStateException If an override is already in place
	 * @throws NullPointerException if {@code action} is null
	 */
	public void execute( Consumer<OutputStream> action ) {
		Objects.requireNonNull( action );
		execute( pipe -> {
			action.accept( pipe );
			return null;
		} );
	}

	/**
	 * Execute {@code action} using the override {@link OutputStream}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Supplier} that will return a value from this method.
	 * @return The output {@code action}.
	 * @throws IllegalStateException If an override is already in place
	 * @throws NullPointerException if {@code action} is null
	 */
	public <T> T execute( Supplier<T> action ) {
		Objects.requireNonNull( action );
		Function<OutputStream, T> wrapped = pipe -> action.get();
		return execute( wrapped );
	}

	/**
	 * Execute {@code action} using the override {@link OutputStream}.
	 * Override will be cleared after {@code action} has executed.
	 * @param action {@link Function} that will accept a {@link OutputStream} and return a value from this method.
	 * @return The output {@code action}.
	 * @throws IllegalStateException If an override is already in place
	 * @throws NullPointerException if {@code action} is null
	 */
	public <T> T execute( Function<OutputStream, T> action ) {
		Objects.requireNonNull( action );
		stream.override( override );
		try {
			return action.apply( override );
		}
		finally {
			stream.reset();
		}
	}

}
