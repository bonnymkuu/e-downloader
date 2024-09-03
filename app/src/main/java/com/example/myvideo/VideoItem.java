package com.example.myvideo;

public class VideoItem {
    private String title;
    private String channelName;
    private String thumbnailUrl;
    private String videoUrl;

    public VideoItem(String title, String channelName, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.channelName = channelName;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
