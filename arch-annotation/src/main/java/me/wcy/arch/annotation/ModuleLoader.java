package me.wcy.arch.annotation;

import java.util.List;

/**
 * Created by wcy on 2020/12/10.
 */
public interface ModuleLoader {
    void loadModule(List<BaseModule> moduleList);
}
