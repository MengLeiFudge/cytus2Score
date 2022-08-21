package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static main.SettingsAndUtils.MAX_GB;
import static main.SettingsAndUtils.MAX_SHOW_NUM;
import static main.SettingsAndUtils.THREAD_NUM;
import static main.SettingsAndUtils.c2s;
import static main.SettingsAndUtils.df;
import static main.SettingsAndUtils.df2;

public class Cytus2Calculator {
    private final int note;
    private final int targetScore;
    private final double noteScoreMin;
    private final double noteScoreMax;
    /**
     * 每一份对应的分数.
     */
    private final double perShareScore;
    static ArrayList<Solution> solutions;
    static int progress;

    Cytus2Calculator(int note, int targetScore) {
        this.note = note;
        this.targetScore = targetScore;
        this.noteScoreMin = Math.max(targetScore - 100000, 0);
        this.noteScoreMax = targetScore + 1.0;
        this.perShareScore = 100000.0 / c2s(note);
        Cytus2Calculator.solutions = new ArrayList<>();
        Cytus2Calculator.progress = 0;
    }

    public void process() {
        System.out.println("初步筛选可能的解...");
        ArrayList<CalculateParam> paramList = new ArrayList<>();
        // 根据指定的p、gb的值，确定所需份数
        // p: shiny perfect + perfect
        for (int p = 0; p <= note; p++) {
            // gb: great + bad
            for (int gb = 0; gb <= note - p; gb++) {
                // 去掉极难打出来的解
                if (gb > MAX_GB) {
                    break;
                }
                // 计算总按键分
                double noteScore = (p * 1.0 + gb * 0.3) * 900000 / note;
                if (noteScore < noteScoreMin || noteScore >= noteScoreMax) {
                    continue;
                }
                // [minComboScore, minComboScore + 1) 这个范围有0个或多个份数符合条件
                // 计算最低连击分
                double minComboScore = targetScore - noteScore;
                int minShares = (int) (minComboScore / perShareScore);
                if (noteScore + minShares * perShareScore < targetScore) {
                    minShares++;
                }
                int maxShares = minShares;
                while (noteScore + maxShares * perShareScore < targetScore + 1) {
                    maxShares++;
                }
                maxShares--;
                if (minShares > maxShares) {
                    // 没有份数符合条件
                    continue;
                }
                // 满足基本条件，现有p、gb的值可能拼出该范围的份数
                paramList.add(new CalculateParam(p, gb, minShares, maxShares));
            }
        }

        System.out.println("筛选完毕，共有 " + paramList.size() + " 种情况可能出现目标解，开始计算！");
        final long beginTime = System.nanoTime();

        // 每秒输出当前计算进度，直至计算完毕
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), new NamedThreadFactory("print"), new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(() -> {
            int lastPrintSeconds = 0;
            while (progress < paramList.size()) {
                double seconds = (System.nanoTime() - beginTime) / 1000000000.0;
                if (seconds <= lastPrintSeconds + 1) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    continue;
                }
                System.out.println("计算进度：" +
                        df.format((double) progress / paramList.size()) +
                        " (" + progress + "/" + paramList.size() + ")，用时 " + df2.format(seconds) + " 秒");
                lastPrintSeconds = (int) seconds;
            }
        });
        singleThreadPool.shutdown();

        // 开启计算线程
        CalculateThread[] threads = new CalculateThread[THREAD_NUM];
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i] = new CalculateThread(i, note, paramList);
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

        double seconds = (System.nanoTime() - beginTime) / 1000000000.0;
        System.out.println("计算完毕，用时 " + df2.format(seconds) + " 秒");

        // 输出最终结果
        if (solutions.isEmpty()) {
            System.out.println("按键数目 " + note + "，目标分 " + targetScore + " 无解！");
            return;
        }
        System.out.println("共找到 " + solutions.size() + " 组解！");
        Collections.sort(solutions);
        for (int i = 0; i < Math.min(solutions.size(), MAX_SHOW_NUM); i++) {
            System.out.println(solutions.get(i).toString());
        }
    }
}
