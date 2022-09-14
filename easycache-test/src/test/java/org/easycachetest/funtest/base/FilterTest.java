package org.easycachetest.funtest.base;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.assertj.core.util.Sets;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.filter.AbsInvokeFilter;
import org.galileo.easycache.core.filter.FilterContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

public class FilterTest {

    @Test
    public void sizeTest() {
        CacheTest target = new CacheTest();

        Filter1 A = new Filter1("A", target);
        Filter1 B = new Filter1("B", target);
        Filter1 C = new Filter1("C", target);

        // A, B, C
        AbsInvokeFilter absInvokeFilter = AbsInvokeFilter.buildFilter(target, A, B, C);
        assert absInvokeFilter.getName().equals("A");
        assert absInvokeFilter.getTarget().equals(target);
        assert AbsInvokeFilter.getLast(absInvokeFilter).getName().equals("C");
        assert AbsInvokeFilter.getLast(absInvokeFilter).getTarget().equals(target);
        checkLink(A, new String[]{"A", "B", "C"});

        Filter1 D = new Filter1("D", target);
        // D, A, B, C
        AbsInvokeFilter.addFilterLast(D, absInvokeFilter);
        assert AbsInvokeFilter.getLast(D).equals(C);
        checkLink(D, new String[]{"D", "A", "B", "C"});

        Filter1 E = new Filter1("E", target);
        // D, A, B, E, C
        AbsInvokeFilter.addFilterAfter(D, "B", E);
        assert AbsInvokeFilter.getLast(D).equals(C);
        checkLink(D, new String[]{"D", "A", "B", "E", "C"});

        Filter1 F = new Filter1("F", target);
        // D, A, F, E, C, F
        AbsInvokeFilter.addFilterAfter(D, "C", F);
        assert AbsInvokeFilter.getLast(D).equals(F);
        assert F.getTarget().equals(target);
        checkLink(D, new String[]{"D", "A", "B", "E", "C", "F"});

        Filter1 G = new Filter1("G", target);
        // D, A, G, F, E, C, F
        AbsInvokeFilter.addFilterBefore(D, "A", G);
        assert D.getNext().equals(G);
        assert G.getTarget().equals(target);
        checkLink(D, new String[]{"D", "G", "A", "B", "E", "C", "F"});

        Filter1 H = new Filter1("H", target);
        // H, D, G, A, F, E, C, F
        AbsInvokeFilter newFirst = AbsInvokeFilter.addFilterBefore(D, "D", H);
        assert newFirst.equals(H);
        assert newFirst.getTarget().equals(target);
        checkLink(H, new String[]{"H", "D", "G", "A", "B", "E", "C", "F"});

        Filter1 I = new Filter1("I", target);
        AbsInvokeFilter.addFilterLast(H, I);
        assert AbsInvokeFilter.getLast(H).equals(I);
        assert newFirst.getTarget().equals(target);
        checkLink(H, new String[]{"H", "D", "G", "A", "B", "E", "C", "F", "I"});

        Method method = MethodUtils.getMatchingMethod(target.getClass(), "get", String.class);
        H.invoke(new FilterContext(target, null, "key", Sets.newHashSet(), OpType.GET, method,
                new Object[]{"key"}));
    }

    private void checkLink(AbsInvokeFilter first, String[] filterNames) {
        for (String name : filterNames) {
            assert first.getName().equals(name);
            first = first.getNext();
        }
    }

    public class Filter1 extends AbsInvokeFilter {
        protected Filter1(String name, CacheProxy target) {
            super(name, null, target);
        }

        @Override
        public Object invoke(FilterContext context) {
            System.out.println(getName());
            return super.invoke(context);
        }

        @Override
        protected boolean canProcess(FilterContext context) {
            return false;
        }
    }

    class CacheTest implements CacheProxy {
        @Override
        public <K, V> ValWrapper get(String key) {
            System.out.println("CacheTest get");
            return null;
        }

        @Override
        public <K, V> void put(String key, ValWrapper valWrapper) {

        }

        @Override
        public <K, V> boolean putIfAbsent(String key, ValWrapper valWrapper) {
            return false;
        }

        @Override
        public <K> boolean remove(String key) {
            return false;
        }

        @Override
        public <K> boolean removeAll(Set<String> keys) {
            return false;
        }

        @Override
        public void close() throws Exception {

        }

        @Override
        public CacheType getCacheType() {
            return CacheType.BOTH;
        }
    }

}
