package com.schoolgorod.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;

/** Директор — босс школы: 300 HP, фиолетовый босс-бар, музыка и огромная награда. */
public class DirectorEntity extends Warden {
	private static final int MUSIC_LOOP_TICKS = 3900;
	private static final int DEFEAT_EXPERIENCE = 62295;
	private final ServerBossEvent bossEvent = new ServerBossEvent(
			Component.translatable("entity.schoolgorod.director"),
			BossEvent.BossBarColor.PURPLE,
			BossEvent.BossBarOverlay.PROGRESS);

	public DirectorEntity(EntityType<? extends Warden> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		bossEvent.setProgress(Math.max(0.0F, Math.min(1.0F, getHealth() / getMaxHealth())));
		if (!level().isClientSide() && tickCount % MUSIC_LOOP_TICKS == 1) {
			playSound(SoundEvents.MUSIC_DISC_OTHERSIDE.value(), 1.0F, 1.0F);
		}
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossEvent.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossEvent.removePlayer(player);
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		bossEvent.removeAllPlayers();
	}

	@Override
	public void die(DamageSource source) {
		if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
			ExperienceOrb.award(serverLevel, position(), DEFEAT_EXPERIENCE);
		}
		super.die(source);
		bossEvent.setProgress(0.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 300.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.30D)
				.add(Attributes.FOLLOW_RANGE, 64.0D)
				.add(Attributes.ATTACK_DAMAGE, 6.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}
}
