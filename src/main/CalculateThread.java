package main;

import java.util.ArrayList;
import java.util.Collections;

import static main.Cytus2Calculator.THREAD_NUM;
import static main.Cytus2Calculator.progress;
import static main.Cytus2Calculator.solutions;
import static main.Main.c2s;
import static main.Main.s2c;

public class CalculateThread extends Thread {
    private final int threadID;// 线程序号，用于判断某个p值是不是本线程处理
    private final int note;// 歌曲按键总数
    private final int targetScore;// 目标分数
    private final double noteScoreMin;// 最低note分
    private final double noteScoreMax;// 最高note分
    private final double perShareScore;// 每一份对应的分数

    static final int MAX_GB = 20;// 最多g+b的值

    CalculateThread(int id, int note, int targetScore,
                    double noteScoreMin, double noteScoreMax, double perShareScore) {
        this.threadID = id;
        this.note = note;
        this.targetScore = targetScore;
        this.noteScoreMin = noteScoreMin;
        this.noteScoreMax = noteScoreMax;
        this.perShareScore = perShareScore;
    }

    @Override
    public void run() {
        // 根据指定的p、gb的值，确定所需份数
        for (int p = 0; p <= note; p++) {// p: shiny perfect + perfect
            if (p % THREAD_NUM != threadID) {// 计算由序号为 p % THREAD_NUM 的线程完成
                continue;
            }
            // long s1 = System.nanoTime();
            for (int gb = 0; gb <= note - p; gb++) {// gb: great + bad
                if (gb > MAX_GB) {// 去掉极难打出来的解
                    break;
                }
                double noteScore = (p * 1.0 + gb * 0.3) * 900000 / note;// 按键分
                if (noteScore < noteScoreMin || noteScore >= noteScoreMax) {
                    continue;
                }
                // [minComboScore, minComboScore + 1) 这个范围有0个或多个份数符合条件
                double minComboScore = targetScore - noteScore;// 最低连击分
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
                addAllSolutions(p, gb, minShares, maxShares);
            }
            synchronized (CalculateThread.class) {
                progress++;
            }
            // long e1 = System.nanoTime();
            // System.out.println("p" + p + " 计算完毕，用时 " + nanoSecondToStr(s1, e1));
        }
    }

    /**
     * 对于给定的p、gb、连击份数，计算所有可能的解.
     *
     * @param p         perfect数目
     * @param gb        great+bad数目
     * @param minShares 最低连击份数和
     * @param maxShares 最高连击份数和
     */
    private void addAllSolutions(int p, int gb, int minShares, int maxShares) {
        ArrayList<Solution> s = new ArrayList<>();// 临时存放解

        // 单份 combo
        int c0min;
        int c0max;
        // combo 只有一份情况下，判断是否有解
        c0min = s2c(minShares);
        c0max = s2c(maxShares);
        if (c0min == c0max) {
            // 如果指向同一个 combo，必有 c2s(combo) <= minShares
            if (c0min >= p && c0min <= p + gb && c2s(c0min) == minShares) {
                s.add(new Solution(p, gb, c0min));
            }
        } else {
            // 如果指向不同的 combo，必有 c2s(c0min) <= minShares < c2s(c0max) <= maxShares
            if (c0min >= p && c0min <= p + gb && c2s(c0min) == minShares) {
                s.add(new Solution(p, gb, c0min));
            }
            if (c0max >= p && c0max <= p + gb && c2s(c0max) == maxShares) {
                s.add(new Solution(p, gb, c0max));
            }
        }

        // 多份 combo
        int minSharesLeft;
        int maxSharesLeft;
        int comboSum;

        // combo 分为两份情况下，判断是否有解
        for (int c1 = 1; c1 < note; c1++) {
            minSharesLeft = minShares - c2s(c1);
            maxSharesLeft = maxShares - c2s(c1);
            if (minSharesLeft < 0 || maxSharesLeft < 0) {
                break;
            }
            c0min = s2c(minSharesLeft);
            c0max = s2c(maxSharesLeft);
            comboSum = c0max + c1;
            if (c0min == c0max) {
                // 如果指向同一个 combo，必有 c2s(combo) <= minShares
                if (comboSum >= p && comboSum <= p + gb && c2s(c0min) == minSharesLeft) {
                    s.add(new Solution(p, gb, c0min, c1));
                }
            } else {
                // 如果指向不同的 combo，必有 c2s(c0min) <= minShares < c2s(c0max) <= maxShares
                if (comboSum >= p && comboSum <= p + gb && c2s(c0min) == minSharesLeft) {
                    s.add(new Solution(p, gb, c0min, c1));
                }
                if (comboSum >= p && comboSum <= p + gb && c2s(c0max) == maxSharesLeft) {
                    s.add(new Solution(p, gb, c0max, c1));
                }
            }
        }

        // combo 分为三份情况下，判断是否有解
        for (int c1 = 1; c1 < note; c1++) {
            for (int c2 = 1; c2 < note - c1; c2++) {
                minSharesLeft = minShares - c2s(c1) - c2s(c2);
                maxSharesLeft = maxShares - c2s(c1) - c2s(c2);
                if (minSharesLeft < 0 || maxSharesLeft < 0) {
                    break;
                }
                c0min = s2c(minSharesLeft);
                c0max = s2c(maxSharesLeft);
                comboSum = c0max + c1 + c2;
                if (c0min == c0max) {
                    // 如果指向同一个 combo，必有 c2s(combo) <= minShares
                    if (comboSum >= p && comboSum <= p + gb && c2s(c0min) == minSharesLeft) {
                        s.add(new Solution(p, gb, c0min, c1, c2));
                    }
                } else {
                    // 如果指向不同的 combo，必有 c2s(c0min) <= minShares < c2s(c0max) <= maxShares
                    if (comboSum >= p && comboSum <= p + gb && c2s(c0min) == minSharesLeft) {
                        s.add(new Solution(p, gb, c0min, c1, c2));
                    }
                    if (comboSum >= p && comboSum <= p + gb && c2s(c0max) == maxSharesLeft) {
                        s.add(new Solution(p, gb, c0max, c1, c2));
                    }
                }
            }
        }

        if (!s.isEmpty()) {
            Collections.sort(s);
            for (int i = 0; i < Math.min(s.size(), 10); i++) {
                // 将最高的10个解加入总解列表
                Solution solution = s.get(i);
                synchronized (CalculateThread.class) {
                    if (!solutions.contains(solution)) {
                        solutions.add(solution);
                    }
                }
            }
        }
    }
}
