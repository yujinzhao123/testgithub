package com.example.yuyu.gitbubtest.http;

public interface HttpInterface<T>{
	public void ok(T t);
	public void erro(int code);
}
