package irc;


public interface ISentence {
    @TargetMethod(name="write")
    public void write(String text);
    @TargetMethod(name="read")
    public String read();
}
