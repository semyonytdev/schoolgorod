package com.schoolgorod.block;

import com.schoolgorod.SchoolMod;
import com.schoolgorod.entity.TeacherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

/** Розовый крикун оглушительно атакует игроков поблизости и призывает учителя. */
public class PinkShriekerBlock extends Block {
	public PinkShriekerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
			Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.FAIL;
		}

		level.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 2.0F, 0.85F);
		AABB range = AABB.ofSize(pos.getCenter(), 16.0D, 8.0D, 16.0D);
		for (Player target : level.getEntitiesOfClass(Player.class, range)) {
			if (target.distanceToSqr(pos.getCenter()) <= 64.0D) {
				target.hurtServer(serverLevel, serverLevel.damageSources().sonicBoom(null), 12.0F);
			}
		}

		TeacherEntity teacher = SchoolMod.TEACHER.create(level, EntitySpawnReason.TRIGGERED);
		if (teacher == null) {
			return InteractionResult.FAIL;
		}
		teacher.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
		teacher.setYRot(level.random.nextFloat() * 360.0F);
		level.addFreshEntity(teacher);
		return InteractionResult.CONSUME;
	}
}
