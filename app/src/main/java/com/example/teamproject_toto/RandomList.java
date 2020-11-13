package com.example.teamproject_toto;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class RandomList {
    private final List<String> randomList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));

    public String getRandomitem() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);
        int seed = Integer.parseInt(ss);

        Random rand = new Random();
        rand.setSeed(seed);

        String smallitem = randomList.get(rand.nextInt(randomList.size()) + 1);

        return smallitem;
    }

}

