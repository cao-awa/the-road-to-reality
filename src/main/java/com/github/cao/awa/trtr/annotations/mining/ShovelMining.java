package com.github.cao.awa.trtr.annotations.mining;

import com.github.cao.awa.apricot.anntations.Auto;
import com.github.cao.awa.apricot.anntations.Stable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Auto
@Stable
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ShovelMining {
    int value();
}