package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static main.Main.*;
import static test.Divide2.*;

public class CalculateThread2 extends Thread {

    int threadNo;

    CalculateThread2(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        for (int s = 1; s < c2s(note); s++) {
            if (s % 8 == threadNo) {
                x(s);
            }
        }
    }

    public void x(int shares) {
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
        writeToFile(shares);
    }

    static File f = new File("1.txt");

    public synchronized void writeToFile(int shares) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
            bw.write(shares + "");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
