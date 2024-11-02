package retryabletask;

import exceptions.ExponencialRetryException;
import exceptions.MaxAttemptsExponencialRetryException;
import exceptions.ThreadInterruptedException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RetryWithExponentialBackoff {
    private static final int MAX_ATTEMPTS = 5;
    private static final long INITIAL_BACKOFF_MILLIS = 1000; // 1 second
    private static final long MAX_BACKOFF_MILLIS = 10000; // 10 seconds
    private static final long BASE = 2; // Exponential backoff base
    private static final float SUCCESS_CHANCE = 0.9f; // Success chance

    private static final Logger logger = Logger.getLogger("RetryWithExponentialBackoff.class");


    public static void main(String[] args) {

        try {
            final int attempts = retryTask();

            logger.log(Level.INFO, "Task completed successfully in {0} attempts.", attempts);

        } catch (MaxAttemptsExponencialRetryException e) {

            logger.log(Level.INFO, "Task failed after {0} retries: {1}.", new Object[]{e.getAttempts(), e.getMessage()});

        }
    }

    // Retry task with exponential backoff time and max attempt tries
    private static int retryTask() throws MaxAttemptsExponencialRetryException, ThreadInterruptedException{

        int attempts = 1;

        while(attempts < MAX_ATTEMPTS) {

            try {

                performTask(attempts);

                return attempts;

            } catch (ExponencialRetryException e) {

                if(attempts >= MAX_ATTEMPTS) {

                    throw new MaxAttemptsExponencialRetryException("Max retry reached.", e, attempts);
                }

                long backOffTime = calculateBackOffWithJitter(attempts);
                logger.info(String.format("Attempt %d failed. Retrying in %d ms...%n", attempts, backOffTime));

                attempts++;

                pauseThreadForExponentialBackoffTime(backOffTime);
            }
        }
        return attempts;
    }

    // Compute exponential backoff time
    private static long calculateBackOffWithJitter(final int attempts) {

        return (long) Math.min(INITIAL_BACKOFF_MILLIS * Math.pow(BASE, attempts - 1.0), MAX_BACKOFF_MILLIS);

    }

    // Pause thead for a given time
    private static void pauseThreadForExponentialBackoffTime(long backOffTime) {

        try {

            Thread.sleep(backOffTime);

        } catch (InterruptedException ie) {

            Thread.currentThread().interrupt();

            throw new ThreadInterruptedException("Thread was interrupted during retry delay", ie);

        }

    }

    // Random success or fail for a hypothetical task
    private static void performTask(final int attempts){

        if(Math.random() > SUCCESS_CHANCE) {

            logger.log(Level.INFO,"{0}. Task succeeded.", attempts);

        } else {

            throw new ExponencialRetryException(String.format("%d. Task failed.", attempts));

        }

    }
}