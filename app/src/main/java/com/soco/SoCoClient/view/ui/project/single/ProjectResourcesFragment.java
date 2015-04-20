package com.soco.SoCoClient.view.ui.project.single;

//import info.androidhive.tabsswipe.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dropbox.client2.DropboxAPI;
import com.soco.SoCoClient.R;
import com.soco.SoCoClient.control.SocoApp;
import com.soco.SoCoClient.control.config.GeneralConfig;
import com.soco.SoCoClient.control.db.DBManagerSoco;
import com.soco.SoCoClient.control.dropbox.DropboxUtil;
import com.soco.SoCoClient.control.dropbox.UploaderWatcher;
import com.soco.SoCoClient.control.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectResourcesFragment extends Fragment implements View.OnClickListener {

    String tag = "ProjectResourcesFragment";
    View rootView;
    String loginEmail, loginPassword;
    int pid;
    DropboxAPI dropboxApi;
    DBManagerSoco dbManagerSoco;
    ArrayList<String> sharedFilesLocalPath;
    ArrayList<String> displayFilenames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(tag, "create project resources fragment view");
        rootView = inflater.inflate(R.layout.fragment_project_resources, container, false);
        if(rootView == null)
            Log.e(tag, "cannot find rootview");
        else
            Log.d(tag, "find rootview " + rootView);

        Log.d(tag, "add listeners");
        rootView.findViewById(R.id.add).setOnClickListener(this);
        rootView.findViewById(R.id.camera).setOnClickListener(this);

        Log.d(tag, "show project resources");
        dbManagerSoco = ((SocoApp) getActivity().getApplicationContext()).dbManagerSoco;
        sharedFilesLocalPath = dbManagerSoco.getSharedFilesLocalPath(pid);
        displayFilenames = dbManagerSoco.getSharedFilesDisplayName(pid);
        showSharedFiles(displayFilenames);

        ListView lv_files = (ListView) rootView.findViewById(R.id.lv_files);
        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                HashMap<String, String> map = (HashMap<String, String>)
                        listView.getItemAtPosition(position);
                String name = map.get(GeneralConfig.PROJECT_PNAME);
                Log.i(tag, "Click on shared file list: " + name);
                String localPath = sharedFilesLocalPath.get(position);
                Log.i(tag, "Shared file local path: " + localPath);
                viewFile(localPath);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_shared_files);
        Log.d(tag, "on create: project resources fragment");

        SocoApp socoApp = (SocoApp) getActivity().getApplication();
        pid = socoApp.getPid();
        loginEmail = socoApp.loginEmail;
        loginPassword = socoApp.loginPassword;
        dropboxApi = socoApp.dropboxApi;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){
            Log.w(tag, "Activity return result is null");
            return;
        }

        SocoApp socoApp = (SocoApp) getActivity().getApplication();

        //add file
        if (requestCode == GeneralConfig.ACTIVITY_OPEN_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.i(tag, "File selected with uri: " + uri.toString());
//            FileUtils.checkUriMeta(getActivity().getContentResolver(), uri);
            DropboxUtil.uploadToDropbox(uri, loginEmail, loginPassword, pid, dropboxApi,
                    getActivity().getContentResolver(), getActivity().getApplicationContext());
            socoApp.setUploadStatus(SocoApp.UPLOAD_STATUS_START);
            // check status
            ((SocoApp)getActivity().getApplicationContext()).uri = uri;
            Log.i(tag, "Start status service");
            Intent intent = new Intent(getActivity(), UploaderWatcher.class);
            getActivity().startService(intent);
        }

        //take picture
        if (requestCode == GeneralConfig.ACTIVITY_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(tag, "Photo uri: " + uri);
//            FileUtils.checkUriMeta(getActivity().getContentResolver(), uri);
            DropboxUtil.uploadToDropbox(uri, loginEmail, loginPassword, pid, dropboxApi,
                    getActivity().getContentResolver(), getActivity().getApplicationContext());
            socoApp.setUploadStatus(SocoApp.UPLOAD_STATUS_START);
            // check status
            socoApp.uri = uri;
            Log.i(tag, "Start status service");
            Intent intent = new Intent(getActivity(), UploaderWatcher.class);
            getActivity().startService(intent);
        }

        //always refresh the list view in the end
        sharedFilesLocalPath = dbManagerSoco.getSharedFilesLocalPath(pid);
        displayFilenames = dbManagerSoco.getSharedFilesDisplayName(pid);
        showSharedFiles(displayFilenames);

        return;
    }

    public void viewFile(String localPath){
        Log.i(tag, "View local file: " + localPath);
        File file = new File(localPath);
        Uri uri = Uri.fromFile(file);
        Log.i(tag, "Created uri: " + uri);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);
        if (type == null)
            type = "*/*";
        Log.i(tag, "File name is: " + file.getName()
                + " ,file ext is: " + ext
                + ", type is: " + type);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }

    public void showSharedFiles(ArrayList<String> sharedFiles) {
        Log.d(tag, "refresh shared files start");
        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (String filename : sharedFiles) {
            Log.d(tag, "Shared file list adding: " + filename);
            HashMap<String, String> map = new HashMap<>();
            map.put(GeneralConfig.PROJECT_PNAME, filename);
            map.put(GeneralConfig.PROJECT_PINFO, "no more info");
            list.add(map);
        }

        ListView lv_files = (ListView) rootView.findViewById(R.id.lv_files);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
                android.R.layout.simple_list_item_2,
                new String[]{GeneralConfig.PROJECT_PNAME, GeneralConfig.PROJECT_PINFO},
                new int[]{android.R.id.text1, android.R.id.text2});
        lv_files.setAdapter(adapter);
    }

    public void addFile(){
        Log.d(tag, "add file start");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, GeneralConfig.ACTIVITY_OPEN_FILE);
    }

    public void takePicture(){
        Log.i(tag, "Start activity: take picture");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(intent, GeneralConfig.ACTIVITY_TAKE_PHOTO);
    }

    @Override
    public void onClick(View v) {
        Log.d(tag, "click on " + v.getId());
        switch (v.getId()) {
            case R.id.add:
                addFile();
                break;
            case R.id.camera:
                takePicture();
                break;
        }
    }
}