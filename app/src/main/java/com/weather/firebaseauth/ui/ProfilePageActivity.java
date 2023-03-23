package com.weather.firebaseauth.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.weather.firebaseauth.adapter.WeatherDataAdapter;
import com.weather.firebaseauth.databinding.ActivityProfilePageBinding;
import com.weather.firebaseauth.model.FiveDayResponse;
import com.weather.firebaseauth.model.WeatherListData;
import com.weather.firebaseauth.utils.UserData;
import com.weather.firebaseauth.viewmodel.BookSearchViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ProfilePageActivity extends AppCompatActivity {

    private ActivityProfilePageBinding activityProfilePageBinding;
    private BookSearchViewModel viewModel;
    // our Firebase Database.
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<WeatherListData> weatherListData = new ArrayList<>();

    // creating a variable for our
    // Database Reference for Firebase.
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String lastCity = "kolkata";

    private WeatherDataAdapter weatherDataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfilePageBinding = ActivityProfilePageBinding.inflate(getLayoutInflater());
        View view = activityProfilePageBinding.getRoot();
        setContentView(view);

        initView();
        initClick();
        initViewModel(lastCity);

    }

    private void initView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activityProfilePageBinding.weatherrecyclerview.setLayoutManager(linearLayoutManager);

        weatherDataAdapter = new WeatherDataAdapter();
        activityProfilePageBinding.weatherrecyclerview.setAdapter(weatherDataAdapter);

        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("UserData");
        getFirebaseProfileData();


    }

    private void initClick() {
        weatherDataAdapter.setClickListener(new WeatherDataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Gson gson = new Gson();
                Intent intent=new Intent(ProfilePageActivity.this,WeatherDetailScreen.class);
                intent.putExtra("myjson",gson.toJson(weatherListData.get(position)));
                startActivity(intent);
            }
        });
        activityProfilePageBinding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityProfilePageBinding.extSearchCity.setVisibility(View.VISIBLE);
                activityProfilePageBinding.txtCurrentCity.setVisibility(View.GONE);
                activityProfilePageBinding.extSearchCity.setText(lastCity);
            }
        });
        activityProfilePageBinding.extSearchCity.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            lastCity = v.getText().toString();
                            activityProfilePageBinding.txtCurrentCity.setVisibility(View.VISIBLE);
                            activityProfilePageBinding.txtCurrentCity.setText(lastCity);
                            viewModel.searchVolumes(lastCity);
                            activityProfilePageBinding.extSearchCity.setVisibility(View.GONE);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }

    private void getFirebaseProfileData() {

        // calling add value event listener method
        // for getting the values from database.
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.

                // after getting the value we are setting
                // our value to our text view in below line.
                UserData post = snapshot.getValue(UserData.class);
                Log.d("TAG", "onDataChange: " + post);
                Glide.with(ProfilePageActivity.this)
                        .asBitmap()
                        .load(post.profilePic)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                activityProfilePageBinding.profileImage.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                activityProfilePageBinding.txtUserName.setText(post.userName);
                activityProfilePageBinding.txtBio.setText(post.bio);
//                Glide.with(ProfilePageActivity.this).load(post.profilePic).into(activityProfilePageBinding.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(ProfilePageActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewModel(String city) {
        viewModel = ViewModelProviders.of(this).get(BookSearchViewModel.class);
        viewModel.init();
        viewModel.searchVolumes(city);
        viewModel.getVolumesResponseLiveData().observe(this, new Observer<FiveDayResponse>() {
            @Override
            public void onChanged(FiveDayResponse volumesResponse) {
                Log.d("TAG", "onChanged: " + volumesResponse);
                if (volumesResponse != null) {
                    if (volumesResponse.list.size() != 0) {

                        String lastDate = "";
                        for (int i = 0; i < volumesResponse.list.size(); i++) {
                            WeatherListData listData = volumesResponse.list.get(i);


                            String dayString = listData.dt_txt;
                            Log.d("TAG", "onChanged: " + "  " + lastDate + "  " + lastDate.length());
                            if (lastDate.length() == 0) {
                                lastDate = dayString;
                                weatherListData.add(listData);
                            } else {
                                String pattern = "dd-MM-yyyy HH:mm:ss";
                                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                                try {
                                    Date one = dateFormat.parse(lastDate);
                                    Date two = dateFormat.parse(dayString);
                                    SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
//                                    outFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    outFormat.setTimeZone(TimeZone.getDefault());
                                    String goal = outFormat.format(two);
                                    Log.d("TAG", "daysname: " + goal + "  " + dayString);
                                    if (one.before(two)) {
                                        Log.d("TAG", "onChanged:before ");
                                        // If start date is before end date.
                                    } else if (one.equals(two)) {
                                        Log.d("TAG", "onChanged:equal ");
                                        // If two dates are equal.
                                    } else {
                                        Log.d("TAG", "onChanged:after ");
                                        // If start date is after the end date.
                                    }
                                    lastDate = dayString;
                                    if (!lastDate.equalsIgnoreCase(dayString)) {
                                        weatherListData.add(listData);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                        Log.d("TAG", "onChanged: " + weatherListData.size());
                        weatherDataAdapter.addAllData(weatherListData);
                    }
                }
            }
        });

    }
}
