package com.caij.weiyo.utils.rxbus;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Caij on 2016/6/9.
 */
public class RxBus {

    private static RxBus instance;
    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    public static RxBus get() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    private RxBus() {
    }

    public <T> Observable<T> register(@NonNull Object tag) {
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
        List<Subject> subjects = subjectMapper.get(tag);
        if (subjects != null) {
            subjects.remove(observable);
            if (subjects.size() == 0) {
                subjectMapper.remove(tag);
            }
        }
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (subjectList != null && subjectList.size() > 0) {
             for (Subject subject : subjectList) {
                subject.onNext(content);
             }
        }
    }
}
