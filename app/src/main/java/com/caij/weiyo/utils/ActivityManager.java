package com.caij.weiyo.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Caij on 2015/8/25.
 */
public class ActivityManager {

    private static ActivityManager sActivityManager = new ActivityManager();

    private Stack<Activity> mActivityStack;

    public static ActivityManager getInstance() {
        return sActivityManager;
    }

    private ActivityManager() {
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
