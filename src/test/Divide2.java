package test;

import static main.Main.*;

public class Divide2 {

    public static int note;
    public static int shares;

    public static void main(String[] args) {
        /*
        Scanner sc = new Scanner(System.in);
        System.out.println("输入note数");
        note = sc.nextInt();
        System.out.println("输入目标份数总和");
        shares = sc.nextInt();

         */
        note = 100;
        CalculateThread2[] t = new CalculateThread2[8];
        for (int i = 0; i < 8; i++) {
            t[i] = new CalculateThread2(i);
            t[i].start();
        }
        try {
            for (int i = 0; i < 8; i++) {
                t[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        for (int s = 1; s < c2s[note]; s++) {
            shares = s;
            x();
        }*/

    }

    public static void x() {
        if (shares < 0 || shares > c2s(note)) {
            System.out.println("初步判断：越界，不存在解");
            return;
        }
        int c0 = s2c(shares);
        if (c2s(c0) == shares) {
            print(new int[]{c0});
            return;
        }
        for (int c1 = 2; c1 <= note - 2; c1++) {
            int sharesLeft = shares - c2s(c1);
            if (sharesLeft < 1) {
                break;
            }
            c0 = s2c(sharesLeft);
            if (c2s(c0) == sharesLeft && c0 + c1 <= note) {
                print(new int[]{c0, c1});
                return;
            }
        }
        for (int c1 = 2; c1 <= note - 2; c1++) {
            for (int c2 = 2; c2 <= note - 2 - c1; c2++) {
                int sharesLeft = shares - c2s(c1) - c2s(c2);
                if (sharesLeft < 1) {
                    break;
                }
                c0 = s2c(sharesLeft);
                if (c2s(c0) == sharesLeft && c0 + c1 + c2 <= note) {
                    print(new int[]{c0, c1, c2});
                    return;
                }
            }
        }
        for (int c1 = 2; c1 <= note - 2; c1++) {
            for (int c2 = 2; c2 <= note - 2 - c1; c2++) {
                for (int c3 = 2; c3 <= note - 2 - c1 - c2; c3++) {
                    int sharesLeft = shares - c2s(c1) - c2s(c2) - c2s(c3);
                    if (sharesLeft < 1) {
                        break;
                    }
                    c0 = s2c(sharesLeft);
                    if (c2s(c0) == sharesLeft && c0 + c1 + c2 + c3 <= note) {
                        print(new int[]{c0, c1, c2, c3});
                        return;
                    }
                }
            }
        }

        for (int c1 = 2; c1 <= note - 2; c1++) {
            for (int c2 = 2; c2 <= note - 2 - c1; c2++) {
                for (int c3 = 2; c3 <= note - 2 - c1 - c2; c3++) {
                    for (int c4 = 2; c4 <= note - 2 - c1 - c2 - c3; c4++) {
                        int sharesLeft = shares - c2s(c1) - c2s(c2) - c2s(c3) - c2s(c4);
                        if (sharesLeft < 1) {
                            break;
                        }
                        c0 = s2c(sharesLeft);
                        if (c2s(c0) == sharesLeft && c0 + c1 + c2 + c3 + c4 <= note) {
                            print(new int[]{c0, c1, c2, c3, c4});
                            return;
                        }
                    }
                }
            }
        }
        System.out.println(shares + "无解");
    }

    public static void print(int[] q) {
        int c = 0;
        for (int i : q) {
            c += i;
        }
        System.out.print("份数=" + shares +
                " combo总和=" + c +
                " combo分段数目=" + q.length +
                " 所需最低bad数=" + Math.max(c + q.length - 1 - note, 0) +
                " 分段细节：");
        for (int value : q) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

}
