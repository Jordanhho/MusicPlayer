package com.example.nova.musicplayer;

import android.graphics.Bitmap;

import java.util.List;

public class Song {

    //Description
    private String title;
    private String filePath;

    //Media
    private String artist;
    private long id;
    private int albumId;
    private int trackId;
    private long duration;
    private Bitmap albumArt;

    //other
    private List<String> artistList;
    private String albumArtist;
    private String album;
    private int year;
    private List<String> genreList;

    //must add in the other attributes later
    public Song(long id, String title, String artist, String filePath, int albumId, int trackId, long duration, Bitmap albumArt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.albumId = albumId;
        this.trackId = trackId;
        this.duration = duration;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public List<String> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<String> artistList) {
        this.artistList = artistList;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }
}



