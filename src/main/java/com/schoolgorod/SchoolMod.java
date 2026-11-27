package com.schoolgorod;

import com.schoolgorod.entity.DirectorEntity;
import com.schoolgorod.entity.StudentEntity;
import com.schoolgorod.entity.TeacherEntity;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.util.valueproviders.ConstantInt;

/** Школа-город: этап 1 и последующие игровые элементы. */
public class SchoolMod implements ModInitializer {
	public static final String MOD_ID = "schoolgorod";

	public static final Item PINK_SHARD = registerItem("pink_shard", Item::new, new Item.Properties().rarity(Rarity.EPIC));
	public static final Item TEACHER_TEMPLATE = registerItem("teacher_template", Item::new, new Item.Properties().rarity(Rarity.EPIC));
	public static final Item CHERRY_TEMPLATE = registerItem("cherry_template", Item::new, new Item.Properties().rarity(Rarity.EPIC));
	public static final Item BOSS_TEMPLATE = registerItem("boss_template", Item::new, new Item.Properties().rarity(Rarity.EPIC));

	public static final Block PINK_SCULK = registerBlock("pink_sculk");
	public static final Item PINK_SCULK_ITEM = registerBlockItem("pink_sculk", PINK_SCULK);

	public static final EntityType<StudentEntity> STUDENT = registerEntity(
			"student", StudentEntity::new, 0.6F, 1.95F);
	public static final EntityType<TeacherEntity> TEACHER = registerEntity(
			"teacher", TeacherEntity::new, 0.6F, 1.95F);
	public static final EntityType<DirectorEntity> DIRECTOR = registerEntity(
			"director", DirectorEntity::new, 0.9F, 2.9F);

	public static final Item STUDENT_SPAWN_EGG = registerSpawnEgg("student_spawn_egg", STUDENT);
	public static final Item TEACHER_SPAWN_EGG = registerSpawnEgg("teacher_spawn_egg", TEACHER);
	public static final Item DIRECTOR_SPAWN_EGG = registerSpawnEgg("director_spawn_egg", DIRECTOR);

	private static <T extends Entity> EntityType<T> registerEntity(
			String name, EntityType.EntityFactory<T> factory, float width, float height) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(
				Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
		EntityType<T> type = EntityType.Builder.of(factory, MobCategory.MONSTER)
				.sized(width, height)
				.build(key);
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
	}

	private static Item registerSpawnEgg(String name, EntityType<? extends Mob> entityType) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		Item.Properties properties = new Item.Properties().setId(key).spawnEgg(entityType);
		return Registry.register(BuiltInRegistries.ITEM, key, new SpawnEggItem(properties));
	}

	private static <T extends Item> T registerItem(String name, Function<Item.Properties, T> factory, Item.Properties props) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		T item = factory.apply(props.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	private static Block registerBlock(String name) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
		Block block = new DropExperienceBlock(ConstantInt.of(2), BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK).setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	private static Item registerBlockItem(String name, Block block) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		BlockItem item = new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix().rarity(Rarity.EPIC));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(STUDENT, StudentEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(TEACHER, TeacherEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(DIRECTOR, DirectorEntity.createAttributes());

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
			entries.accept(PINK_SHARD);
			entries.accept(TEACHER_TEMPLATE);
			entries.accept(CHERRY_TEMPLATE);
			entries.accept(BOSS_TEMPLATE);
		});
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
			entries.accept(STUDENT_SPAWN_EGG);
			entries.accept(TEACHER_SPAWN_EGG);
			entries.accept(DIRECTOR_SPAWN_EGG);
		});
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> entries.accept(PINK_SCULK_ITEM));
	}
}
