
package com.main.todolist.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Task;


public class DB {
    private Connection c;
    
    
    private String filter=" where status='Не выполнено'";

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    
    private static DB db=new DB();
    private DB(){
        connect();
    }
    private void connect(){
    try{
       // Class.forName("com.mysql.jdbc.Driver");
        c=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/todolist_1234", "root", "salmanovelvin1999_");
    }catch(Exception ex){
        ex.printStackTrace();
    }
    }
    public static DB getInstance(){
    return db;
    }
    
    public void iud(String query){
        try{
            Statement s=c.createStatement();
            s.execute(query);
            s.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    } 
    
    public boolean hasRowInTableForThisCondition(String table,String condition){
        boolean result=false;
        try{
            Statement s=c.createStatement();
            ResultSet r=s.executeQuery("select * from "+table+" "+condition);
            if(r.next()){
                result=true;
            }
            r.close();
            s.close();
        } catch(Exception ex){
        ex.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getColumnRowsFromTableAsList(String table,String column,String condition){
    ArrayList<String> list=new ArrayList<String>();
    
    try{
     Statement s=c.createStatement();
            ResultSet r=s.executeQuery("select "+column+" from "+table+" "+condition);
            while(r.next()){
                list.add(r.getString(column));
            }
            r.close();
            s.close();
            
    }catch(Exception ex){
    ex.printStackTrace();
    }
    
    return list;
    }

   public ObservableList<Task> getTasks(){
       ObservableList<Task> tasks=FXCollections.observableArrayList();
       
       
       try{
     Statement s=c.createStatement();
            ResultSet r=s.executeQuery("select * from tasks "+filter);
            while(r.next()){
                 Task t=new Task(r.getInt("id"),r.getString("task"),
                         r.getInt("day"),r.getDate("register").toLocalDate(),r.getString("category"),r.getString("status"));
                 tasks.add(t);
            }
            r.close();
            s.close();
            
    }catch(Exception ex){
    ex.printStackTrace();
    }
       
       
       return tasks;
   }
    
}
