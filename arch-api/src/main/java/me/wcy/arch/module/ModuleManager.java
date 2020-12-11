package me.wcy.arch.module;

import java.util.ArrayList;
import java.util.List;

import me.wcy.arch.annotation.BaseModule;
import me.wcy.arch.annotation.ModuleLoader;

/**
 * Created by wcy on 2020/12/10.
 */
class ModuleManager {
    private static final List<BaseModule> sModuleList = new ArrayList<>();

    public static List<BaseModule> getModuleList() {
        return sModuleList;
    }

    public static void register(ModuleLoader moduleLoader) {
        moduleLoader.loadModule(sModuleList);
    }
}
