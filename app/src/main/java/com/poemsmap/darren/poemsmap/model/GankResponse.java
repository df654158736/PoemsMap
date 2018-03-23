package com.poemsmap.darren.poemsmap.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Darren on 2018/3/21.
 */

public class GankResponse<T> implements Serializable {
    private static final long serialVersionUID = -686453405647539973L;
    public T results;
}