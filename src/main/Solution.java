package main;

import java.util.Arrays;

public class Solution implements Comparable<Solution> {
    private final int p;
    private final int g;
    private final int b;
    private final Integer[] comboDiv;

    Solution(int p, int gb, Integer... comboDiv) {
        this.p = p;
        this.comboDiv = comboDiv;
        Arrays.sort(comboDiv, (i1, i2) -> Integer.compare(i2, i1));// 各段combo由多到少排列
        int leftP = p;
        int leftGB = gb;
        int g0 = 0;
        for (int i : comboDiv) {
            int p0 = Math.min(i, leftP);
            if (p0 > 0) {
                leftP -= p0;
                i -= p0;
            }
            g0 += i;
            leftGB -= i;
        }
        this.g = g0;
        this.b = leftGB;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Solution) {
            Solution s = (Solution) obj;
            if (p != s.p || g != s.g || b != s.b || comboDiv.length != s.comboDiv.length) {
                return false;
            }
            for (int i = 0; i < comboDiv.length; i++) {
                if (!comboDiv[i].equals(s.comboDiv[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Solution s) {
        if (p + g + b != s.p + s.g + s.b) {
            return Integer.compare(p + g + b, s.p + s.g + s.b);// 总按键数少的靠前
        }
        if (g != s.g) {
            return Integer.compare(g, s.g);// 总键数相同时，g少的靠前
        }
        if (b != s.b) {
            return Integer.compare(b, s.b);// g相同时，b少的靠前
        }
        return Integer.compare(p, s.p);// g、b相同时，p少的靠前
    }

    /**
     * 输出具体的操作步骤.
     * 当然，并不一定非要按照这种打法，这里仅提供一种。
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        int leftG = g;
        for (int i : comboDiv) {
            ret.append("(");
            int g0 = Math.min(i, leftG);
            if (g0 > 0) {
                leftG -= g0;
                i -= g0;
            }
            int p0 = i;
            if (p0 > 0) {
                ret.append(p0).append(" Perfect");
            }
            if (g0 > 0) {
                if (p0 > 0) {
                    ret.append(", ");
                }
                ret.append(g0).append(" Great");
            }
            ret.append(") ");
        }
        if (b > 0) {
            ret.append("+ ").append(b).append(" Great/Bad");
        }
        return ret.toString();
    }
}
