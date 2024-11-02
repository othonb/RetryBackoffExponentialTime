package exceptions;

public class MaxAttemptsExponencialRetryException extends Exception{

    private int attempts;

    public MaxAttemptsExponencialRetryException(String s, Exception e, int attempts) {
        super(s, e);
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }
}
