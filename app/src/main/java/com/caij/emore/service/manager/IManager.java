package com.caij.emore.service.manager;

import android.content.Context;

/**
 */
public abstract class IManager {
	protected Context ctx;
    protected void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		ctx = context;
	}

    public void onCreateManager(Context ctx){
        setContext(ctx);
        doOnCreate();
    }
    /**
     * Service 服务建立的时候
     * 初始化所有的manager 调用onStartIMManager会调用下面的方法
     */
    protected abstract  void doOnCreate();
    /**
     * 上下文环境的更新
     * 1. 环境变量的clear
     * */
	public abstract void reset();
}
