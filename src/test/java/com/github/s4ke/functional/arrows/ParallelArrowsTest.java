package com.github.s4ke.functional.arrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.s4ke.functional.monads.ListM;

import org.junit.Test;

/**
 * @author Martin Braun
 */
public class ParallelArrowsTest {

    public Integer plusOne(Integer val) {
        return val + 1;
    }

    public static List<Integer> values() {
        List<Integer> ret = new ArrayList<>(1000);
        for (int i = 1; i < 1001; ++i) {
            ret.add(i);
        }
        return ret;
    }

    @Test
    public void test() {
        ExecutorService exec = Executors.newFixedThreadPool(16);
        ListM<Integer> values = ListM.fromList(values());

        try {
            ListM<Integer> result =
                    ParallelArrow.parallel(ListM.infinite(this::plusOne))
                            .runOn(exec)
                            .apply(values);
            System.out.println(
                    result.toList()
            );
        } finally {
            exec.shutdown();
        }
    }

}
