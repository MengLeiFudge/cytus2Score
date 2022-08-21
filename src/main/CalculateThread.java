package main;

import java.util.ArrayList;
import java.util.Collections;

import static main.Cytus2Calculator.progress;
import static main.Cytus2Calculator.solutions;
import static main.SettingsAndUtils.THREAD_NUM;
import static main.SettingsAndUtils.c2s;
import static main.SettingsAndUtils.s2c;

public class CalculateThread extends Thread {
    private final int threadID;
    private final int note;
    private final ArrayList<CalculateParam> paramList;

    CalculateThread(int id, int note, ArrayList<CalculateParam> paramList) {
        this.threadID = id;
        this.note = note;
        this.paramList = paramList;
    }

    @Override
    public void run() {
        for (int i = 0; i < paramList.size(); i++) {
            if (i % THREAD_NUM != threadID) {
                continue;
            }
            CalculateParam param = paramList.get(i);
            addAllSolutions(param.p, param.gb, param.minShares, param.maxShares);
            synchronized (CalculateThread.class) {
                progress++;
            }
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
        // 临时存放解
        ArrayList<Solution> s = new ArrayList<>();

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

        // combo 分为四份、五份的情况不去计算，耗费时间多还不好打

        // 将解全部加入总解列表
        if (!s.isEmpty()) {
            Collections.sort(s);
            for (Solution solution : s) {
                synchronized (CalculateThread.class) {
                    if (!solutions.contains(solution)) {
                        solutions.add(solution);
                    }
                }
            }
        }
    }
}
