package com.wol.wakeonlan.controller;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jl
 * @date 2021/9/28 10:31
 */
public interface IScanner {
    void Scan(String ipPattern) throws IOException;

    ArrayList<String> Parse(String macPattern) throws IOException;
}
