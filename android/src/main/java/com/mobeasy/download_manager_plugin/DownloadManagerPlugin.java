package com.mobeasy.download_manager_plugin;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.File;
import java.util.HashMap;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
//

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
//


/** DownloadManagerPlugin */
public class DownloadManagerPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private  long downloadID ;
  Context context ;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "download_manager_plugin");
    channel.setMethodCallHandler(this);
     context = flutterPluginBinding.getApplicationContext() ;
    context.registerReceiver(onDownloadComplete , new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) ;



  }
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    HashMap<String, String> map =(HashMap) call.arguments;



    // Toast.makeText(this, map.get("name"), Toast.LENGTH_SHORT).show();
    if(call.method.equals("downloadManager")) {
      beginDownload(map.get("url") , map.get("name") , map.get("mime"));
    }

  }


  private void beginDownload(String url , String name , String mime){
    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)  + "/"+name);

    String mimeString = MimeTypeMap.getFileExtensionFromUrl(url);



    Log.e("Path:" , file.getPath()+ "\n" +"Mime Type :" +mimeString);

    DownloadManager.Request request ;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {


      request=new DownloadManager.Request(Uri.parse(url))
              .setTitle(name)
              .setDescription("Downloading")
              .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
              .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name)
              .setRequiresCharging(false)
              .setAllowedOverMetered(true)
              .setMimeType(mime)
              .setAllowedOverRoaming(false);
    }
    else{
      request=new DownloadManager.Request(Uri.parse(url))
              .setTitle(name)
              .setDescription("Downloading")
              .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
              .setDestinationUri(Uri.fromFile(file) )
              .setMimeType(mimeString)
              .setAllowedOverRoaming(false);
    }

    DownloadManager downloadManager=(DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
    downloadID=downloadManager.enqueue(request);
    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

  }

  private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID , -1) ;
      if(downloadID == id)
        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
    }
  };



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


}
