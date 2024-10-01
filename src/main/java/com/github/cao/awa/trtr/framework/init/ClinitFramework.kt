package com.github.cao.awa.trtr.framework.init

import com.github.cao.awa.trtr.annotation.framework.AutoClinit
import com.github.cao.awa.trtr.annotation.framework.AutoFramework
import com.github.cao.awa.trtr.framework.ReflectionFramework
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@AutoFramework
class ClinitFramework : ReflectionFramework() {
    val LOGGER: Logger = LogManager.getLogger("ClinitFramework")

    override fun work() {
        reflection()
            .getTypesAnnotatedWith(AutoClinit::class.java)
            .forEach {
                LOGGER.info("Clinit for {}", it.name)
            }
    }
}
