package com.caij.emore.utils.rxbus;

import android.support.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Caij on 2016/6/9.
 */
public class RxBus implements RxBusInterface {

    private static RxBusInterface instance;

    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();
    private List<Listener> mActionEventListeners;

    public static RxBusInterface getDefault() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public RxBus() {
    }

    public <T> Observable<T> register(@NonNull Object tag) {
        if (mActionEventListeners != null) {
            for (Listener listener : mActionEventListeners) {
                listener.onRegister(tag);
            }
        }
        List<Subject> subjects = subjectMapper.get(tag);
        if (subjects == null) {
            subjects = new ArrayList<>();
            subjectMapper.put(tag, subjects);
        }

        Subject<T, T> subject;
        subjects.add(subject = PublishSubject.create());
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        if (mActionEventListeners != null) {
            for (Listener listener : mActionEventListeners) {
                listener.onUnregister(tag, observable);
            }
        }
        List<Subject> subjects = subjectMapper.get(tag);
        if (subjects != null) {
            subjects.remove(observable);
            if (subjects.size() == 0) {
                subjectMapper.remove(tag);
            }
        }
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        if (mActionEventListeners != null) {
            for (Listener listener : mActionEventListeners) {
                listener.onPost(tag, content);
            }
        }
        List<Subject> subjectList = subjectMapper.get(tag);
        if (subjectList != null && subjectList.size() > 0) {
             for (Subject subject : subjectList) {
                subject.onNext(content);
             }
        }
    }

    public void addEventListener(Listener listener) {
        if (mActionEventListeners == null) {
            mActionEventListeners = new ArrayList<>();
        }
        mActionEventListeners.add(listener);
    }

    @Override
    public void removeEventListener(Listener listener) {
        if (mActionEventListeners != null) {
            mActionEventListeners.remove(listener);
        }
    }

    public static class ListenerAdapter implements Listener {

        @Override
        public void onPost(Object tag, @NonNull Object content) {

        }

        @Override
        public void onRegister(Object tag) {

        }

        @Override
        public void onUnregister(Object tag, @NonNull Observable observable) {

        }
    }
}
