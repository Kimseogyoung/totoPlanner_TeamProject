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
    private final List<String> randomList = new ArrayList<>(Arrays.asList(
            "독서 30분하기",
            "시원한 바람 쐬며 산책하기"
            ,"친구 만나서 수다떨기"
    ,"낮잠 1시간 자기"
    ,"운동 1시간하기"
    ,"주변 사람의 좋은점 3가지 칭찬하기"
    ,"주변 사람에게 소소한 선물 사주기"
    ,"이웃에게 인사하기"
    ,"자기개발 1시간 투자하기"
    ,"맛있는 요리 하나 만들기"
    ,"평소보다 한시간 일찍 일어나기"
    ,"평소보다 한시간 일찍 자기"
    ,"오늘의 일기 쓰기"
    ));

    public String getRandomitem() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);
        int seed = Integer.parseInt(ss);

        Random rand = new Random();
        rand.setSeed(seed);

        String smallitem = randomList.get(rand.nextInt(randomList.size()));

        return smallitem;
    }

}
