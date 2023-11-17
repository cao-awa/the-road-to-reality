package com.github.cao.awa.trtr.annotation.inaction;

import com.github.cao.awa.apricot.anntation.Auto;
import com.github.cao.awa.apricot.anntation.Stable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation mark a method should not be overrides.
 */
@Auto
@Stable
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DoNotOverride {
}
