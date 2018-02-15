package com.example.nova.musicplayer;

import java.util.List;

public class Song {

    //Description
    private String title;
    private String filePath;

    //Media
    private String artist;
    private long id;
    private List<String> artistList;
    private String albumArtist;
    private String album;
    private int year;
    private List<String> genreList;
    private int lengthInSeconds;

    //must add in the other attributes later
    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        //this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getArtistList() {
        return artistList;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public int getYear() {
        return year;
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public int getLengthInSeconds() {
        return lengthInSeconds;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtistList(List<String> artistList) {
        this.artistList = artistList;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenreList(List<String> genreList) {
        this.genreList = genreList;
    }

    public void setLengthInSeconds(int lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    public String getArtist() {
        return artist;
    }

    public long getId() {
        return id;
    }
}



