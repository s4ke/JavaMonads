package com.github.s4ke.monads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Martin Braun
 */
public class ListM<A> {

	protected final Supplier<List<A>> value;

	public ListM(Supplier<List<A>> value) {
		this.value = value;
	}

	public ListM(List<A> value) {
		this.value = () -> Collections.unmodifiableList( value );
	}

	public List<A> get() {
		return this.value.get();
	}

	public <B> ListM<B> bind(Function<A, ListM<B>> fn) {
		Supplier<List<B>> wrapped = () -> {
			List<A> a = this.value.get();
			if ( a == null || a.size() == 0 ) {
				return null;
			}
			List<B> values = new ArrayList<>();
			a.stream().map( fn::apply ).map( ListM::get ).forEach( values::addAll );
			return values;
		};
		return new ListM<>( wrapped );
	}

	public <B> ListM<B> map(Function<A, B> fn) {
		Supplier<List<B>> wrapped = () -> {
			List<A> a = this.value.get();
			if ( a == null || a.size() == 0 ) {
				return null;
			}
			return a.stream().map(fn::apply).collect( Collectors.toList() );
		};
		return new ListM<>(wrapped);
	}

}
