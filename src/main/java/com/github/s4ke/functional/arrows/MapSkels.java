package com.github.s4ke.functional.arrows;

import com.github.s4ke.functional.monads.ListM;

import java.util.function.Function;

/**
 * Created by Martin on 05.07.2017.
 */
public class MapSkels {

    public static <A, B> ParallelArrow<A, B> parMap(Function<A, B> fn) {
        return ParallelArrow.parallel(ListM.infinite(fn));
    }

}
