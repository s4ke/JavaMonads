package com.github.s4ke.functional.monads;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Martin Braun
 */
public class MaybeTest {

	@Test
	public void testNothing() {
		String str = new Maybe<>( (String) null ).bind(
				(val) -> {
					fail( "for null this should not have been called" );
					return new Maybe<>( "" );
				}
		).get();
		assertEquals( null, str );
	}

	@Test
	public void testHasValue() {
		String str = new Maybe<>( "String" ).bind(
				(val) -> {
					System.out.println( "String" );
					return new Maybe<>( "worked" );
				}
		).get();
		System.out.println( str );
		assertEquals( "worked", str );
	}

	@Test
	public void testLaziness() {
		boolean called[] = new boolean[1];
		Maybe<String> maybe = new Maybe<>( "nothing" ).bind(
				(val) -> {
					called[0] = true;
					return new Maybe<>( "value" );
				}
		);
		assertFalse( called[0] );
		assertEquals( "value", maybe.get() );
	}

}
