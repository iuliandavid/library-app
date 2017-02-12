/**
 * 
 */
package com.library.app.logaudit.interceptor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.library.app.logaudit.model.LogAudit.Action;

/**
 * @author iulian
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Auditable {
	Action action();
}
