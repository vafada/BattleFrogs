package com.mojang.mojam.ai;

import com.mojang.mojam.ai.actions.Action;

public class Behavior implements Comparable<Behavior> {

    private int priority;
    private Action action;

    public Behavior(int priority, Action action) {
        this.priority = priority;
        this.action = action;
    }

    public int getPriority() {
        return priority;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public int compareTo(Behavior behavior) {
        if (priority == behavior.getPriority()) {
            return 0;
        } else if (priority < behavior.getPriority()) {
            return -1;
        }
        return 1;
    }
}
