package com.example.anil.aplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    RecyclerView rv;
    ArrayList<HashMap<String, String>> songsList;
    private Context mContext=MainActivity.this;

    private static final int REQUEST = 112;
    boolean permission_flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.view);
        permissioncheck();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.hasFixedSize();

    }


    private void fetchSongsData() {
        songsList = new ArrayList<>();
        String[] STAR = {"*"};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = managedQuery(uri, STAR, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String songName = cursor.getString(cursor.
                            getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int albumId = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("songTitle", albumName + " " + songName + "___" + albumId);
                    song.put("songPath", path);
                    songsList.add(song);
                } while (cursor.moveToNext());
            }
        }
        Toast.makeText(this, ""+songsList.size(), Toast.LENGTH_SHORT).show();
    }
    private void permissioncheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
                fetchSongsData();
                rv.setAdapter(new MySongsAdapter(this, songsList));
            }
        } else {
            //do here
        }
        return ;
    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                    fetchSongsData();
                    rv.setScrollingTouchSlop(songsList.size());
                    rv.setAdapter(new MySongsAdapter(this, songsList));
                } else {
                    Toast.makeText(mContext, "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
