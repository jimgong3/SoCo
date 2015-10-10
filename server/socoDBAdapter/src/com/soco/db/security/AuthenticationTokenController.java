package com.soco.db.security;

import java.util.Date;

import com.soco.dbconnect.dbconnect;
import com.soco.log.Log;
import com.soco.security.AuthenticationToken;
import com.soco.security.authentication.UserToken;
import com.soco.security.encryption.AES;
import com.soco.user.User;

public class AuthenticationTokenController {

	
	public AuthenticationToken hasTokenByUId(AuthenticationToken auToken){
		Log.infor("In AuthenticationToken controller.Query token for user id: " + auToken.getUId());
		dbconnect dbc = new dbconnect();
		auToken = dbc.queryObjectOfAuthenticationToken(auToken.getQuerySQLByUId());
		if(null != auToken){
			Log.debug("Get token: " + auToken.getToken());
		} else {
			Log.error("Token is not existent.");
		}
		return auToken;
	}
	
	
	public AuthenticationToken generateTokenForUser(User user){
		boolean ret = false;
		AuthenticationToken auToken = null;
		if(user.getId() > 0){
			long expired = (new Date()).getTime() + UserToken.ONE_MONTH_MILLIONSECOND;
			String key = AES.getRandomSecKey();
			String token = UserToken.getToken(key, user.getId(), expired);
			AuthenticationTokenController atc = new AuthenticationTokenController();
			auToken = new AuthenticationToken();
			auToken.setKey(key);
			auToken.setStartTime(new Date());
			auToken.setUId(user.getId());
			auToken.setToken(token);
			auToken.setValidity(expired);
			if(atc.hasTokenByUId(auToken) != null){
				// error
				Log.error("In register. When insert authentication token, the record is already existent.");
				Log.error("To update the authentication token record for user id: " + user.getId());
				ret = atc.updateAuthenticationToken(auToken);
			} else {
				ret = atc.createAuthenticationToken(auToken);
			}
			if(!ret) {
				//
				Log.error("There is error to create/update authentication token.");
				//TODO : refresh db for authentication token later.
			}
		}else{
			Log.error("The user is invalide because the id of user is 0.");
		}
		return auToken;
	}
	
	public boolean createAuthenticationToken(AuthenticationToken auToken){
		boolean ret = false;
		Log.infor("In create Authentication Token.");
		dbconnect dbc = new dbconnect();
        int rows = dbc.exectuteUpdateSQL(auToken.getInsertSQL());
        if( rows > 0 ){
        	ret = true;
        }
		return ret;
	}
	
	public boolean updateAuthenticationToken(AuthenticationToken auToken){
		boolean ret = false;
		Log.infor("In update Authentication Token.");
		dbconnect dbc = new dbconnect();
        int rows = dbc.exectuteUpdateSQL(auToken.getUpdateSQLById());
        if( rows > 0 ){
        	ret = true;
        }
		return ret;
	}
	
	public boolean deleteAuthenticationToken(AuthenticationToken auToken){
		boolean ret = false;
		Log.infor("In delete Authentication Token.");
		dbconnect dbc = new dbconnect();
        int rows = dbc.exectuteUpdateSQL(auToken.getDeleteSQLByUId());
        if( rows > 0 ){
        	ret = true;
        }
		return ret;
	}
}
