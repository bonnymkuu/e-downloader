package com.example.myvideo;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.MessageFormat;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class SearchActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    EditText editText;
    TextView title,channelName,videoLength,viewCount;
    RecyclerView recyclerView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button download = findViewById(R.id.download);
        editText = findViewById(R.id.urlET);
        imageView = findViewById(R.id.thumbnail);
        title = findViewById(R.id.titleTV);
        channelName = findViewById(R.id.channelName);
        videoLength = findViewById(R.id.videoLength);
        viewCount = findViewById(R.id.viewCount);
        recyclerView = findViewById(R.id.recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
        bottomNavigationView.setSelectedItemId(R.id.search);
        String videoLink = getIntent().getStringExtra("videoUrl");
        editText.setText(videoLink);
        download.setOnClickListener(view -> showVideoToDownload());
    }

    @SuppressLint("StaticFieldLeak")
    private void showVideoToDownload() {
        new YouTubeExtractor(SearchActivity.this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> files, VideoMeta videoMeta) {
                if (files == null) {
                    Toast.makeText(SearchActivity.this, "There was an error while extracting video", Toast.LENGTH_SHORT).show();
                    return;
                }
                title.setText(MessageFormat.format("Title: {0}", videoMeta.getTitle()));
                channelName.setText(MessageFormat.format("Channel Name: {0}", videoMeta.getAuthor()));
                videoLength.setText(MessageFormat.format("Video Duration: {0}", getDuration(videoMeta.getVideoLength())));
                viewCount.setText(MessageFormat.format("Views Count: {0}", videoMeta.getViewCount()));
                Glide.with(getApplicationContext()).load(videoMeta.getMaxResImageUrl().replace("http","https")).fitCenter().into(imageView);

                SparseArray<YtFile> array = new SparseArray<>();

                for (int i = 0; i < files.size(); i++) {
                    YtFile ytFile = files.get(files.keyAt(i));

                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        array.put(files.keyAt(i), ytFile);
                    }
                }

                ResultAdapter adapter = new ResultAdapter(SearchActivity.this, array);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(ytFile -> {
                    String filename = videoMeta.getTitle() + "." + ytFile.getFormat().getExt();
                    filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
                    download(ytFile.getUrl(), videoMeta.getTitle(), filename);
                });
            }
        }.extract(Objects.requireNonNull(editText.getText()).toString(), false, false);

    }

    public String getDuration(long duration) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = (int) (duration % SECONDS_IN_A_MINUTE);
        int totalMinutes = (int) (duration / SECONDS_IN_A_MINUTE);
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return hours + ":" + minutes + ":" + seconds;
    }

    private void download(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
    }
    private final BottomNavigationView.OnItemSelectedListener selectedListener = menuItem -> {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.download) {
            Intent i = new Intent(SearchActivity.this, DownloadsActivity.class);
            startActivity(i);

            return true;
        }else if (itemId == R.id.search) {
            Log.d("", "Welcome: ...");
            return true;
        }
        return false;
    };
}
