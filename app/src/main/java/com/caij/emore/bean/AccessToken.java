package com.caij.emore.bean;


import com.caij.emore.utils.LogUtil;

import java.util.concurrent.TimeUnit;

public class AccessToken  {

	private static final long serialVersionUID = 1L;

	private String verifier;

	private String uid;
	
	private String access_token;

	private long expires_in;

    private long create_at = System.currentTimeMillis();

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getAccess_token() {
		return access_token;
	}

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }

    public boolean isExpired() {
		long time  = expires_in * 1000 - (System.currentTimeMillis() - create_at);
		String days = String.valueOf(TimeUnit.MILLISECONDS.toDays(time));
		LogUtil.d(this, "expires_in : %s", expires_in);
        LogUtil.d(this, "%s 还有 %s天失效", uid, days);

        return time <= 0;
    }


}
