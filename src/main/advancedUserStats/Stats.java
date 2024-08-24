package main.advancedUserStats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class Stats {
    private NowPlaying nowPlaying = new NowPlaying();
    private int remainedTime;
    private int startingTime;
    private String repeat;
    private boolean shuffle = false;
    private boolean paused;
    private ArrayList<Integer> shuffledOrder = new ArrayList<>();


    public int ForIndGetInd(int indice) {
        int i = 0;
        while (shuffledOrder.get(i) != indice) {
            i++;
        }
        return i;
    }


}
