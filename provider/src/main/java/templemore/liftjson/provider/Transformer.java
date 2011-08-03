package templemore.liftjson.provider;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@java.lang.annotation.Target({METHOD, FIELD})
@java.lang.annotation.Retention(RUNTIME)
public @interface Transformer {

    Class<?> value();
}
