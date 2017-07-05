package com.github.s4ke.functional.arrows;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
            //force to normal form, hack with normalization to List
            List<Future<B>> futures = this.fns.zipWith(aListM, (fn, a) ->
                    executorService.submit(() -> fn.apply(a))
            ).toList();
            return ListM.fromList(futures).map(fut -> {
                try {
                    return fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }

}
