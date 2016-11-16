import org.igov.io.log.Logger;

public class ClassWithLogger {
    Logger log = new Logger();

    public void sayHello(){
        log.debug("This is SLF4J approach. {}", "smile");

        // A big piece of business logig here
        // ...
        String smile = "smile";
        log.debug("This is igov approach", smile);
    }
}