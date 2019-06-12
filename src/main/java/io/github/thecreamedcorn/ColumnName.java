package io.github.thecreamedcorn;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for setting member variables to corresponding table's column name
 *
 * This annotation should be used on setter methods so that when using
 * the libraries object mapping feature, it can determine which method
 * will be used to set certain member variables. Note: This should
 * only be used on setters in POJOs.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnName {
	String name();
}
