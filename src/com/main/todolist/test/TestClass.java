package com.main.todolist.test;

import com.main.todolist.db.DB;


public class TestClass {
    public static void insertTestInformationToDatabase(){
        DB db=DB.getInstance();
        db.iud("insert into user_info (username,password) values('behram','5678')");
    }

  

}
