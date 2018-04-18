package com.jason.multithreads.constants;

/**
 * Description:
 * <p>
 * Created by zhenzong on 2018/4/15 10:36.<p>
 * Email: reozong@gmail.com <p>
 * Reference:
 */
public class Constants {
    public static final int CORES = Runtime.getRuntime().availableProcessors();
    public static final int POOL_SIZE = CORES + 1;
    public static final int POOL_SIZE_MAX = CORES * 2 + 1;
}
