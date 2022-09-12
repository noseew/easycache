package org.galileo.easycache.easycachetest.funtest.base;

import org.aopalliance.intercept.Interceptor;
import org.galileo.easycache.springboot.aop.EasyCacheAdvisor;
import org.galileo.easycache.springboot.aop.EasyCacheInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.ArrayList;
import java.util.List;

public class BeanSortTest {

    @Test
    public void sortTest() {
        List<Interceptor> list = new ArrayList<>();
        list.add(new TransactionInterceptor());
        list.add(new EasyCacheInterceptor());
        // 排序和原始加入的顺序一致,
        AnnotationAwareOrderComparator.sort(list);
        System.out.println();
    }
    @Test
    public void sortTest2() {
        List<Advisor> list = new ArrayList<>();
        list.add(new BeanFactoryTransactionAttributeSourceAdvisor());
        list.add(new EasyCacheAdvisor());
        // 排序和原始加入的顺序一致,
        AnnotationAwareOrderComparator.sort(list);
        System.out.println();
    }

}
