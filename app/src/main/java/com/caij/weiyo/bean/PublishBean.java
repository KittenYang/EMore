package com.caij.weiyo.bean;


import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class PublishBean implements Serializable {

	private static final long serialVersionUID = -9160054733168344496L;

	String id = UUID.randomUUID().toString();

	String text;

    List<String> pics;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
