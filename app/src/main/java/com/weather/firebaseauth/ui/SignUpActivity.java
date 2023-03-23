package com.weather.firebaseauth.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.weather.firebaseauth.databinding.ActivityLoginBinding;
import com.weather.firebaseauth.databinding.ActivitySignupBinding;
import com.weather.firebaseauth.utils.UserData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding activitySignupBinding;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String uid;
    private UserData userData;
    private String fullname, username, email, password, co_password;
    private Uri filePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind view for activity
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = activitySignupBinding.getRoot();
        setContentView(view);

        initView();
        initClickView();
    }

    private void initClickView() {
        activitySignupBinding.txtViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        activitySignupBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionX.init(SignUpActivity.this)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                openDialogForImagePick();
                            } else {
                                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        activitySignupBinding.btnSignUp.setOnClickListener(view -> {
            //        handle user SignUp button
            if (!validateUsername() | !validateEmail() | !validatePassword()) {
                return;
            }
            Log.d("TAG", "initClickView: " + password + "   " + co_password);
            if (password.equals(co_password)) {
                //    progressbar VISIBLE
                activitySignupBinding.lytProgressView.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                        (new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("TAG", "onComplete: " + task.getException());
                                if (task.isSuccessful()) {
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    storageReference.putFile(filePath)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    // Code for showing progressDialog while uploading

                                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            UserData data = new UserData(username, email, uri.toString(), activitySignupBinding.edtBio.getText().toString());
                                                            FirebaseDatabase.getInstance().getReference("UserData")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            //    progressbar GONE
                                                                            activitySignupBinding.lytProgressView.setVisibility(View.GONE);
                                                                            Toast.makeText(SignUpActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();
//                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
                                                                            Intent intent = new Intent(SignUpActivity.this, ProfilePageActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("TAG", "onProgress:onfa " + e);
                                                }
                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                    float percentage = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                    Log.d("TAG", "onProgress: " + percentage + "   " + snapshot.getTotalByteCount() + "   " + snapshot.getBytesTransferred());
                                                }
                                            });

                                } else {
                                    //    progressbar GONE
                                    activitySignupBinding.lytProgressView.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, "Check Email id or Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Password didn't match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        File f = new File(Environment.getExternalStorageDirectory().toString());
                        Log.d("TAG", "onActivityResult: " + f.getAbsolutePath());
                        for (File temp : f.listFiles()) {
                            if (temp.getName().equals("temp.jpg")) {
                                f = temp;
                                break;
                            }
                        }

                        try {
                            Log.d("TAG", "onActivityResult: vawje");
                            Bitmap bitmap;
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                    bitmapOptions);
                            activitySignupBinding.profileImage.setImageBitmap(bitmap);
                            String path = android.os.Environment
                                    .getExternalStorageDirectory()
                                    + File.separator
                                    + "Phoenix" + File.separator + "default";
                            f.delete();
                            OutputStream outFile = null;
                            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                            try {
                                outFile = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                                outFile.flush();
                                outFile.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    ActivityResultLauncher<Intent> galleryIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        filePath = data.getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(filePath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            activitySignupBinding.profileImage.setImageBitmap(bitmap);
                        } catch (Exception e) {

                        }
                    }
                }
            });
    private void openDialogForImagePick() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    } else {
                        File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    }

//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    someActivityResultLauncher.launch(intent);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.launch(intent);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void initView() {

        //you can also put string from string.xml file
        activitySignupBinding.lytToolbar.toolbar.setTitle("Register");

        //        Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
    }


    private boolean validateUsername() {
        username = activitySignupBinding.edtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(SignUpActivity.this, "Enter Your User Name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        email = activitySignupBinding.edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        password = activitySignupBinding.edtPass.getText().toString().trim().toLowerCase();
        co_password = activitySignupBinding.edtConfirmPass.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(co_password)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Co-Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() <= 6) {
            Toast.makeText(SignUpActivity.this, "Password is Very Short", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


}