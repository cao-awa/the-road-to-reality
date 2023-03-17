package com.github.cao.awa.trtr.framework.accessor.identifier;

import com.github.cao.awa.trtr.framework.accessor.filed.FieldAccessor;
import net.minecraft.util.Identifier;

public class IdentifierAccessor implements FieldAccessor {
    public static final IdentifierAccessor ACCESSOR = new IdentifierAccessor();

    public Identifier get(Class<?> clazz) {
        return get(clazz,
                   "IDENTIFIER"
        );
    }

    public Identifier get(Object o) {
        return get(o.getClass());
    }

    public boolean has(Class<?> clazz) {
        return has(clazz, "IDENTIFIER");
    }

    public boolean has(Object block) {
        return has(block.getClass());
    }
}