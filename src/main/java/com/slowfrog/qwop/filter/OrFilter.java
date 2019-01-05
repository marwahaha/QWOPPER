package com.slowfrog.qwop.filter;

public class OrFilter<T> implements IFilter<T> {

    private final IFilter<T> filter1;
    private final IFilter<T> filter2;

    public OrFilter(IFilter<T> filter1, IFilter<T> filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }

    @Override
    public boolean matches(T t) {
        return filter1.matches(t) || filter2.matches(t);
    }

}
