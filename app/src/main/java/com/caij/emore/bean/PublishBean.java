package com.caij.emore.bean;


import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class PublishBean implements Serializable {

	private static final long serialVersionUID = -9160054733168344496L;

	long id;

	String text;

    List<String> pics;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getPics() {
		return pics;
	}

	public void setPics(List<String> pics) {
		this.pics = pics;
	}
}
