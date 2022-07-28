package main;

import java.text.DecimalFormat;

public class SettingsAndUtils {
    /**
     * 最多展示多少个解.
     */
    public static final int MAX_SHOW_NUM = 50;

    /**
     * 最多g+b的值.
     */
    public static final int MAX_GB = 20;

    public static final int MAX_COMBO = 2000;

    /**
     * 将combo转为对应的贡献份数.
     */
    public static int c2s(int combo) {
        return combo * (combo - 1) / 2;
    }

    /**
     * 将贡献份数转为不超过该份数的最大combo数.
     * <p>
     * 利用二分法寻找位置，若combo对应的份数小于等于传入的份数，且combo+1对应的份数大于传入的份数，则返回combo。
     *
     * @param shares 预定贡献份数
     * @return 不超过该份数的最大combo数
     */
    public static int s2c(int shares) {
        if (shares < 0 || shares > c2s(MAX_COMBO)) {
            throw new IllegalArgumentException("shares越界：" + shares);
        }
        int start = 1;
        int end = MAX_COMBO;
        int mid = (start + end) / 2;
        while (start < end) {
            if (shares < c2s(mid)) {
                end = mid - 1;
            } else if (shares >= c2s(mid + 1)) {
                start = mid + 1;
            } else {
                return mid;
            }
            mid = (start + end) / 2;
        }
        return mid;
    }

    public static final int THREAD_NUM = Runtime.getRuntime().availableProcessors();

    public static DecimalFormat df = new DecimalFormat("#00.00%");
    public static DecimalFormat df2 = new DecimalFormat("0.000");
}
