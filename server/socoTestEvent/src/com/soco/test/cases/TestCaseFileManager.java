package com.soco.test.cases;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.soco.log.Log;

public class TestCaseFileManager {

	private static String TEST_CASE_FOLDER = "./TestCases";
	private List<TestCaseFile> _list_testcase_file = new ArrayList<TestCaseFile>();
	
	public void searchTestCaseFiles(){
		
		if(TEST_CASE_FOLDER != ""){
			File folder = new File(TEST_CASE_FOLDER);
			if(folder.exists()){
				File[] listOfFiles = folder.listFiles();
				for(File file : listOfFiles){
					if(file.isFile()){
						String filePathName = file.getPath();
						Log.debug("File name: " + file.getName());
						Log.debug("File path: " + file.getPath());
						TestCaseFile tcf = new TestCaseFile();
						tcf.setFileName(file.getName());
						boolean ret;
						try {
							ret = tcf.parseTestCase(filePathName);
							if(ret){
								this._list_testcase_file.add(tcf);
							}else{
								Log.error("There is error happened in parsing process.");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					} else if(file.isDirectory()){
						Log.debug("Folder name: " + file.getName());
					} else {
						Log.error("Unknow type: " + file.getName());
					}
				}
			} else {
				Log.error("The path " + TEST_CASE_FOLDER + " is not existent.");
			}
		}
	}

	public List<TestCaseFile> getListTestcaseFile() {
		return _list_testcase_file;
	}

	public void setListTestcaseFile(List<TestCaseFile> _list_testcase_file) {
		this._list_testcase_file = _list_testcase_file;
	}
}
