package irc;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetMethod {
    String name();
}
