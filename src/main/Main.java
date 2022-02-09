package main;

import java.util.Scanner;

public class Main {
    /**
     * cy2音游美学计算器.
     * <p>
     * cy2 分数有两部分，即 90w 按键分 和 10w 连击分。
     * sp：1 note基础分，1 tp（俗称大p）
     * p：1 note基础分，0.7 tp（俗称小p）
     * g：0.3 note基础分，0.3 tp
     * b：0.3 note基础分，0 tp，会导致断连
     * m：0note基础分，0tp，会导致断连
     * 记一份连击分为d.
     * 第n个combo 对应的连击分份数为 n-1，即 (n-1)*d 分。
     * 长度为n的combo，其对应连击分份数为 n*(n-1)/2，即 (n*(n-1)/2)*d 分。
     * 记 s(n) = n*(n-1)/2，则 s = {0,1,3,6,10,15,21,...}
     * 由连击分共有10w，可得 d = 10w/(按键数*(按键数-1)/2).
     * <p>
     * 1.输入只有两个值，note数和目标分数。
     * cy2各种按键的分数与键型无关，可以抽象出来，仅以note数目进行后续计算。
     * 2.遍历p（即sp+p）、gb（即g+b），得到唯一note分，进而得到对应连击份数范围。
     * 3.以连击份数范围、p、gb的数目为基础，算出最简解。
     * 4.步骤2、3将得到很多最简解，将他们排序，最后输出靠前的解。
     * 在这其中，耗时最久的部分是步骤3。
     * 通过一定的计算，现有一个可以被利用的规律：
     * 对给定的combo总和、连击份数，要么可以用1-5段combo表示，要么无解。
     * PS: 该规律不一定正确，但可以加以利用。
     * <p>
     * 计算结果表明，即使在双层循环，即最多三段combo的情况下，依然得到了大量解。
     * 优化1就是舍弃用4、5段combo表示解。
     * 而在g、b过多时，对应的操作实在难以打出，且无用循环次数过多。
     * 优化2就是舍弃g+b大于20的所有情况，减少了绝大部分循环。
     * <p>
     * 程序目前测试结果，50 g+b 值，1500c，时间为2分36s，这是一个可以接受的数字。
     * 显然，上面两层优化提升了速度，但是也牺牲了准确率。
     * 但是我想，大部分情况下，该程序得到的解都是数量足够，且可以轻便的打出所需分数的。
     * 这样就够了。
     * enjoy！
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("输入note");
        int note = sc.nextInt();
        System.out.println("输入目标分数");
        int targetScore = sc.nextInt();
        new Cytus2Calculator(note, targetScore).process();
        System.out.println("cy2音游美学分数计算器 by萌泪");
        System.out.println("喜欢的话点个赞吧！");
    }

    static final int MAX_COMBO = 1700;

    /**
     * 将combo转为对应的贡献份数.
     */
    public static int c2s(int combo) {
        return combo * (combo - 1) / 2;
    }

    /**
     * 将贡献份数转为不超过该份数的最大combo数.
     * 若combo对应的份数小于等于传入的份数，且combo+1对应的份数大于传入的份数，则返回combo.
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

    /**
     * 将以毫秒ms为单位的时间差改成字符串，可选是否展示秒.
     *
     * @param beginTime 时间开始，单位ns
     * @param endTime   时间截止，单位ns
     * @return 对应的格式化时间字符串
     */
    public static String nanoSecondToStr(long beginTime, long endTime) {
        long nanoTime = Math.abs(endTime - beginTime);
        String str = "";
        if (nanoTime >= 60000000000L) {
            str = str + (nanoTime / 60000000000L) + " min ";
            nanoTime %= 60000000000L;
        }
        if (nanoTime >= 1000000000) {
            str = str + (nanoTime / 1000000000) + " s ";
            nanoTime %= 1000000000;
        }
        if (nanoTime >= 1000000) {
            str = str + (nanoTime / 1000000) + " ms ";
            nanoTime %= 1000000;
        }
        if (nanoTime != 0) {
            str = str + nanoTime + " ns";
        }
        return str;
    }
}
