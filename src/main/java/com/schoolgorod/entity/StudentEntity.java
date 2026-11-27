package com.schoolgorod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

/** Ученик — враждебный зомби с параметрами школьного ученика. */
public class StudentEntity extends Zombie {
	public StudentEntity(EntityType<? extends Zombie> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.34D)
				.add(Attributes.FOLLOW_RANGE, 32.0D)
				.add(Attributes.ATTACK_DAMAGE, 4.0D);
	}
}
