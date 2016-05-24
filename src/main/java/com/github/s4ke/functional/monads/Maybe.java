package com.github.s4ke.functional.monads;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Martin Braun
 */
public class Maybe<A> {

	protected final Supplier<A> value;

	public Maybe(Supplier<A> value) {
		this.value = value;
	}

	public Maybe(A value) {
		this.value = () -> value;
	}

	public A get() {
		return this.value.get();
	}

	public <B> Maybe<B> bind(Function<A, Maybe<B>> fn) {
		Supplier<B> wrapped = () -> {
			A a = this.value.get();
			if ( a == null ) {
				return null;
			}
			return fn.apply( a ).value.get();
		};
		return new Maybe<>( wrapped );
	}

	public <B> Maybe<B> map(Function<A, B> fn) {
		Supplier<B> wrapped = () -> {
			A a = this.value.get();
			if ( a == null ) {
				return null;
			}
			return fn.apply( a );
		};
		return new Maybe<>( wrapped );
	}

}
