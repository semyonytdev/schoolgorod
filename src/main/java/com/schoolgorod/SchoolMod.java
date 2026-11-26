package com.schoolgorod;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Школа-город: этап 1 — розовый осколок, розовый скалк, три розовых шаблона. */
public class SchoolMod implements ModInitializer {
	public static final String MOD_ID = "schoolgorod";

	// Rarity.EPIC красит название предмета в розовый (светло-фиолетовый) цвет.
	public static final Item PINK_SHARD = registerItem("pink_shard", Item::new,
			new Item.Properties().rarity(Rarity.EPIC));
	public static final Item TEACHER_TEMPLATE = registerItem("teacher_template", Item::new,
			new Item.Properties().rarity(Rarity.EPIC));
	public static final Item CHERRY_TEMPLATE = registerItem("cherry_template", Item::new,
			new Item.Properties().rarity(Rarity.EPIC));
	public static final Item BOSS_TEMPLATE = registerItem("boss_template", Item::new,
			new Item.Properties().rarity(Rarity.EPIC));

	/** Розовый скалк: даёт 2 опыта вместо 1 у обычного. */
	public static final Block PINK_SCULK = registerBlock("pink_sculk");
	public static final Item PINK_SCULK_ITEM = registerBlockItem("pink_sculk", PINK_SCULK);

	private static <T extends Item> T registerItem(String name, Function<Item.Properties, T> factory, Item.Properties props) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		T item = factory.apply(props.setId(key));
		Registry.register(BuiltInRegistries.ITEM, key, item);
		return item;
	}

	private static Block registerBlock(String name) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
		Block block = new DropExperienceBlock(ConstantInt.of(2),
				BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK).setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	private static Item registerBlockItem(String name, Block block) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		BlockItem item = new BlockItem(block,
				new Item.Properties().setId(key).useBlockDescriptionPrefix().rarity(Rarity.EPIC));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
			entries.accept(PINK_SHARD);
			entries.accept(TEACHER_TEMPLATE);
			entries.accept(CHERRY_TEMPLATE);
			entries.accept(BOSS_TEMPLATE);
		});
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> entries.accept(PINK_SCULK_ITEM));
	}
}
