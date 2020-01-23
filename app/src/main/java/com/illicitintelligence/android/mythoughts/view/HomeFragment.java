package com.illicitintelligence.android.mythoughts.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.illicitintelligence.android.mythoughts.R;
import com.illicitintelligence.android.mythoughts.util.Constants;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    @BindView(R.id.username_tv)
    TextView username;
    @BindView(R.id.profile_pic)
    ImageView profilePic;
    private StorageReference mStorageRef;
    private final String TAG = "TAG_X";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_frag,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        Log.d(TAG, "onViewCreated: ");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_pics/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
//        loadNewImage(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        try {
            Log.d(TAG, "onViewCreated: "+FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
            loadNewImage(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        }catch (Exception n){
            Log.d(TAG, "onViewCreated: "+n.getMessage());
        }
        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()!=null&&
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName().length()>0){
            username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }else {
            username.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }


    @OnClick(R.id.profile_pic)
    public void changePic(View view){
        if(getContext().checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
            }
        }else{
            requestPermissions(new String[]{Manifest.permission.CAMERA},Constants.CAMERA_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== Constants.CAMERA_REQUEST_CODE&&resultCode== Activity.RESULT_OK){
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
            if(capturedImage!=null){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                capturedImage.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                UploadTask uploadTask = mStorageRef.putBytes(imageBytes);
                 

                uploadTask.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        mStorageRef.getDownloadUrl().addOnCompleteListener(taskUri->{
                            setNewImage(taskUri.getResult());
                            loadNewImage(taskUri.getResult());
                        });
                        //setNewImage(task.getResult().getUploadSessionUri());
                        //loadNewImage(task.getResult().getUploadSessionUri());
//                        setNewImage(mStorageRef.getDownloadUrl().getResult());
//                        loadNewImage(mStorageRef.getDownloadUrl().getResult());
                    }else{
                        Toast.makeText(getContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    private void setNewImage(Uri uri) {
        UserProfileChangeRequest userChange = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        try{
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userChange);
            Toast.makeText(getContext(),"Profile picture changed",Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNewImage(Uri uri){
        Log.d(TAG, "loadNewImage: "+uri.toString());
        Glide.with(getContext())
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                .load(uri)
                .into(profilePic);
    }
}
