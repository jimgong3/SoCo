package com.soco.app.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.soco.db.user.FacebookUserController;
import com.soco.db.user.UserController;
import com.soco.log.Log;
import com.soco.security.encryption.MD5;
import com.soco.user.FacebookUser;
import com.soco.user.User;
import com.soco.algorithm.user.UserInfor;;


public class AppUserMessageHandler implements AppMessageHandler {

	private static final String[] USER_CMD_ARRAY = { "register", "login", "logout", "social_login" };
	
	private static final String FIELD_NAME = "name";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_PHONE = "phone";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_HOMETOWN = "hometown";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "longitude";
	
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_TYPE_FB = "facebook";
	private static final String FIELD_TYPE_WECHAT = "wechat";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_FIRST_NAME = "first_name";
	private static final String FIELD_LAST_NAME = "last_name";
	private static final String FIELD_AGE_RANGE = "age_range";
	private static final String FIELD_LINK = "link";
	private static final String FIELD_GENDER = "gender";
	private static final String FIELD_LOCALE = "locale";
	private static final String FIELD_TIMEZONE = "timezone";
	private static final String FIELD_VERIFIED = "verified";
	
	
	private static ArrayList<String> _cmdList = new ArrayList<String>();
	
	private HttpResponseStatus _http_status;
	private String _http_response_content;

	@Override
	public boolean messageHandler(String version, String className, String httpMethod, String paramters, String content) {
		// TODO Auto-generated method stub
		boolean ret = false;
		Log.debug("In AppUserMessageHandler. The command " + httpMethod + " and message is: ");
		// format to json object
		try {
			this.set_http_response_content(content);
			this.set_http_status(OK);
			JSONObject jsonObj = new JSONObject(content);
			String methodName = httpMethod.toLowerCase() + "_" + className + "_" + version;
			Method method = this.getClass().getMethod(methodName, JSONObject.class, String.class);
			method.invoke(this, jsonObj, paramters);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	@SuppressWarnings("static-access")
	@Override
	public List<String> getCmdList() {
		// TODO Auto-generated method stub
		if(AppUserMessageHandler._cmdList != null){
			for(String cmd: this.USER_CMD_ARRAY){
				_cmdList.add(cmd);
			}
		} else {
			Log.debug("In AppUserMessageHandler. The command list is null.");
		}
		
		return AppUserMessageHandler._cmdList;
	}
	
	/**
	 * Register by email and password
	 * @return
	 */
	public boolean post_register_v1 (JSONObject json, String param){
		boolean ret = false;
		Log.debug("In register.");
		
		//todo: thread id and area id
		long uid = UserInfor.getUID(1, 1);
		
		if(json != null && json.has(FIELD_NAME)){
			if(json.has(FIELD_EMAIL)){
				if(json.has(FIELD_PASSWORD)){
					try {
					    User user = new User();
						String name = json.getString(FIELD_NAME);
						String email = json.getString(FIELD_EMAIL);
						String password = json.getString(FIELD_PASSWORD);
						String hometown = "";
						if(json.has(FIELD_HOMETOWN)){
						    hometown = json.getString(FIELD_HOMETOWN);
						} else {
                            Log.warn("There is no hometown.");
                        }
						String phone = "";
						if(json.has(FIELD_PHONE)){
							phone = json.getString(FIELD_PHONE);
						} else {
							Log.warn("There is no phone.");
						}
						Float latitude = (float) 0.0;
                        if(json.has(FIELD_LATITUDE)){
                            phone = json.getString(FIELD_LATITUDE);
                        } else {
                            Log.warn("There is no latitude.");
                        }
                        Float longitude = 0f;
                        if(json.has(FIELD_LONGITUDE)){
                            phone = json.getString(FIELD_LONGITUDE);
                        } else {
                            Log.warn("There is no longitude.");
                        }
						String encryptPassword = MD5.getMD5(password);
						////
						user.setId(uid);
						user.setUserName(name);
						user.setEmail(email);
						user.setUserEncryptPassword(encryptPassword);
						user.setUserPlainPassword(password);
						user.setMobilePhone(phone);
						user.setLatitude(latitude);
						user.setLongitude(longitude);
						user.setHometown(hometown);
						////
						UserController uc = new UserController();
						int rows = uc.createUser(user);
						JSONObject jsonResp = new JSONObject();
						if (rows > 0){
							jsonResp.put("status", 200);
							jsonResp.put("user_id", uid);
							jsonResp.put("token", "test-token");
							this.set_http_status(OK);
							//send out the register code to the email
							this.sendEmail(user.getEmail(), this.getEmailContent());
							ret = true;
						}
						this.set_http_response_content(jsonResp.toString());
						////
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Log.error("There is no password field in request.");
				}
			} else {
				Log.error("There is no email field in request.");
			}
		} else {
			Log.error("There is no name field in request.");
		}
		
		if(!ret){
			JSONObject jsonResp = new JSONObject();
			try {
				jsonResp.put("status", 400);
				jsonResp.put("error_code", 11);
				jsonResp.put("property", "email");
				jsonResp.put("message", "email not unique.");
				jsonResp.put("more_info", "http://www.socotechhk.com/api/help/11");
				this.set_http_status(HttpResponseStatus.BAD_REQUEST);
				this.set_http_response_content(jsonResp.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	public boolean post_login_v1 (JSONObject json, String param){
		boolean ret = false;
		Log.debug("In login.");
		return ret;
	}
	
	public boolean post_social_login_v1 (JSONObject json, String param){
		boolean ret = false;
		Log.debug("In social login.");
		
		try {
			if(null != json){
				if (json.has(FIELD_TYPE)){
					String type;
					type = json.getString(FIELD_TYPE);
					
					if(type.equals(FIELD_TYPE_FB)){
						if(json.has(FIELD_ID)){
							long id = json.getLong(FIELD_ID);
							FacebookUser fbUser = this.parseFacebookUserFromJson(json);
							FacebookUserController fbuc = new FacebookUserController();
							if(fbuc.has(id)){
								ret = fbuc.updateFBUser(fbUser);
							} else {
								ret = fbuc.createFBUser(fbUser);
							}
							if(ret){
								JSONObject jsonResp = new JSONObject();
								jsonResp.put("status", 200);
								jsonResp.put("user_id", fbUser.getUid());
								jsonResp.put("token", "test-token");
								this.set_http_status(OK);
								this.set_http_response_content(jsonResp.toString());
							}
						} else {
							Log.debug("There is no id field in json.");
						}
					} else if(type.equals(FIELD_TYPE_WECHAT)){
						
					} else {
						Log.warn("The type value is incorrect.");
					}
				}
			} else {
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!ret){
			JSONObject jsonResp = new JSONObject();
			try {
				jsonResp.put("status", 400);
				jsonResp.put("error_code", 11);
				jsonResp.put("property", "email");
				jsonResp.put("message", "email not unique.");
				jsonResp.put("more_info", "http://www.socotechhk.com/api/help/11");
				this.set_http_status(HttpResponseStatus.BAD_REQUEST);
				this.set_http_response_content(jsonResp.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public boolean post_logout_v1 (JSONObject json, String param){
		boolean ret = false;
		Log.debug("In logout.");
		return ret;
	}
	
	public void post_users_v1 (JSONObject json, String param) {
		
	}
	
	
	private FacebookUser parseFacebookUserFromJson(JSONObject json){
		FacebookUser fbUser = new FacebookUser();
		try {
			fbUser.setUid(UserInfor.getUID(1, 1));
			if(json.has(FIELD_ID)){
				if(json.has(FIELD_NAME)){
					fbUser.setId(json.getLong(FIELD_ID));
					fbUser.setName(json.getString(FIELD_NAME));
					if(json.has(FIELD_EMAIL)) fbUser.setEmail(json.getString(FIELD_EMAIL));
					if(json.has(FIELD_FIRST_NAME)) fbUser.setFirstName(json.getString(FIELD_FIRST_NAME));
					
				} else {
					
				}
			} else {
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fbUser;
	}
	
	private boolean sendEmail(String email, String content) {
		boolean ret = false;
		return ret;
	}
	
	private String getEmailContent(){
		String content = null;
		return content;
	}

	@Override
	public FullHttpResponse getResponse() {
		// TODO Auto-generated method stub
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, this.get_http_status(), Unpooled.wrappedBuffer(this.get_http_response_content().getBytes()));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
		return response;
	}

	public HttpResponseStatus get_http_status() {
		return _http_status;
	}

	public void set_http_status(HttpResponseStatus _http_status) {
		this._http_status = _http_status;
	}

	public String get_http_response_content() {
		return _http_response_content;
	}

	public void set_http_response_content(String _http_response_content) {
		this._http_response_content = _http_response_content;
	}

}
