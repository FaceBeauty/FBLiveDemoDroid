package com.nimo.fb_effect.utils;

public interface FBConfigCallBack<T> {

  void success(T list);

  void fail(Exception error);

}
