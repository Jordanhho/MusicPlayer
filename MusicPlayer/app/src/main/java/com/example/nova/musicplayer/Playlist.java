package com.example.nova.musicplayer;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String name;
    //note must add in other filtering attributes such as genres, type of album

    private List<Song> songList;

    public Playlist(String name) {
        this.name = name;
        this.songList = new ArrayList<>();
    }

    public Playlist(String name, List<Song> songList) {
        this.name = name;
        this.songList = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //returns list of songs
    public List<Song> getSongList() {
        return songList;
    }

    //searches based on titles
    public void addSong(Song song) {
        songList.add(song);
    }

    public void removeSong(Song song) {
        for(Song s: songList) {
            if(s.getTitle().equals(song.getTitle())) {
                songList.remove(s);
                break;
            }
        }
    }

    //deletes all songs
    public void deleteAllSongs() {
        songList.clear();
    }


    //get song list size
    public int getSongListSize() {
        return songList.size();
    }
}
