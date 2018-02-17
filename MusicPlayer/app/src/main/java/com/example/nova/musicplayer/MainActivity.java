package com.example.nova.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.nova.musicplayer.MusicService.MusicBinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */




public class MainActivity extends Activity implements MediaPlayerControl {

	private static final int REQUEST_WRITE_PERMISSION = 786;
	private static final String DEBUG_TAG = "DEBUG";

	//song list variables
	private ListView songView;
	private SongCollections songCollections;
	private MusicPlayerSettings musicPlayerSettings;
	private ImportSongSystem importSongSystem;

	//service
	private MusicService musicSrv;
	private Intent playIntent;
	//binding
	private boolean musicBound=false;

	//controller
	private MusicController controller;

	//activity and playback pause flags
	private boolean paused=false, playbackPaused=false;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		songView = (ListView)findViewById(R.id.song_list);
		checkPermissions();

	}



	public void initMusicPlayer() {

		songCollections = new SongCollections();

		musicPlayerSettings = new MusicPlayerSettings();

		importSongSystem = new ImportSongSystem();

		//get songs from device
//		scanSongFolder();
//		//getSongFolderList();
//		//sort alphabetically by title
//		Collections.sort(songCollections.getAllCollection().getSongList(), new Comparator<Song>(){
//			public int compare(Song a, Song b){
//				return a.getTitle().compareTo(b.getTitle());
//			}
//		});

		//scan for songs to import
		importSongSystem.scanEntireStorageForSongs(this);
		//songCollections.addSongListToAllCollection(importSongSystem.getSongList());

		scanSongFolder();


		//create and set adapter
		SongAdapter songAdt = new SongAdapter(this, songCollections.getAllCollection().getSongList());
		songView.setAdapter(songAdt);

		//setup controller
		setController();
	}





	public void scanSongFolder() {
		String selection = MediaStore.Audio.Media.IS_MUSIC + " !=" + 0
				+ " AND " + MediaStore.Audio.Media.DATA + " LIKE '" + importSongSystem.getSongFolderPath() + "/%'";

		//query external audio
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

		//only get songs from specific song folder
		Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);




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

			//add songs to list
			do {
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
				songCollections.getAllCollection().getSongList().add(new Song(thisId, thisTitle, thisArtist, songPath, albumId, trackId, duration, albumArt));
			}
			while (musicCursor.moveToNext());
		}

		if(musicCursor != null) {
			musicCursor.close();
		}
	}










//	//    //method to retrieve song info from device
//	public void getAllSongsExceptSongFolder() {
//		//query external audio
//		ContentResolver musicResolver = getContentResolver();
//		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//		Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Images.Media.DATA + " not like ? ", new String[] { "%" + importSongSystem.getSongFolderPath() + "%" } , null);
//		//iterate over results if valid
//		if(musicCursor!=null && musicCursor.moveToFirst()){
//			//get columns
//			int titleColumn = musicCursor.getColumnIndex
//					(android.provider.MediaStore.Audio.Media.TITLE);
//			int idColumn = musicCursor.getColumnIndex
//					(android.provider.MediaStore.Audio.Media._ID);
//			int artistColumn = musicCursor.getColumnIndex
//					(android.provider.MediaStore.Audio.Media.ARTIST);
//			//add songs to list
//			do {
//				long thisId = musicCursor.getLong(idColumn);
//				String thisTitle = musicCursor.getString(titleColumn);
//				String thisArtist = musicCursor.getString(artistColumn);
//
//				songCollections.getAllCollection().addSong(new Song(thisId, thisTitle, thisArtist));
//			}
//			while (musicCursor.moveToNext());
//		}
//	}















	@RequiresApi(api = Build.VERSION_CODES.M)
	private void checkPermissions() {
		if (ContextCompat.checkSelfPermission(getApplicationContext(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
		}
        else {
			initMusicPlayer();
        }
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			initMusicPlayer();
		}
		else {
			System.exit(1);
		}
	}





	//connect to the service
	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder)service;
			//get service
			musicSrv = binder.getService();
			//pass list
			musicSrv.setList(songCollections.getAllCollection().getSongList());
			musicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicBound = false;
		}
	};

	//start and bind the service when the activity starts
	@Override
	protected void onStart() {
		super.onStart();
		if(playIntent==null){
			playIntent = new Intent(this, MusicService.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			startService(playIntent);
		}
	}

	//user song select
	public void songPicked(View view){
		musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
		musicSrv.playSong();
		if(playbackPaused){
			setController();
			playbackPaused=false;
		}
		controller.show(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//menu item selected
		switch (item.getItemId()) {
		case R.id.action_shuffle:
			musicSrv.setShuffle();
			break;
		case R.id.action_end:
			stopService(playIntent);
			musicSrv=null;
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}







	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if(musicSrv!=null && musicBound && musicSrv.isPng())
			return musicSrv.getPosn();
		else return 0;
	}

	@Override
	public int getDuration() {
		if(musicSrv!=null && musicBound && musicSrv.isPng())
			return musicSrv.getDur();
		else return 0;
	}

	@Override
	public boolean isPlaying() {
		if(musicSrv!=null && musicBound)
			return musicSrv.isPng();
		return false;
	}

	@Override
	public void pause() {
		playbackPaused=true;
		musicSrv.pausePlayer();
	}

	@Override
	public void seekTo(int pos) {
		musicSrv.seek(pos);
	}

	@Override
	public void start() {
		musicSrv.go();
	}

	//set the controller up
	private void setController(){
		controller = new MusicController(this);
		//set previous and next button listeners
		controller.setPrevNextListeners(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playNext();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playPrev();
			}
		});
		//set and show
		controller.setMediaPlayer(this);
		controller.setAnchorView(findViewById(R.id.song_list));
		controller.setEnabled(true);
	}

	private void playNext(){
		musicSrv.playNext();
		if(playbackPaused){ 
			setController();
			playbackPaused=false;
		}
		controller.show(0);
	}

	private void playPrev(){
		musicSrv.playPrev();
		if(playbackPaused){
			setController();
			playbackPaused=false;
		}
		controller.show(0);
	}

	@Override
	protected void onPause(){
		super.onPause();
		paused=true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(paused){
			setController();
			paused=false;
		}
	}

	@Override
	protected void onStop() {
		controller.hide();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		musicSrv=null;
		super.onDestroy();
	}

}
