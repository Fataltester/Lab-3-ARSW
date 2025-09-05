package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private volatile boolean paused = false;
    
    private final Object pauseLock = new Object();
    
    private volatile boolean alive = true;
    
    private volatile boolean running = true;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (running) {
                synchronized (pauseLock) {
                    while (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            if (!running)break;
            if (immortalsPopulation.size() > 1) {
                int myIndex = immortalsPopulation.indexOf(this);

                Immortal opponent = null;
                do {
                    int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                    if (nextFighterIndex != myIndex) {
                        Immortal candidate = immortalsPopulation.get(nextFighterIndex);
                        if (candidate.Alive()) {
                            opponent = candidate;
                        }
                    }
                } while (opponent == null && immortalsPopulation.size() > 1);

                if (opponent != null) {
                    this.fight(opponent);
                }

            } else if(immortalsPopulation.size() == 1 && this.Alive()){
                updateCallback.processReport(this + " is the only one alive.\n");
                break;
            } else{
                break;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) {
        Immortal first = this;
        Immortal second = i2;
        
        if (first.getName().compareTo(second.getName()) > 0) {
            first = i2;
            second = this;
        }
        synchronized (first) {
            synchronized (second) {
                if (!this.alive || !i2.alive) {
                    
                    if (!i2.alive && immortalsPopulation.contains(i2)) {
                        immortalsPopulation.remove(i2);
                        updateCallback.processReport(this + " says: " + i2 + " was already dead and removed!\n");
                    }
                    return;
                }

                int damage = Math.min(defaultDamageValue, i2.getHealth());
                i2.changeHealth(i2.getHealth() - damage);
                this.changeHealth(this.getHealth() + damage);

                if (i2.getHealth() <= 0) {
                    i2.alive = false;
                    immortalsPopulation.remove(i2);
                    updateCallback.processReport(i2 + " is dead and has been eliminated.\n");
                } else {
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                }
            }
        }
    }
    
    public void pauseInmortal(){
        synchronized (pauseLock) {
            paused = true;
        }
    }
    
    public void resumeImmortal(){
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public boolean Alive() {
        return alive && health > 0;
    }

}
