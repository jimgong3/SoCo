package com.soco.SoCoClient._v2.datamodel;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.soco.SoCoClient._v2.businesslogic.config.DbConfig;
import com.soco.SoCoClient._v2.businesslogic.database.DbHelper;

import java.util.ArrayList;

public class Task {

    String tag = "Task";

    //fields saved to db
    int taskIdLocal;
    int taskIdServer;
    String taskName;
    String taskPath;
    int isTaskActive;

    //fields not saved to db
    SQLiteDatabase db;

    public Task(Context context){
        Log.d(tag, "create new task object");

        this.taskIdLocal = DbConfig.ENTITIY_ID_NOT_READY;
        this.taskIdServer = DbConfig.ENTITIY_ID_NOT_READY;
        this.isTaskActive = DbConfig.TASK_IS_ACTIVE;

        DbHelper dbHelper= new DbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public Task(Cursor cursor){
        this.taskIdLocal = cursor.getInt(cursor.getColumnIndex(DbConfig.COLUMN_TASK_TASKIDLOCAL));
        this.taskIdServer = cursor.getInt(cursor.getColumnIndex(DbConfig.COLUMN_TASK_TASKIDSERVER));
        this.taskName = cursor.getString(cursor.getColumnIndex(DbConfig.COLUMN_TASK_TASKNAME));
        this.taskPath = cursor.getString(cursor.getColumnIndex(DbConfig.COLUMN_TASK_TASKPATH));
        this.isTaskActive = cursor.getInt(cursor.getColumnIndex(DbConfig.COLUMN_TASK_ISTASKACTIVE));
    }

    public void save(){
        if(taskIdLocal == DbConfig.ENTITIY_ID_NOT_READY) {
            Log.d(tag, "save new task");
            saveNew();
        }else{
            Log.d(tag, "update existing task");
            update();
        }
    }

    void saveNew(){
        Log.d(tag, "save new task to local database");
        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(DbConfig.COLUMN_TASK_TASKNAME, taskName);
            cv.put(DbConfig.COLUMN_TASK_TASKPATH, taskPath);
            cv.put(DbConfig.COLUMN_TASK_ISTASKACTIVE, isTaskActive);
            db.insert(DbConfig.TABLE_TASK, null, cv);
            db.setTransactionSuccessful();
            Log.d(tag, "new task inserted into database");
        } finally {
            db.endTransaction();
        }

        Log.d(tag, "get task id local from database");
        int tidLocal = -1;
        String query = "select max (" + DbConfig.COLUMN_TASK_TASKIDLOCAL
                + ") from " + DbConfig.TABLE_TASK;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            tidLocal = cursor.getInt(0);
        if (taskIdLocal == DbConfig.ENTITIY_ID_NOT_READY){
            Log.d(tag, "update task id local: " + tidLocal);
            taskIdLocal = tidLocal;
        }

        //todo: save new task to server
    }

    void update(){
        Log.d(tag, "update existing task to local database");
        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(DbConfig.COLUMN_TASK_TASKIDLOCAL, taskIdLocal);
            cv.put(DbConfig.COLUMN_TASK_TASKIDSERVER, taskIdServer);
            cv.put(DbConfig.COLUMN_TASK_TASKNAME, taskName);
            cv.put(DbConfig.COLUMN_TASK_TASKPATH, taskPath);
            cv.put(DbConfig.COLUMN_TASK_ISTASKACTIVE, isTaskActive);
            db.update(DbConfig.TABLE_TASK, cv,
                    DbConfig.COLUMN_TASK_TASKIDLOCAL + " = ?",
                    new String[]{String.valueOf(taskIdLocal)});
            db.setTransactionSuccessful();
            Log.d(tag, "task updated into database");
        } finally {
            db.endTransaction();
        }

        //todo: update task to server

    }

    public void delete(){
        Log.d(tag, "delete existing task");
        if(taskIdLocal == DbConfig.ENTITIY_ID_NOT_READY){
            Log.e(tag, "cannot delete a non-existing task");
        } else {
            db.delete(DbConfig.TABLE_TASK,
                    DbConfig.COLUMN_TASK_TASKIDLOCAL + " = ?",
                    new String[]{String.valueOf(taskIdLocal)});
            Log.d(tag, "task deleted from database");
        }

        //todo: delete task from server
    }

    public void addMember(Contact contact){}

    public ArrayList<Contact> loadMembers(){
        return null;
    }

    public void updateAttribute(Attribute attr){}

    public void clearAttributes(){}

    public ArrayList<Attribute> loadAttributes(){
        return null;
    }

    public void addComment(Comment comment){}

    public ArrayList<Comment> loadComments(){
        return null;
    }

    public void addAttachment(Attachment attachment){}

    public ArrayList<Attachment> loadAttachments(){
        return null;
    }

    public String toString() {
        return "Task{" +
                "taskIdLocal=" + taskIdLocal +
                ", taskIdServer=" + taskIdServer +
                ", taskName='" + taskName + '\'' +
                ", taskPath='" + taskPath + '\'' +
                ", isTaskActive=" + isTaskActive +
                '}';
    }

    public int getTaskIdLocal() {
        return taskIdLocal;
    }

    public void setTaskIdLocal(int taskIdLocal) {
        this.taskIdLocal = taskIdLocal;
    }

    public int getTaskIdServer() {
        return taskIdServer;
    }

    public void setTaskIdServer(int taskIdServer) {
        this.taskIdServer = taskIdServer;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskPath() {
        return taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public int getIsTaskActive() {
        return isTaskActive;
    }

    public void setIsTaskActive(int isTaskActive) {
        this.isTaskActive = isTaskActive;
    }
}
