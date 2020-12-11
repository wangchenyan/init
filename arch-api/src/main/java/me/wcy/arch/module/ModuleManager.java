package me.wcy.arch.module;

import java.util.ArrayList;
import java.util.List;

import me.wcy.arch.annotation.AbsModule;
import me.wcy.arch.annotation.ModuleLoader;

/**
 * Created by wcy on 2020/12/10.
 */
class ModuleManager {
    private static final List<AbsModule> sModuleList = new ArrayList<>();

    public static List<AbsModule> getModuleList() {
        return sModuleList;
    }

    public static void register(ModuleLoader moduleLoader) {
        moduleLoader.loadModule(sModuleList);
    }
}
