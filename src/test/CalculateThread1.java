package test;

import static test.Divide1.*;
import static main.Main.*;

public class CalculateThread1 extends Thread {

    int threadNo;
    int noteUsedNum;
    int size;

    CalculateThread1(int threadNo, int noteUsedNum, int size) {
        this.threadNo = threadNo;
        this.noteUsedNum = noteUsedNum;
        this.size = size;
    }

    @Override
    public void run() {
        for (int i = 2; i < noteUsedNum; i++) {// i表示新增部分的combo数
            if (i % 8 == threadNo) {
                for (int j = 0; j < size; j++) {// j表示以前算的数据的第j项
                    int c0 = i + comboSum.get(j);
                    if (c0 > note) {
                        continue;
                    }
                    int f0 = c2s(i) + sharesSum.get(j);
                    if (f0 > shares) {
                        continue;
                    }
                    x(c0, f0, j, i);
                }
            }
        }
    }

    public synchronized void x(int c0, int f0, int j, int i) {
        if (!haveSolution[c0][f0]) {
            comboSum.add(c0);
            sharesSum.add(f0);
            int[] q = comboDetail.get(j);
            int[] w = new int[q.length + 1];
            w[0] = i;
            System.arraycopy(q, 0, w, 1, q.length);
            comboDetail.add(w);
            haveSolution[c0][f0] = true;
        }
    }

}
