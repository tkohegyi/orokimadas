package website.magyar.adoration.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Dummy annotation to mark public fields in Json (structure) files.
 * Their public access is intended, there is no point to make them private and add getter/setter for every field.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface JsonField {
}
