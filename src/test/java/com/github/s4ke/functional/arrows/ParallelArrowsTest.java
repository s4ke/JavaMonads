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

	public ListM<Integer> plusOneL(ListM<Integer> val) {
		return val.map( this::plusOne );
	}

	public static List<Integer> values() {
		List<Integer> ret = new ArrayList<>( 1000 );
		for ( int i = 1; i < 1001; ++i ) {
			ret.add( i );
		}
		return ret;
	}

	@Test
	public void test() {
		ExecutorService exec = Executors.newFixedThreadPool( 4 );
		ListM<Integer> values = new ListM<>( values() );

		try {
			System.out.println(
					ParallelArrow.parrallel( exec, this::plusOneL )
							.parallel( this::plusOneL )
							.apply(
									values,
									values
							)
			);
		}
		finally {
			exec.shutdown();
		}
	}

}
