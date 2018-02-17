package com.example.nova.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;

public class ImportSongSystem {

    private static final String DEBUG_TAG = "DEBUG";

    //can change later but for now constant
    private final String songFolderPath = Environment.getExternalStorageDirectory() + "/Songs";
    private final String songJunkFolderPath = Environment.getExternalStorageDirectory() + "/Songs/Junk";

    //valid extensions
    private String[] songExtensions = {".mp3", ".flac", ".aac", ".wma", ".alac", ".wav", ".pcm", ".aiff"};

    //seperate based on duration based on seconds
    private int songMinRangeDuration = 80; //1:20 min
    private int songMaxRangeDuration = 300; //5 min

    //import songs seperated into 3 lists of song file paths
    private List<Song> shortSongList;
    private List<Song> songList;
    private List<Song> longSongList;

    public ImportSongSystem() {
        shortSongList = new ArrayList<>();
        songList = new ArrayList<>();
        longSongList = new ArrayList<>();

        //create song folder
        createSongFolder();
        createSongJunkFolder();

        //scan for songs based on extensions and song duration
    }

    private void createSongFolder() {
        File dir = new File(songFolderPath);
        if(!dir.exists() || (dir.exists() && !dir.isDirectory())) {
            dir.mkdirs();
        }
    }

    private void createSongJunkFolder() {
        File dir = new File(songJunkFolderPath);
        if(!dir.exists() || (dir.exists() && !dir.isDirectory())) {
            dir.mkdirs();
        }
    }


    public void scanEntireStorageForSongs(final Context c) {
        //clear previous search results
        shortSongList.clear();
        songList.clear();
        longSongList.clear();
        setSongSearchResults(c);
    }


    //    //method to retrieve song info from device
    private void setSongSearchResults(final Context c) {
        //query external audio
        ContentResolver musicResolver = c.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //search for songs excluding song folder and junk folder
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Images.Media.DATA + " not like ? ", new String[] { "%" + songJunkFolderPath + "%" } , null);

        String path;

        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int songPathColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.DATA);
            int albumColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.ALBUM_ID);
            int trackColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.TRACK);
            int durationColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.DURATION);


            //add songs to list based on file extensions
            do {

                path = musicCursor.getString(1).toLowerCase();
                for (String ext : songExtensions) {
                    if (path.endsWith(ext)) {

                        long thisId = musicCursor.getLong(idColumn);
                        String thisTitle = musicCursor.getString(titleColumn);
                        String thisArtist = musicCursor.getString(artistColumn);
                        String songPath = musicCursor.getString(songPathColumn);

                        int albumId = musicCursor.getInt(albumColumn);
                        int trackId = musicCursor.getInt(trackColumn);
                        Long duration = musicCursor.getLong(durationColumn);

                        //get duration of song in seconds from milliseconds
                        Long durationInSeconds = duration/1000;


                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(songPath);

                        Bitmap albumArt = null;

                        //get music cover art
                        byte[] data = mmr.getEmbeddedPicture();
                        if (data != null) {
                            // Display method for cover art is through bitmap
                            albumArt = BitmapFactory.decodeByteArray(data, 0, data.length);
                        }

                        //filter songs according to duration
                        if(durationInSeconds < songMinRangeDuration) {
                            shortSongList.add(new Song(thisId, thisTitle, thisArtist, songPath, albumId, trackId, duration, albumArt));
                        }
                        else if( songMinRangeDuration <= durationInSeconds && durationInSeconds <= songMaxRangeDuration) {
                            songList.add(new Song(thisId, thisTitle, thisArtist, songPath, albumId, trackId, duration, albumArt));
                        }
                        else {
                            longSongList.add(new Song(thisId, thisTitle, thisArtist, songPath, albumId, trackId, duration, albumArt));
                        }

                        Log.d(DEBUG_TAG, "music title " + thisTitle);
                        Log.d(DEBUG_TAG, "music duration " + duration);
                        Log.d(DEBUG_TAG, "music path " + songPath);
                        break;


//                        //removes file extension from string
//                        String songTitle = file.getName();
//                        songTitle = songTitle.substring(0, songTitle.lastIndexOf('.'));
//                        //checks if file name matches one of the already added songs
//                        if (!songCollections.ifSongExists(songTitle)) {
//                            //adds song to collection
//
//
//                            //songCollections.addSongToCollection(new Song(songTitle, file.getAbsolutePath()));
//                            break;
//                        }

                    }

                }
            }
            while (musicCursor.moveToNext());
        }
        if(musicCursor != null) {
            musicCursor.close();
        }
    }

    public String getSongJunkFolderPath() {
        return songJunkFolderPath;
    }

    public int getSongMinRangeDuration() {
        return songMinRangeDuration;
    }

    public int getSongMaxRangeDuration() {
        return songMaxRangeDuration;
    }

    public List<Song> getShortSongList() {
        return shortSongList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Song> getLongSongList() {
        return longSongList;
    }


    //    private void scanSongFolder(String folderPath) {
//
//        File file = new File(folderPath);
//
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//
//            if (files != null && files.length > 0) {
//                for (File f: files) {
//                    if (f.isDirectory()) {
//                        scanForSongs(f.getAbsolutePath());
//                    }
//                    else {
//                        for(String ext: songExtensions) {
//                            if (f.getAbsolutePath().endsWith(ext)) {
//                                MediaMetadataRetriever songFile = new MediaMetadataRetriever();
//                                songFile.setDataSource(f.getAbsolutePath());
//
//                                //get duration of song in seconds
//                                int durationInSeconds = (int)((float) Integer.parseInt(songFile.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / (float) 1000);
//
//                                //filter songs according to duration
//                                if(durationInSeconds < songMinRangeDuration) {
//                                    shortSongList.add(f.getAbsolutePath());
//                                }
//                                else if( songMinRangeDuration <= durationInSeconds && durationInSeconds <= songMaxRangeDuration) {
//                                    songList.add(f.getAbsolutePath());
//                                }
//                                else {
//                                    longSongList.add(f.getAbsolutePath());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }





//
//    //method to retrieve song info from device
//    public void getSongFolderList(){
//
//        createSongFolder();
//
//        List<String> args = new ArrayList<String>();
//        String path = musicPlayerSettings.getSongFolderPath();
//
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=" + 0
//                + " AND " + MediaStore.Audio.Media.DATA + " LIKE '" + musicPlayerSettings.getSongFolderPath() + "/%'";
//
//
//        //query external audio
//        ContentResolver musicResolver = getContentResolver();
//        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
//        //iterate over results if valid
//
////			if(musicCursor!=null && musicCursor.moveToFirst()){
////				//get columns
////				int titleColumn = musicCursor.getColumnIndex
////						(android.provider.MediaStore.Audio.Media.TITLE);
////				int idColumn = musicCursor.getColumnIndex
////						(android.provider.MediaStore.Audio.Media._ID);
////				int artistColumn = musicCursor.getColumnIndex
////						(android.provider.MediaStore.Audio.Media.ARTIST);
////
////				//add songs to list
////				do {
////					long thisId = musicCursor.getLong(idColumn);
////					String thisTitle = musicCursor.getString(titleColumn);
////					String thisArtist = musicCursor.getString(artistColumn);
////					songCollections.getAllCollection().getSongList().add(new Song(thisId, thisTitle, thisArtist));
////				}
////				while (musicCursor.moveToNext());
////			}
//
//
//        while(musicCursor != null && musicCursor.moveToFirst()) {
//            for(String itr: musicPlayerSettings.getSongExtensions()) {
//                Log.d(DEBUG_TAG, "music path:: " + MediaStore.Audio.Medias);
//                if (android.provider.MediaStore.Audio.Media.DATA.endsWith(itr)) {
//                    //get columns
//                    int titleColumn = musicCursor.getColumnIndex
//                            (android.provider.MediaStore.Audio.Media.TITLE);
//                    int idColumn = musicCursor.getColumnIndex
//                            (android.provider.MediaStore.Audio.Media._ID);
//                    int artistColumn = musicCursor.getColumnIndex
//                            (android.provider.MediaStore.Audio.Media.ARTIST);
//
//                    //add songs to list
//                    long thisId = musicCursor.getLong(idColumn);
//                    String thisTitle = musicCursor.getString(titleColumn);
//                    String thisArtist = musicCursor.getString(artistColumn);
//                    songCollections.getAllCollection().getSongList().add(new Song(thisId, thisTitle, thisArtist));
//                    break;
//                }
//            }
//            musicCursor.moveToNext();
//        }
//
//
//        musicCursor.close();




//		while(musicCursor != null && musicCursor.moveToFirst()) {
//
//			for(String itr: musicPlayerSettings.getSongExtensions()) {
//				if (android.provider.MediaStore.Audio.Media.DATA.endsWith(itr)) {
//					//get columns
//					int titleColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media.TITLE);
//					int idColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media._ID);
//					int artistColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media.ARTIST);
//
//					//add songs to list
//					long thisId = musicCursor.getLong(idColumn);
//					String thisTitle = musicCursor.getString(titleColumn);
//					String thisArtist = musicCursor.getString(artistColumn);
//					songCollections.getAllCollection().getSongList().add(new Song(thisId, thisTitle, thisArtist));
//					break;
//				}
//			}
//			musicCursor.moveToNext();
//		}

//		if(musicCursor!=null && musicCursor.moveToFirst()){
//
//			for(String itr: musicPlayerSettings.getSongExtensions()) {
//				if (android.provider.MediaStore.Audio.Media.DATA.endsWith(itr)) {
//					//get columns
//					int titleColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media.TITLE);
//					int idColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media._ID);
//					int artistColumn = musicCursor.getColumnIndex
//							(android.provider.MediaStore.Audio.Media.ARTIST);
//
//					//add songs to list
//					do {
//						long thisId = musicCursor.getLong(idColumn);
//						String thisTitle = musicCursor.getString(titleColumn);
//						String thisArtist = musicCursor.getString(artistColumn);
//						songCollections.getAllCollection().getSongList().add(new Song(thisId, thisTitle, thisArtist));
//					}
//					while (musicCursor.moveToNext());
//				}
//			}
//		}











//		if (filter != null && filter.length() > 0) {
//			filter = "%" + filter + "%";
//			selection =
//					"(" + selection + " AND " +
//							"((TITLE LIKE ?) OR (ARTIST LIKE ?) OR (ALBUM LIKE ?)))";
//			args.add(filter);
//			args.add(filter);
//			args.add(filter);
//		}
//
//		String[] argsArray = args.toArray(new String[args.size()]);
//
//		getExternalAudioCursor(selection, argsArray);
//		getInternalAudioCursor(selection, argsArray);
//
//		Cursor c = new MergeCursor(new Cursor[] {
//				getExternalAudioCursor(selection, argsArray),
//				getInternalAudioCursor(selection, argsArray)});
//		startManagingCursor(c);
//		return c;









//    private void importFromSongFolder(String path) {
//
//        //loops through files inside the directory
//        File[] directory = new File(musicPlayerSettings.getSongFolderPath()).listFiles();
//
//        for (File file : directory) {
//            //checks if matches a valid sound file extension
//            for (String ext : musicPlayerSettings.getSongExtensions()) {
//                if (file.getName().toLowerCase().endsWith(ext)) {
//
//                    //removes file extension from string
//                    String songTitle = file.getName();
//                    songTitle = songTitle.substring(0, songTitle.lastIndexOf('.'));
//                    //checks if file name matches one of the already added songs
//                    if (!songCollections.ifSongExists(songTitle)) {
//                        //adds song to collection
//
//
//                        //songCollections.addSongToCollection(new Song(songTitle, file.getAbsolutePath()));
//                        break;
//                    }
//                }
//            }
//        }
//    }
//






    public void setSongMinRangeDuration(int songMinRangeDuration) {
        this.songMinRangeDuration = songMinRangeDuration;
    }

    public void setSongMaxRangeDuration(int songMaxRangeDuration) {
        this.songMaxRangeDuration = songMaxRangeDuration;
    }



    public String getSongFolderPath() {
        return songFolderPath;
    }

    public String[] getSongExtensions() {
        return songExtensions;
    }

}
