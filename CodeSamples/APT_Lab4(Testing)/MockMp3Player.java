import java.util.ArrayList;
 
 
public class MockMp3Player implements Mp3Player{
 
    private ArrayList songsInPlaylist;
    private int curSongPtr = 0; //points to the current song being played
    private double curSongNumSeconds = 0.0; //Seconds advanced in the current song
    private boolean isPlaying = false;
 
    public MockMp3Player(){
        this.songsInPlaylist = new ArrayList<String>();
    }
 
    public void play() {
        if(this.songsInPlaylist.size() > 0){
            this.isPlaying = true;
            curSongNumSeconds += 1.0;
        }
        else{
        	this.isPlaying = false;
        	curSongNumSeconds = 0.0;
        }
    }

    public void pause() {
        if(this.songsInPlaylist.size() > 0){
            this.isPlaying = false;
        }
    }

    public void stop() {
        if(this.songsInPlaylist.size() > 0){
            this.isPlaying = false;
            this.curSongNumSeconds = 0.0;
        }
    }
 
    @Override
    public double currentPosition() {
        return this.curSongNumSeconds;
    }
 
 
    @Override
    public String currentSong() {
        return (String)(this.songsInPlaylist.get(curSongPtr));
    }
 
    public void next() {
 
        if(this.curSongPtr < this.songsInPlaylist.size() - 1) 
        {
          this.curSongPtr++;
        }
        
        this.curSongNumSeconds = 0;
 
    }

    public void prev() {
        if(this.curSongPtr > 0) 
        this.curSongPtr--;
        this.curSongNumSeconds = 0;
    }
 
    public boolean isPlaying() {
        return this.isPlaying;
    }
 
    public void loadSongs(ArrayList listOfSongs) {
    	this.songsInPlaylist = listOfSongs;
    }
 
}
