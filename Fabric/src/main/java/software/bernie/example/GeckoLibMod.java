package software.bernie.example;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.registry.BlockEntityRegistry;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Set;

public final class GeckoLibMod implements ModInitializer {

	public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";
	private static final boolean isDevelopmentEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		if (!shouldRegisterExamples()) {
			return;
		}

		new EntityRegistry();
		registerEntityAttributes();

		new ItemRegistry();
		new BlockEntityRegistry();

		new BlockRegistry();
		new SoundRegistry();

		registerEvents();
	}

	private void registerEvents() {
 		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClientSide) {
				Set<Item> wornArmor = new ObjectOpenHashSet<>();

				for (ItemStack stack : player.getArmorSlots()) {
					wornArmor.add(stack.getItem());
				}

				// Check each of the pieces match our set
				boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of(
						ItemRegistry.GECKO_ARMOR_BOOTS,
						ItemRegistry.GECKO_ARMOR_LEGGINGS,
						ItemRegistry.GECKO_ARMOR_CHESTPLATE,
						ItemRegistry.GECKO_ARMOR_HELMET));

				if (isFullSet) {
					ItemStack chestPiece = player.getInventory().armor.get(1);
					GeckoArmorItem geckoArmorItem = (GeckoArmorItem) chestPiece.getItem();
					geckoArmorItem.triggerAnim(player, GeoItem.getOrAssignId(chestPiece, (ServerLevel)world), "slam_controller", "slam");
				}
			}
			return InteractionResult.PASS;
		});
	}

	private void registerEntityAttributes() {
		FabricDefaultAttributeRegistry.register(EntityRegistry.BIKE, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.RACE_CAR, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.BAT, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.MUTANT_ZOMBIE, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.GREMLIN, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.COOL_KID, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.FAKE_GLASS, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.PARASITE, createGenericEntityAttributes());
	}

	private static AttributeSupplier.Builder createGenericEntityAttributes() {
		return PathfinderMob.createLivingAttributes().add(Attributes.MOVEMENT_SPEED, 0.80000000298023224D)
				.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 5)
				.add(Attributes.ATTACK_KNOCKBACK, 0.1);
	}

	/**
	 * By default, GeckoLib will register and activate several example entities,
	 * items, and blocks when in dev.<br>
	 * These examples are <u>not</u> present when in a production environment
	 * (normal players).<br>
	 * This can be disabled by setting the
	 * {@link GeckoLibMod#DISABLE_EXAMPLES_PROPERTY_KEY} to false in your run args
	 */
	static boolean shouldRegisterExamples() {
		return isDevelopmentEnvironment && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
	}
}
