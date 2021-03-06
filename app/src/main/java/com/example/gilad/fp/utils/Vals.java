package com.example.gilad.fp.utils;

/**
 * Created by Gilad on 9/20/2015.
 */
public class Vals {

    public enum Types{
        LIST, TRIPLE_STORY, PATTERN, PIN
    }

    // In lieu of enum for SharedPrefs, for now?
    public static final int list = 0;
    public static final int story = 1;
    public static final int pattern = 2;
    public static final int pin = 3;

    public static final int STAGES = 2;
    public static final int[] ITERATIONS = {3, 3, 5, 3, 3, 3};
    public static final int[] GAP = {1, 1, 1, 2, 2, 1};
}
