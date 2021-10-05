package com.example.kontabai.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kontabai.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    TextView createProfile,createAsDriver;
    EditText fullname,phonenumber;
    CircleImageView imageView;
    String[] manifest={Manifest.permission.CAMERA};
    Uri imageUri;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private static final int PERMISSION_CAMERA_CODE=121;
    OutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initViews();
        imageView.setOnClickListener(v->{
            checkPermission(PERMISSION_CAMERA_CODE);
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            PopupMenu popupMenu=new PopupMenu(UserProfile.this,imageView);
            popupMenu.getMenuInflater().inflate(R.menu.popmenu_imageview,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId()==R.id.fromCamera){
                    activityResultLauncher.launch(intent);
                    return true;
                }else if(menuItem.getItemId()==R.id.fromGallery){
                    activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent intent1=result.getData();
                            if(intent1!=null){
                                try {
                                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(
                                            getContentResolver(),intent1.getData()
                                    );
                                    imageView.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    return true;
                }
                return false;
            });
        });
        createAsDriver.setOnClickListener(view -> startActivity(new Intent(UserProfile.this,CreateDriverProfile.class).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        )));
    }

    private void checkPermission(int permissionCameraCode)
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,manifest,permissionCameraCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CAMERA_CODE && grantResults[0]==PackageManager.PERMISSION_DENIED){
            AlertDialog alertDialog=new AlertDialog.Builder(this).create();
            View alertView=getLayoutInflater().inflate(R.layout.settings_alert,null,false);
            alertDialog.setView(alertView);
            alertDialog.show();
            alertDialog.setCancelable(true);
            TextView textView=alertView.findViewById(R.id.settingsButton);
            textView.setOnClickListener(v->{
                Intent intent=new Intent(android.provider.Settings.ACTION_SETTINGS);
                startActivity(intent);
            });
            alertDialog.dismiss();
        }
    }

    private void getActivityLauncher(){
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK)
                        {
                            if(result.getData()!=null){
                                Bundle bundle=result.getData().getExtras();
                                Bitmap bitmap= (Bitmap) bundle.get("data");
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                });
    }
    private void initViews() {
        createProfile=findViewById(R.id.createProfile);
        createAsDriver=findViewById(R.id.createProfileDriver);
        fullname=findViewById(R.id.fullName);
        phonenumber=findViewById(R.id.phoneNumber);
        imageView=findViewById(R.id.profileImage);
        getActivityLauncher();
    }

    private void saveImageInInternalStorage()
    {
        BitmapDrawable bitmapDrawable=(BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();

        File filepath= Environment.getExternalStorageDirectory();
        File dir=new File(filepath.getAbsolutePath()+"/KontaBai/");
        dir.mkdir();
        File file=new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputStream=new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}