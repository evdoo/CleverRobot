package com.example.CleverRobot_1_0;

import android.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Vsevolod on 28.09.2014.
 */
public class AdaptiveControl {
    public static final long THRESHOLD = 1000;
    public static final int MULTIPLIER = 10;

    private Map<Long, Evaluations> database;
    private Set<Action> actions;
    private Random rnd;

    public AdaptiveControl() {
        database = new HashMap<Long, Evaluations>();
        actions = new ConcurrentSkipListSet<Action>();
        rnd = new Random();
    }

    public int analyze(boolean[] sensorDataWall, boolean[] sensorDataPaper) {
        long hash = 0;
        for (int i = 0; i < CleverRobot.LEVELS * CleverRobot.SECTORS; i++) {
            hash += Math.pow(2, 2 * i) * (sensorDataWall[i] ? 1 : 0) + Math.pow(2, 2 * i + 1) * (sensorDataPaper[i] ? 1 : 0);
        }

        if (!database.containsKey(hash)) {
            database.put(hash, new Evaluations());
        }

        int zeros = 0;
        for (int i = 0; i < 6; i++) {
            if (database.get(hash).evaluation[i] == 0) {
                zeros++;
            }
        }

        double magic_number = rnd.nextDouble();
        if (magic_number < 0.1 * (zeros + 1)) {
            int control_signal;
            int choice = rnd.nextInt(6 - zeros + zeros*10);
            int i = -1;
            if(choice >= zeros*10) {
                choice -= zeros*10;
                while (choice >= 0 && i < 6) {
                    i++;
                    if (database.get(hash).evaluation[i] != 0) {
                        choice--;
                    }
                }
            }
            else {
                choice /= 10;
                while (choice >= 0 && i < 6) {
                    i++;
                    if (database.get(hash).evaluation[i] == 0) {
                        choice--;
                    }
                }
            }
            control_signal = i;
            actions.add(new Action(hash, control_signal));
            return control_signal;
        }
        else {
            int max_evaluation = database.get(hash).evaluation[0];
            int max_control_signal = 0;
            for (int i = 1; i < 6; i++) {
                if (max_evaluation < database.get(hash).evaluation[i]) {
                    max_evaluation = database.get(hash).evaluation[i];
                    max_control_signal = i;
                }
            }
            return max_control_signal;
        }
    }

    public void evaluate(boolean feedback) {
        for (Action action : actions) {
            if (System.currentTimeMillis() - action.timestamp > THRESHOLD) {
                actions.remove(action);
            } else {
                action.evaluate(feedback);
            }
        }
    }

    private class Action implements Comparable<Action> {
        public long timestamp;
        private long state;
        private int action;

        public Action(long _state, int _action) {
            timestamp = System.currentTimeMillis();
            state = _state;
            action = _action;
        }

        public void evaluate(boolean feedback) {
            int change = (int) (100 * Math.pow(MULTIPLIER, timestamp - System.currentTimeMillis()) * (feedback ? 1 : -1));
            if (database.get(state).evaluation[action] > database.get(state).evaluation[action] + change ^ !feedback) {
                database.get(state).evaluation[action] += change;
            }
            else {
                database.get(state).evaluation[action] = feedback ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
        }

        @Override
        public int compareTo(Action another) {
            if (timestamp > another.timestamp) {
                return 1;
            }
            else if (timestamp < another.timestamp) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    private class Evaluations {
        private int[] evaluation;

        public Evaluations() {
            evaluation = new int[6];
            for (int i = 0; i < 6; i++) {
                evaluation[i] = 0;
            }
        }
    }
}
