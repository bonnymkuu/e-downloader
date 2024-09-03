package com.example.myvideo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class DownloadsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView1,recyclerView2;
    FloatingActionButton floatingActionButton;
    private VideosAdapter videosAdapter;
    private List<VideoItem> videoItemList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        recyclerView1 = findViewById(R.id.downloading_recyclerView);
        recyclerView2 = findViewById(R.id.downloaded_recyclerView);
        floatingActionButton = findViewById(R.id.add);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
        bottomNavigationView.setSelectedItemId(R.id.download);
        floatingActionButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(DownloadsActivity.this,PasteLinkActivity.class);
            startActivity(intent);
            return true;
        });
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        videoItemList = new ArrayList<>();
        videosAdapter = new VideosAdapter(this, videoItemList);
        recyclerView1.setAdapter(videosAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("youtubeLinks");
        fetchYoutubeLinks();
    }
    private void fetchYoutubeLinks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String youtubeLink = snapshot.getValue(String.class);
                    fetchVideoDetails(youtubeLink);
                }
                if (videoItemList.isEmpty()){
                    recyclerView1.setVisibility(View.GONE);
                    Toast.makeText(DownloadsActivity.this, "Please wait for uploaded video", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DownloadsActivity.this, "Failed to load YouTube links", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchVideoDetails(String youtubeLink) {
        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> files, VideoMeta videoMeta) {
                if (videoMeta != null) {
                    String title = videoMeta.getTitle();
                    String channelName = videoMeta.getAuthor();
                    String thumbnailUrl = videoMeta.getMaxResImageUrl().replace("http", "https");
                    videoItemList.add(new VideoItem(title, channelName, thumbnailUrl, youtubeLink));
                    videosAdapter.notifyItemChanged(videoItemList.size());
                } else {
                    Toast.makeText(DownloadsActivity.this, "Failed to extract video details", Toast.LENGTH_SHORT).show();
                }
            }
        }.extract(youtubeLink, false, false);
    }
    private final BottomNavigationView.OnItemSelectedListener selectedListener = menuItem -> {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.download) {
            Log.d("", "Welcome: ...");
            return true;
        }else if (itemId == R.id.search) {
            Intent i = new Intent(DownloadsActivity.this, SearchActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    };
}