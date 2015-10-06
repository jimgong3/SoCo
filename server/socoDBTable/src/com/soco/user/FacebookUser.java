package com.soco.user;

import static java.lang.String.format;

import java.util.Date;

import com.soco.log.Log;
import com.soco.table.BaseTable;

public class FacebookUser implements BaseTable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -738628564324549991L;
	
	private static final String tableName = "fb_user";
	private long uid;
	private long id;
	private String name;
	private String email;
	private String first_name;
	private String last_name;
	private String age_range;
	private String link;
	private int gender;
	private String locale;
	private Float timezone;
	private Date updated_time;
	private Boolean verified;
	
	public FacebookUser(){
		this.uid = 0l;
		this.id = 0l;
		this.name = "";
		this.email = "";
		this.first_name = "";
		this.last_name = "";
		this.age_range = "";
		this.link = "";
		this.gender = 0;
		this.locale = "";
		this.timezone = 0f;
		this.updated_time = new Date();
		this.verified = false;
	}
	
	@Override
	public String getTableCreateSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return this.tableName;
	}

	@Override
	public String getInsertSQL() {
		// TODO Auto-generated method stub
		String sql = format(
		        "insert into %s " +
		        "( uid, " +
		          "fb_id, " +
		          "name, " +
		          "email, " +
		          "first_name, " +
		          "last_name, " +
		          "age_range, " +
		          "link, " +
		          "gender, " +
		          "locale, " +
		          "timezone, " +
		          "updated_time, " +
		          "verified " +
		          ") values(%s,%s,'%s','%s','%s','%s','%s','%s',%s,'%s','%s','%s', %s)", 
				getTableName(),
				this.getUid(),
				this.getId(),
				this.getName(),
				this.getEmail(),
				this.getFirstName(),
				this.getLastName(), 
				this.getAgeRange(), 
				this.getLink(),
				this.getGender(),
				this.getLocale(),
				this.getTimezone(),
				new java.sql.Timestamp( this.getUpdated_time().getTime()),
				this.getVerified()
				);
		Log.debug("insert sql: " + sql);
		return sql;
	}
	
	public String getQuerySQLById(){
		String sql = format("select * from %s where fb_id=%s", this.getTableName(), this.getId());
		Log.debug("query by id: " + sql);
		return sql;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getAgeRange() {
		return age_range;
	}

	public void setAgeRange(String age_range) {
		this.age_range = age_range;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Float getTimezone() {
		return timezone;
	}

	public void setTimezone(Float timezone) {
		this.timezone = timezone;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}


}
