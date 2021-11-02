package com.example.yuehaoting.kotlin;

import com.example.yuehaoting.kotlin.MyMainJava.MyMain2;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 作者: LiangKe 时间: 2021/10/21 15:47 描述:
 */
public class MyMainJava {

  public void play() {
    System.out.println("巴卡卡巴");
  }

  public static final class MyMain {

    MyMainJava m = new MyMainJava();
  }

  public final class MyMain2 extends MyMainJava {

    MyMainJava m = MyMainJava.this;
  }

  public static void main(String[] args) {
    MyMainJava m = new MyMainJava();
    m.play();
    MyMain m2 = new MyMainJava.MyMain();
    MyMain2 m3 = (MyMain2) m;
    m3.m.play();

    m2.m.play();


    int a = 5;
    switch(a) {
      case 1:
      case 4:
      case 5:
      case 7:
      case 9:
      case 45:
      default:
    }

  }

  public void kk() {
    MyMain2 m3 = new MyMainJava.MyMain2();
    m3.m.play();

  }



}


