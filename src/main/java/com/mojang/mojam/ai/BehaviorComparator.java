package com.mojang.mojam.ai;

import java.util.Comparator;

public class BehaviorComparator implements Comparator<Behavior> {

    @Override
    public int compare(Behavior behavior, Behavior behavior2) {
        return behavior.compareTo(behavior2);
    }
}
