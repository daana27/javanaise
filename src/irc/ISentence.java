package irc;

public interface ISentence {
    @TargetMethod(name="write")
    void write(String text);
    @TargetMethod(name="read")
    String read();
}
