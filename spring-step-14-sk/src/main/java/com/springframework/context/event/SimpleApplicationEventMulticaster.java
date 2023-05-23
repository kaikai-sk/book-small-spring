package com.springframework.context.event;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.context.ApplicationEvent;
import com.springframework.context.ApplicationListener;

/**
 *
 *
 *
 * 作者：DerekYRC https://github.com/DerekYRC/mini-spring
 * @description Simple implementation of the {@link ApplicationEventMulticaster} interface.
 * @date 2022/3/13
 *  /CodeDesignTutorials
 *
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void multicastEvent(final ApplicationEvent event) {
        for (final ApplicationListener listener : getApplicationListeners(event)) {
            listener.onApplicationEvent(event);
        }
    }

}
