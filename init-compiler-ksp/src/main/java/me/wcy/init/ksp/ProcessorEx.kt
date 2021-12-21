package me.wcy.init.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName

/**
 * Created by wangchenyan.top on 2021/12/21.
 */

fun KSDeclaration.toClassName(): ClassName {
    return ClassName(packageName.asString(), simpleName.asString())
}