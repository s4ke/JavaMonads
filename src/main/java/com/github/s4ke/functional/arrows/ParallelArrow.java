package com.github.s4ke.functional.arrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import com.github.s4ke.functional.monads.ListM;

/**
 * Parallelism -  the Arrow way
 *
 * @author Martin Braun
 */
public class ParallelArrow<A, B> {

    private final ListM<Function<A, B>> fns;

    private ParallelArrow(ListM<Function<A, B>> fns) {
        this.fns = fns;
    }

    public ParallelArrow<A, B> prepend(Function<A, B> arr) {
        return new ParallelArrow<>(this.fns.prepend(arr));
    }

    static <A, B> ParallelArrow<A, B> parallel(Function<A, B> fn) {
        return parallel(new ListM<>(fn));
    }

    static <A, B> ParallelArrow<A, B> parallel(ListM<Function<A, B>> fns) {
        return new ParallelArrow<>(fns);
    }

    public Function<ListM<A>, ListM<B>> runOn(ExecutorService executorService) {
        return (aListM) -> {
            // zipWith fns to ListM<Future<B>>, force evaluation of Futures, map to ListM<B>
            return this.fns.zipWith(aListM, (fn, a) ->
                    executorService.submit(() -> fn.apply(a))
            ).eval().map(fut -> {
                try {
                    return fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }

}
