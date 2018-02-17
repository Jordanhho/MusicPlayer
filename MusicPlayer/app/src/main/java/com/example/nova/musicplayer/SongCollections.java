package com.example.nova.musicplayer;


import java.util.ArrayList;
import java.util.List;

public class SongCollections {

    private final String allCollectionName = "All Songs";
    private final String defaultCollectionName = "Default";
    private final String favouriteCollectionName = "Favourite";
    private final String dislikedCollectionName = "Disliked";

    private Playlist allCollection;
    private Playlist defaultCollection;
    private Playlist favouriteCollection;
    private Playlist dislikedCollection;
    private List<Playlist> playLists;

    public SongCollections() {
        List<Song> emptySongList = new ArrayList<>();

        //TODO must implement shallow copy where all songs are inherited from the all songs playlist
        //TODO: Ie: all songs must be added to the all songs collection, anything deleted from favourites, default collection will not be deleted from the all collection
        //TODO: songs that are added to the any of the collections that are not all collection is a shallow copy where if it is deleted in the inherited collections, nothing will happen, if it is deleted in all collection it will be deleted from all collections the song belongs to
        allCollection = new Playlist(allCollectionName, emptySongList);
        defaultCollection = new Playlist(defaultCollectionName, emptySongList);
        favouriteCollection = new Playlist(favouriteCollectionName, emptySongList);
        dislikedCollection = new Playlist(dislikedCollectionName, emptySongList);
        playLists = new ArrayList<>();
    }


    public String getAllCollectionName() {
        return allCollectionName;
    }

    public String getDefaultCollectionName() {
        return defaultCollectionName;
    }

    public String getFavouriteCollectionName() {
        return favouriteCollectionName;
    }

    public String getDislikedCollectionName() {
        return dislikedCollectionName;
    }

    public Playlist getAllCollection() {
        return allCollection;
    }

    public Playlist getDefaultCollection() {
        return defaultCollection;
    }

    public Playlist getFavouriteCollection() {
        return favouriteCollection;
    }

    public Playlist getDislikedCollection() {
        return dislikedCollection;
    }

    public List<Playlist> getPlayLists() {
        return playLists;
    }



    public void addSongListToAllCollection(List<Song> songList) {
        for(Song s: songList) {
            allCollection.getSongList().add(s);
        }
    }

    public void addSongToCollectionAllCollection(Song song) {
        allCollection.getSongList().add(song);
    }

    public void addNewPlaylist(String name, List<Song> songList) {
        playLists.add(new Playlist(name, songList));
    }

    public void removePlayList(String name) {
        for(Playlist p: playLists) {
            if(p.getName().equals(name)) {
                playLists.remove(p);
                break;
            }
        }
    }

    public int getCollectionSize() {
        return allCollection.getSongListSize();
    }

    public int getPlayListSize() {
        return playLists.size();
    }


    public boolean ifSongExists(String songTitle) {
        boolean found = false;
        for(Song s: allCollection.getSongList()) {
            if(s.getTitle().equals(songTitle)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
