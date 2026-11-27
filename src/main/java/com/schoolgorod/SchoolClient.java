package com.schoolgorod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WardenRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;

/**
 * Reuse vanilla renderers for the custom mobs. The raw provider cast is safe
 * here because StudentEntity/TeacherEntity extend Zombie and DirectorEntity
 * extends Warden; the vanilla renderers only use those inherited base APIs.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SchoolClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(SchoolMod.STUDENT, (EntityRendererProvider) ZombieRenderer::new);
		EntityRendererRegistry.register(SchoolMod.TEACHER, (EntityRendererProvider) ZombieRenderer::new);
		EntityRendererRegistry.register(SchoolMod.DIRECTOR, (EntityRendererProvider) WardenRenderer::new);
	}
}
