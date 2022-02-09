package test;

import java.io.*;
import java.util.ArrayList;

import static main.Main.*;

public class Divide1 {

    public static int note;
    public static int shares;

    public static ArrayList<Integer> comboSum;
    public static ArrayList<Integer> sharesSum;
    public static ArrayList<int[]> comboDetail;
    public static boolean[][] haveSolution;

    static File f = new File("1.txt");

    public static void main(String[] args) {
        note = 100;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String s;
            while ((s = br.readLine()) != null) {
                shares = Integer.parseInt(s);
                x();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        Scanner sc = new Scanner(System.in);
        System.out.println("输入note数");
        note = sc.nextInt();
        System.out.println("输入目标份数总和");
        shares = sc.nextInt();
         */
    }

    public static void x() {
        if (shares < 0 || shares > c2s(note)) {
            System.out.println("初步判断：越界，不存在解");
            return;
        }

        comboSum = new ArrayList<>();
        sharesSum = new ArrayList<>();
        comboDetail = new ArrayList<>();
        haveSolution = new boolean[note + 1][c2s(note) + 1];
        long beginTime = System.nanoTime();

        for (int noteUsedNum = 2; noteUsedNum <= note; noteUsedNum++) {// combo表示combo总和
            int size = comboSum.size();
            // 判断使用一份combo是否能达到目标，即combo在s中
            if (s2c(c2s(noteUsedNum)) == noteUsedNum) {
                if (c2s(noteUsedNum) <= shares) {// 如果c2s(combo)超过预定份数，就不用记录了
                    // 记录数据：combo数、份数、combo具体情况
                    comboSum.add(noteUsedNum);
                    sharesSum.add(c2s(noteUsedNum));
                    comboDetail.add(new int[]{noteUsedNum});
                }
            }
            // 判断使用多份combo是否能达到目标
            /*
            for (int i = 2; i < noteUsedNum; i++) {// i表示新增部分的combo数
                // long b0 = System.nanoTime();
                for (int j = 0; j < size; j++) {// j表示以前算的数据的第j项
                    int c0 = i + comboSum.get(j);
                    if (c0 > note) {
                        continue;
                    }
                    int f0 = c2s[i] + sharesSum.get(j);
                    if (f0 > shares) {
                        continue;
                    }
                    if (!haveSolution[c0][f0]) {
                        int[] q = comboDetail.get(j);
                        int len = q.length;
                        comboSum.add(c0);
                        sharesSum.add(f0);
                        int[] w = new int[len + 1];
                        w[0] = i;
                        System.arraycopy(q, 0, w, 1, len);
                        comboDetail.add(w);
                        haveSolution[c0][f0] = true;
                    }
                }
                //System.out.println("计算小进度：" + i + "/" + noteUsedNum +
                //        "，用时" + (System.nanoTime() - b0) / 1000000 + "ms");
            }
             */
            CalculateThread1[] threads = new CalculateThread1[8];
            for (int i = 0; i < 8; i++) {
                threads[i] = new CalculateThread1(i, noteUsedNum, size);
                threads[i].start();
            }
            try {
                for (int i = 0; i < 8; i++) {
                    threads[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //long endTime = System.nanoTime();
            //System.out.println("计算进度：" + noteUsedNum + "/" + note +
            //        "，用时" + (endTime - beginTime) / 1000000 + "ms");
            //beginTime = endTime;
        }

        boolean haveFinalSolution = false;
        for (int i = 0; i < sharesSum.size(); i++) {
            if (sharesSum.get(i) == shares) {
                haveFinalSolution = true;
                int[] q = comboDetail.get(i);
                System.out.print("份数=" + sharesSum.get(i) +
                        " combo总和=" + comboSum.get(i) +
                        " combo分段数目=" + q.length +
                        " 所需最低bad数=" + Math.max(comboSum.get(i) + q.length - 1 - note, 0) +
                        " 分段细节：");
                for (int value : q) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }
        }
        if (!haveFinalSolution) {
            System.out.println(shares + "无解");
        }
    }

}
