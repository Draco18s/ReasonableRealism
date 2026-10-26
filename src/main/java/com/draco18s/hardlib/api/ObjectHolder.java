package com.draco18s.hardlib.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public static (non-final) field that EasyRegistry fills with a registered object once its registry has been
 * registered: Forge's ObjectHolder, which NeoForge doesn't have.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectHolder {
	/** The registry, e.g. "minecraft:block". */
	String registryName();

	/** The object's id. */
	String value();
}
