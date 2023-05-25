package com.example.shoesee.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.shoesee.GetNetworkJson;
import com.example.shoesee.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class IdentifyFragment extends Fragment {

    private static File outputImage;
    private Context context;
    public static final String TAG="MyLog";
    int flag = 3;
    private static final  int REQUEST_CAMERA_CAPTURE=1;
    private static final  int REQUEST_IMAGE_CAPTURE=2;
    private static final int IMAGE_CAPTURE_FLAG = 3;
//    public static final int CROP_PHOTO = 4;

    Button takePhoto, uploadPhoto, choosePhoto;

    //    private TextView messageText;
    private Uri imageUri; //圖片路徑
    private ImageView mImageView;
    private String upLoadServerUri = null;
    private String getServerUri = null;
    public String imagemessage=null;
    public  String vegetableSource = null;
    private String filename; //圖片名稱
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    boolean hasPermissionDismiss = false;//有权限没有通过

    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码
    private Intent intent;
    public static TextView messageText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = getLayoutInflater().inflate(R.layout.fragment_identify,container,false);
        initPermission();
        context=getActivity();

        uploadPhoto = root.findViewById(R.id.uploadPhoto);
        choosePhoto = root.findViewById(R.id.choosePhoto);
        takePhoto = root.findViewById(R.id.takePhoto);
        mImageView = (ImageView)root.findViewById(R.id.iv);
        messageText = root.findViewById(R.id.message);
        viewSwitcher = (ViewSwitcher)root.findViewById(R.id.intentCamera_activity_viewSwitcher);
        vImageView = (ImageView)root.findViewById(R.id.intentCamera_activity_igv);

        //設定連結到PHP的網址。(建議用手機來測試，再連到固定IP的網址。)
        upLoadServerUri = "http://140.126.130.201/shoesee/111/uploadImg.php";
//        getServerUri = "http://140.126.130.201/vegetable/alexnet_code/result.txt";
        initView();

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissionDismiss) {
                    initPermission();
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(System.currentTimeMillis());
                    filename = format.format(date);
                    Log.d("文件",""+Environment.getExternalStorageDirectory());
                    getFolderPath(filename);

                    //將File對象轉換為Uri並啟動照相程序
                    imageUri = FileProvider.getUriForFile(
                            getActivity(),
                            getActivity().getPackageName() + ".fileprovider", outputImage);
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //照相
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定圖片輸出地址
                    Log.d("路徑", ""+ imageUri);
                    startActivityForResult(intent,REQUEST_CAMERA_CAPTURE); //啟動照相
//        拍完照startActivityForResult() 結果返回onActivityResult()函數
                }

            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasPermissionDismiss){
                    initPermission();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasPermissionDismiss){
                    initPermission();
                }else{
                    if(mImageView.getDrawable() == null){
                        messageText.setText("請拍照或選擇圖片");
                    }else {
                        //按上傳檔案的按鈕，要處理時，會用Thread 來處理 Http Post的動作。
                        dialog = ProgressDialog.show(getActivity(), "", "上傳辨識中...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(imagemessage);
                            }
                        }).start();
                    }
                }

            }
        });

        return root;

    }
    ViewSwitcher viewSwitcher;
    ImageView vImageView;
    private void initPermission() {
        mPermissionList.clear();//清空没有通过的权限
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(getActivity(), permissions, mRequestCode);
        }
    }
    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }
        }

    }
    // 不再提示权限时的展示对话框
    AlertDialog mPermissionDialog;

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(getActivity())
                    .setMessage("不給授權是要怎麼做啦!")
                    .setPositiveButton("知道了! 進入設置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package",getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //關閉頁面或者做其他操作
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //關閉對話框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }


    public void initView(){

        viewSwitcher.setInAnimation(getActivity(),android.R.anim.slide_in_left);
        viewSwitcher.setOutAnimation(getActivity(),android.R.anim.slide_out_right);

    }
    public static File getFolderPath (String filename )
    {
        //存儲至DCIM文件夾
        File imgpath = null;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imgpath = new File(path, "SHOESTABLE");

        if (!imgpath.exists())
        {
            imgpath.mkdirs();
        }
        Log.d("檔案", ""+imgpath);
        outputImage = new File(imgpath, filename + ".jpg");
        try {
            if(outputImage.exists())
            {
                outputImage.delete();
                Log.d("圖片", ""+outputImage);
            }else {
                outputImage.createNewFile();
                Log.d("create圖片", ""+outputImage);
            }
        } catch(IOException e) {
            Log.d("異常", ""+ e);
        }
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CAMERA_CAPTURE&&resultCode==getActivity().RESULT_OK){
            if(flag != IMAGE_CAPTURE_FLAG){
                flag = IMAGE_CAPTURE_FLAG;
                viewSwitcher.showNext();
            }
            //廣播重新整理相簿
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(Uri.fromFile(outputImage));
            context.sendBroadcast(intentBc);

            mImageView.setImageURI(imageUri);
            imagemessage = getFilePathByUri(getActivity(), Uri.fromFile(outputImage));
        }else if(requestCode==REQUEST_IMAGE_CAPTURE&&resultCode==getActivity().RESULT_OK){
            Uri uri=data.getData();
            mImageView.setImageURI(uri);
            imagemessage = getFilePathByUri(getActivity(), uri);
            Log.d(TAG, "完成");
            Log.d("onActivityResult","uri = "+uri);
            Log.d("onActivityResult","imagemessage = "+imagemessage);
        }
    }

    //進行檔案上傳的動作。
    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.d("uploadFile", "Source File not exist :" + imagemessage);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("錯誤", imagemessage);
                    messageText.setText("Source File not exist :"+ imagemessage);
                }
            });
            return 0;
        }
        else
        {
            try {

                //使用HttpURLConnection，連到Server瑞的網頁
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                final URL url = new URL(upLoadServerUri);
//                final URL result = new URL(getServerUri);
                Log.d("測試", ""+url);
                //打開 HTTP 連到 URL物件上的網頁，再設定要以多媒體的方式，POST資料到Server端。
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                Log.d("測試", ""+dos);
                //上傳檔案，不是一次就可以傳送上去。要一部份一部份的上傳。
                //所以，要先設定一個buffer，將檔案的內容分次上傳。
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                //傳送多媒體的form資料。
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //接收Server端的回傳訊息及代碼
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            String urlString = "http://140.126.130.201/shoesee/111/condb.php";
                            GetNetworkJson process = new GetNetworkJson();
                            process.execute(urlString);
                        }
                    });
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                dialog.dismiss();
                ex.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }

    //將以 content://開頭的轉換為路徑
    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}



