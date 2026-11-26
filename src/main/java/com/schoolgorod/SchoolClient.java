package com.schoolgorod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.WardenRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;

public class SchoolClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(SchoolMod.STUDENT, ZombieRenderer::new);
		EntityRendererRegistry.register(SchoolMod.TEACHER, ZombieRenderer::new);
		EntityRendererRegistry.register(SchoolMod.DIRECTOR, WardenRenderer::new);
	}
}
