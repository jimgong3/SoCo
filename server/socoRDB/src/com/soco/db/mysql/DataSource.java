package com.soco.db.mysql;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.soco.log.Log;

public class DataSource {

    private static DataSource     datasource = null;
    private ComboPooledDataSource cpds = null;
    //
    private String _db = "socoserver1";
    private String _port = "3306";
    private String _jdbc_url = "jdbc:mysql://socodb1.cke0xhfpla2s.ap-southeast-1.rds.amazonaws.com";
    private String _user = "soco1";
    private String _password = "soco123456";
    
    private boolean _update = true;

    private DataSource() throws IOException, SQLException, PropertyVetoException {
    	this.setDBConfigFromSystemProperty();
        this.createDBSource();
    }
    
    private void createDBSource(){
    	this.createDBSource(this._db, this._port, this._jdbc_url, this._user, this._password);
    }
    
    private void createDBSource(String dbName, String port, String url, String userName, String password){
    	if(this._update){
    		try {
    			ComboPooledDataSource cpds_ = null;
    			cpds_ = new ComboPooledDataSource();
				cpds_.setDriverClass("com.mysql.jdbc.Driver");
				cpds_.setJdbcUrl(url + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8");
	            cpds_.setUser(userName);
	            cpds_.setPassword(password);

	            // the settings below are optional -- c3p0 can work with defaults
	            cpds_.setMinPoolSize(5);
	            cpds_.setAcquireIncrement(5);
	            cpds_.setMaxPoolSize(20);
	            cpds_.setMaxStatements(180);
	            cpds_.getConnection().close();
	            
	            ////make sure everything is ok then get the datasource object
	            if(cpds != null){
    				cpds.getConnection().close();
    				cpds.close();
    				DataSources.destroy(cpds);
    				cpds = null;
    			}
	            
	            cpds = cpds_;
	            
	            this._db = dbName;
	        	this._port = port;
	        	this._jdbc_url = url;
	        	this._user = userName;
	        	this._password = password;
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //loads the jdbc driver
    		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				this._update = false;
			}
    	}
    }

    public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new DataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() {
        Connection conn = null;
		try {
			conn = this.cpds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.error("DB Connect is not valide or disconnection.");
			Log.error("DB exception: " + e.getMessage());
			Log.error("Exception stack trace: " + e.getStackTrace());
			//reconnection again
			this.createDBSource(this._db, this._port, this._jdbc_url, this._user, this._password);
		}
        
        return conn;
    }
    
    public void updateDBConfiguration(String dbName, String port, String url, String userName, String password){
    	
    	this._update = true;
    	this.createDBSource(dbName, port, url, userName, password);
    }
    
    public void setDBConfigFromSystemProperty(){
    	String url = System.getProperty("DBUrl");
    	String dbName = System.getProperty("DBName");
    	String dbPort = System.getProperty("DBPort");
    	String dbUser = System.getProperty("DBUser");
    	String dbPassword = System.getProperty("DBPassword");
    	
    	if(null != url) this._jdbc_url = url;
    	if(null != dbName) this._db = dbName;
    	if(null != dbPort) this._port = dbPort;
    	if(null != dbUser) this._user = dbUser;
    	if(null != dbPassword) this._password = dbPassword;
    }
    
    public void updateDBConfigFromSystemProperty(){
    	this._update = true;
    	this.setDBConfigFromSystemProperty();
    	this.createDBSource();
    }
}
