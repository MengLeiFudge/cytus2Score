package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import static main.Main.*;

public class Cytus2Calculator {
    private final int note;// 歌曲按键总数
    private final int targetScore;// 目标分数
    private final double noteScoreMin;// 最低note分
    private final double noteScoreMax;// 最高note分
    private final double perShareScore;// 每一份对应的分数
    static ArrayList<Solution> solutions;// 所有可能的解，每得到一个解，就将其填入该列表
    static int progress;// 某个perfect值计算完成后，该值+1，每个歌需计算 note+1 次
    private int second;// 记录计算用时

    static final int THREAD_NUM = 16;// 多线程计算时，所使用的线程数目
    static final int MAX_SHOW_NUM = 10;// 最多展示多少个解

    Cytus2Calculator(int note, int targetScore) {
        this.note = note;
        this.targetScore = targetScore;
        this.noteScoreMin = Math.max(targetScore - 100000, 0);
        this.noteScoreMax = targetScore + 1.0;
        this.perShareScore = 100000.0 / c2s(note);
        Cytus2Calculator.solutions = new ArrayList<>();
        Cytus2Calculator.progress = 0;
        this.second = 0;
    }

    DecimalFormat df = new DecimalFormat("#00.00%");

    public void process() {
        System.out.println("开始计算！");
        long beginTime = System.nanoTime();

        // 每秒输出当前计算进度，直至计算完毕
        Thread printProgress = new Thread(() -> {
            try {// 先休眠1s
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            while (progress < note + 1) {
                second++;
                System.out.println("计算进度：" +
                        df.format(progress / (note + 1.0)) +
                        " (" + progress + "/" + (note + 1) + ")，用时" + second + "秒");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        printProgress.start();

        // 计算线程，每个线程处理不同的p
        CalculateThread[] threads = new CalculateThread[THREAD_NUM];
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i] = new CalculateThread(i, note, targetScore,
                    noteScoreMin, noteScoreMax, perShareScore);
            threads[i].start();
        }

        // 等待所有线程计算完毕
        try {
            for (int i = 0; i < THREAD_NUM; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        System.out.println("计算完毕，用时 " + nanoSecondToStr(beginTime, endTime));

        // 输出最终结果
        if (solutions.isEmpty()) {
            System.out.println("按键数目 " + note + "，目标分 " + targetScore + " 无解！");
            return;
        }
        Collections.sort(solutions);
        for (int i = 0; i < Math.min(solutions.size(), MAX_SHOW_NUM); i++) {
            System.out.println(solutions.get(i).toString());
        }
    }

}
