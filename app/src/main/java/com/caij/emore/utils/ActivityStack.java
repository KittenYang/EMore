package com.caij.emore.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Caij on 2015/8/25.
 */
public class ActivityStack {

    private static ActivityStack sActivityStack = new ActivityStack();

    private Stack<Activity> mActivityStack;

    public static ActivityStack getInstance() {
        return sActivityStack;
    }

    private ActivityStack() {
        mActivityStack = new Stack<>();
    }

    public void push(Activity activity) {
        mActivityStack.push(activity);
    }

    public Activity pop() {
        return mActivityStack.pop();
    }

    public void finishAllActivity() {
        while (!mActivityStack.empty()) {
            mActivityStack.pop().finish();
        }
    }

    public void remove(Activity activity) {
        mActivityStack.remove(activity);
    }

}
