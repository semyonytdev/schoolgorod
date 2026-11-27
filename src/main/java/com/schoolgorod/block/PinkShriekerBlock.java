package com.schoolgorod.block;

import com.schoolgorod.SchoolMod;
import com.schoolgorod.entity.TeacherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Розовый крикун: при активации призывает учителя рядом с блоком. */
public class PinkShriekerBlock extends Block {
	public PinkShriekerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		TeacherEntity teacher = SchoolMod.TEACHER.create(level);
		if (teacher == null) {
			return InteractionResult.FAIL;
		}
		teacher.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
				level.random.nextFloat() * 360.0F, 0.0F);
		level.addFreshEntity(teacher);
		return InteractionResult.CONSUME;
	}
}
