package com.slowfrog.qwop.filter;

public interface IFilter<T> {
    boolean matches(T t);
}
