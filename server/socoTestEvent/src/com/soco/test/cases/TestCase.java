package com.soco.test.cases;

import org.json.JSONObject;

public class TestCase {

	private String name;
	private String req_method;
	private String req_parameter;
	private JSONObject req_json;
	private int resp_status;
	private JSONObject resp_json_success;
	private JSONObject resp_json_failure;
	
	
	public TestCase(){
		this.setName("");
		this.setReqMethod("");
		this.setReqParameter("");
		this.setReqJson(null);
		this.setRespJsonSuccess(null);
		this.setRespJsonFailure(null);
		this.setRespStatus(0);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReqMethod() {
		return req_method;
	}
	public void setReqMethod(String req_method) {
		this.req_method = req_method;
	}
	public String getReqParameter() {
		return req_parameter;
	}
	public void setReqParameter(String req_parameter) {
		this.req_parameter = req_parameter;
	}
	public JSONObject getReqJson() {
		return req_json;
	}
	public void setReqJson(JSONObject req_json) {
		this.req_json = req_json;
	}
	public int getRespStatus() {
		return resp_status;
	}
	public void setRespStatus(int resp_status) {
		this.resp_status = resp_status;
	}
	public JSONObject getRespJsonSuccess() {
		return resp_json_success;
	}
	public void setRespJsonSuccess(JSONObject resp_json) {
		this.resp_json_success = resp_json;
	}
	public JSONObject getRespJsonFailure() {
		return resp_json_failure;
	}
	public void setRespJsonFailure(JSONObject resp_json) {
		this.resp_json_failure = resp_json;
	}
	
	public String toString(){
		return "TestCase name: "+ this.getName() + "\n" +
				"Request method: " + this.getReqMethod() + "\n" +
				"Request parameter: " + this.getReqParameter() + "\n" +
				"Request content: " + (this.getReqJson() != null ? this.getReqJson().toString() : "{}") + "\n" +
				"Response success: " + (this.getRespJsonSuccess() != null ? this.getRespJsonSuccess().toString() : "{}") + "\n" +
				"Response failure: " + (this.getRespJsonFailure() != null ? this.getRespJsonFailure().toString() : "{}") + "\n" +
				"Response status: " + this.getRespStatus() + "\n";
	}
}
