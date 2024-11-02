package exceptions;

public class ThreadInterruptedException extends RuntimeException{

    public ThreadInterruptedException(String msg, Exception e) {
        super(msg, e);
    }
}
