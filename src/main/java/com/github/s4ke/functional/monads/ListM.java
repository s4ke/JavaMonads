package com.github.s4ke.functional.monads;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Martin Braun
 */
public class ListM<A> {

    public static final ListM EMPTY = new ListM();

    private final Supplier<A> head;
    private final Supplier<ListM<A>> tail;

    private A headValue;
    private ListM<A> tailValue;

    private ListM() {
        this(null, null);
    }

    public ListM(A head) {
        this(() -> head);
    }

    public ListM(Supplier<A> head) {
        this(head, () -> EMPTY);
    }

    public ListM(Supplier<A> head, Supplier<ListM<A>> tail) {
        this.head = head;
        this.tail = tail;
    }

    public synchronized A head() {
        if (this.headValue == null) {
            this.headValue = this.head.get();
        }
        return this.headValue;
    }

    public synchronized ListM<A> tail() {
        if (this.tailValue == null) {
            this.tailValue = this.tail.get();
        }
        return this.tailValue;
    }

    public ListM<A> prepend(A value) {
        return prepend(() -> value);
    }

    public ListM<A> prepend(Supplier<A> value) {
        return new ListM<>(value, () -> this);
    }

    public List<A> toList() {
        List<A> ret = new ArrayList<>();
        ListM<A> cur = this;
        while (cur != EMPTY) {
            ret.add(cur.head());
            cur = cur.tail();
        }
        return ret;
    }

    public ListM<A> eval() {
        ListM<A> cur = this;
        while (cur != EMPTY) {
            cur.head();
            cur = cur.tail();
        }
        return this;
    }

    public <B> ListM<B> map(Function<A, B> fn) {
        return new ListM<>(
                () -> fn.apply(this.head()),
                () -> {
                    ListM<A> tail = this.tail();
                    if (tail != EMPTY) {
                        return this.tail().map(fn);
                    } else {
                        return (ListM<B>) EMPTY;
                    }
                });
    }

    public <B, C> ListM<C> zipWith(ListM<B> right, BiFunction<A, B, C> zipper) {
        return new ListM<>(
                () -> zipper.apply(this.head(), right.head()),
                () -> {
                    ListM<A> ownTail = this.tail();
                    ListM<B> rightTail = right.tail();
                    if (ownTail != EMPTY && rightTail != EMPTY) {
                        return ownTail.zipWith(rightTail, zipper);
                    } else {
                        return (ListM<C>) EMPTY;
                    }
                }
        );
    }

    public static <A> ListM<A> fromList(final List<A> list) {
        ListM<A> ret = new ListM<>(list.get(list.size() - 1));
        for (int i = list.size() - 2; i >= 0; --i) {
            ret = ret.prepend(list.get(i));
        }
        return ret;
    }

    public static <A> ListM<A> infinite(A a) {
        return new ListM<>(() -> a,
                () -> infinite(a));
    }

    public static <A, B> ListM<Function<A, B>> infinite(Function<A, B> a) {
        return new ListM<>(() -> a,
                () -> infinite(a));
    }

}
