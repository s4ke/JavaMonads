package com.github.s4ke.monads;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Martin Braun
 */
public class ListTest {

	@Test
	public void testListBind() {
		List<String> list = new ListM<>( Collections.singletonList( "hello" ) ).bind(
				(val) -> new ListM<>(
						Arrays.asList(
								val,
								"world"
						)
				)
		).get();
		assertEquals( 2, list.size() );
		System.out.println( list );
	}

	@Test
	public void testListMap() {
		List<List<String>> list = new ListM<>( Collections.singletonList( "hello" ) ).map(
				(val) -> Arrays.asList( val, "world" )
		).get();
		assertEquals( 1, list.size() );
		System.out.println( list );
	}

}
