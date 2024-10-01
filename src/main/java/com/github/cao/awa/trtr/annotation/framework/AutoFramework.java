package com.github.cao.awa.trtr.annotation.framework;

import com.github.cao.awa.apricot.annotations.Auto;
import com.github.cao.awa.apricot.annotations.Stable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Auto
@Stable
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AutoFramework {
}
