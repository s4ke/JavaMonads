package com.github.s4ke.functional.arrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.s4ke.functional.monads.ListM;

/**
 * Parallelism the Arrow way
 *
 * @author Martin Braun
 */
public class ParallelArrow<A, B> implements Function<ListM<A>, ListM<B>> {

	private final ExecutorService executorService;
	private final Function<ListM<A>, ListM<B>> fn;

	private ParallelArrow(ExecutorService executorService, Function<ListM<A>, ListM<B>> fn) {
		this.executorService = executorService;
		this.fn = fn;
	}

	public ListM<B> apply(ListM<A> as) {
		int size = as.get().size();
		ListM<B> ret = this.fn.apply( as );
		if ( ret.get().size() != size ) {
			throw new IllegalArgumentException( "input length was " + size + ", but output length was " + ret.get().size() );
		}
		return ret;
	}

	@SafeVarargs
	public final ListM<B> apply(A... as) {
		return this.apply( new ListM<>( Arrays.asList( as ) ) );
	}

	public ParallelArrow<A, B> parallel(Function<A, B> arr) {
		return new ParallelArrow<>(
				this.executorService, (asM) -> {
			List<A> as = asM.get();
			//fork away the computation of the new one
			Future<B> newOne = this.spawn( () -> arr.apply( as.get( as.size() - 1 ) ) );
			//and retrieve the others
			ListM<B> list = this.fn.apply( new ListM<>( as.subList( 0, as.size() - 1 ) ) );

			List<B> ret = new ArrayList<>();
			ret.addAll( list.get() );
			try {
				ret.add( newOne.get() );
			}
			catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException( e );
			}
			return new ListM<>( ret );
		}
		);
	}

	private <X> Future<X> spawn(Supplier<X> supplier) {
		return this.executorService.submit( supplier::get );
	}

	public static <A, B> ParallelArrow<A, B> parrallel(ExecutorService exec, Function<A, B> fn) {
		return new ParallelArrow<>(
				exec, (as) -> as.map( fn )
		);
	}

}
