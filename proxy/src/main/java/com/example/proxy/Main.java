package com.example.proxy;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bo = new BufferedWriter(new OutputStreamWriter(System.out));

        String s = br.readLine();
        String[] split = s.split(" ");
        int n = Integer.parseInt(split[0]);
        int r = Integer.parseInt(split[1]);

        int[] ints = new int[n];
        for (int i = 1; i <= n; i++) {
            ints[i - 1] = i;
        }

        combination(ints, n, r, "", bo);
        bo.flush();
    }

    public static void combination(int[] box, int size, int popCnt, String result, BufferedWriter bo) throws IOException {
        if (popCnt == 0) {
            bo.write(result);
            bo.newLine();
        }
        for (int i : box) {
            int[] newArray = getNewArray(box, i);
            combination(newArray, size, popCnt - 1, result + i + " ", bo);
        }
    }

    public static int[] getNewArray(int[] arr, int i) {
        int length = arr.length;
        int[] newInts = new int[length - 1];
        int index = 0;
        for (int j = 0; j < arr.length; j++, index++) {
            int val = arr[j];
            if(i == val) {
                index--;
                continue;
            }
            newInts[index] = arr[j];
        }
        return newInts;
    }

}