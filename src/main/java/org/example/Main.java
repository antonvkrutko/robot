package org.example;

public class Main {
    public static void main(String[] args) {
        Object lock = new Object();

        Thread leftThread = new Thread(new Printer(() -> "left", 300L, lock));
        Thread rightThread = new Thread(new Printer(() -> "right", 500L, lock));

        leftThread.start();
        rightThread.start();

        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}

interface SourceProvider {
    String provide();
}

class Printer implements Runnable {
    private final SourceProvider provider;
    private final Long delay;

    private final Object lock;

    Printer(SourceProvider provider, Long delay, Object lock) {
        this.provider = provider;
        this.delay = delay;
        this.lock = lock;
    }

    private void print() {
        System.out.println(provider.provide());
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                print();
                sleep(delay);

                try {
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void sleep(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}