package com.example.teamproject_toto;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

// RandomList.java 파일 작성자 : 이아연
// 해당 파일은 소확행 기능을 위해 만들어진 것
// 같은 날의 모든 사용자들은 같은 소확행을 얻도록 함. 날마다 랜덤으로 소확행 일정이 바뀌는 시스템.
public class RandomList {
    // 소확행 리스트
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

    // 소확행 리스트에서 오늘의 소확행 난수 고정 랜덤 추출
    public String getRandomitem() {
        Date today = new Date(); // 오늘 날짜 가져오기
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);
        int seed = Integer.parseInt(ss); // 오늘 날짜를 seed값으로 고정 -> 같은 날의 모든 사용자는 같은 소확행 일정 소화

        Random rand = new Random();
        rand.setSeed(seed);

        String smallitem = randomList.get(rand.nextInt(randomList.size())); // randomList 크기 만큼 난수 범위 설정

        return smallitem;
    }

}
