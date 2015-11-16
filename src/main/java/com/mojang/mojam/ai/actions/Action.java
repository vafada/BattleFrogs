package com.mojang.mojam.ai.actions;

import com.mojang.mojam.entities.Humanoid;

public abstract class Action {

    public abstract void performAction(Humanoid entity);

    public abstract boolean isConditionMet(Humanoid entity);
}
