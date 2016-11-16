import org.igov.io.log.Logger;

public class ClassWithLogger {

    Logger log = new Logger();

    public void sayHello(){
        String smile = "smile";
        log.debug("This is igov approach", smile);
    }
}
