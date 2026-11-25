package chronicboy.appleskince.mixin;

import chronicboy.appleskince.AppleSkinConfig;
import chronicboy.appleskince.FoodIcons;
import chronicboy.appleskince.FoodInfo;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.FoodStats;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.IntegratedServer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Potion;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws on top of BTW's hunger bar (GuiIngame.drawFoodOverlay). The icon maths below copies that method:
 * shank i sits at x = screenX - i * 8 - 9, hunger fills in sixths, fat fills in eighths.
 */
@Mixin(GuiIngame.class)
public class GuiIngameMixin {
	@Shadow
	@Final
	private Minecraft mc;

	@Unique
	private static int appleskin$failures = 0;

	/** Hatched exhaustion bar, drawn before BTW's shanks so it sits behind them like AppleSkin's. */
	@Inject(method = "drawFoodOverlay(II)V", at = @At("HEAD"))
	private void appleskin$beforeFoodBar(int screenX, int screenY, CallbackInfo ci) {
		if (!AppleSkinConfig.showExhaustion || appleskin$failures >= 5 || mc.thePlayer == null) return;
		boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
		try {
			float progress = appleskin$exhaustionProgress();
			if (progress > 0F) FoodIcons.drawExhaustionHatch(screenX, screenY, progress);
		} catch (Throwable t) {
			appleskin$failures++;
		} finally {
			GL11.glColor4f(1F, 1F, 1F, 1F);
			if (blend) GL11.glEnable(GL11.GL_BLEND);
			else GL11.glDisable(GL11.GL_BLEND);
			mc.getTextureManager().bindTexture(Gui.icons);
		}
	}

	@Inject(method = "drawFoodOverlay(II)V", at = @At("RETURN"))
	private void appleskin$afterFoodBar(int screenX, int screenY, CallbackInfo ci) {
		if (appleskin$failures >= 5 || mc.thePlayer == null) return;
		boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
		try {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if (AppleSkinConfig.showHudPreview) appleskin$drawPreview(screenX, screenY);
			appleskin$failures = 0;
		} catch (Throwable t) {
			appleskin$failures++;
		} finally {
			GL11.glColor4f(1F, 1F, 1F, 1F);
			if (blend) GL11.glEnable(GL11.GL_BLEND);
			else GL11.glDisable(GL11.GL_BLEND);
			mc.getTextureManager().bindTexture(Gui.icons);
		}
	}

	@Unique
	private void appleskin$drawPreview(int screenX, int screenY) {
		if (mc.thePlayer.isPotionActive(Potion.hunger)) return;
		FoodInfo info = FoodInfo.of(mc.thePlayer.getHeldItem());
		if (info == null || !info.canConsume(mc.thePlayer)) return;
		FoodInfo.Result r = info.predict(mc.thePlayer.getFoodStats());
		if (!r.changesAnything()) return;

		Gui gui = (Gui) (Object) this;
		mc.getTextureManager().bindTexture(Gui.icons);

		// Pulse between faint and clear, like AppleSkin.
		double t = (Minecraft.getSystemTime() % 1200L) / 1200.0;
		float alpha = 0.2F + 0.55F * (float) (0.5 + 0.5 * Math.sin(t * Math.PI * 2));

		int oldFatPips = appleskin$fatPips(r.oldFat);
		int newFatPips = appleskin$fatPips(r.newFat);

		for (int i = 0; i < 10; i++) {
			int x = screenX - i * 8 - 9;
			int oldHunger = appleskin$fill(r.oldFood, i, 6);
			int newHunger = appleskin$fill(r.newFood, i, 6);
			int oldFat = appleskin$fill(oldFatPips, i, 8);
			int newFat = appleskin$fill(newFatPips, i, 8);

			if (newFat > oldFat) {
				// Fat is the yellow background behind the shank.
				GL11.glColor4f(1F, 1F, 1F, alpha);
				appleskin$drawFat(gui, x, screenY, newFat);
				// Keep the shank itself crisp on top.
				GL11.glColor4f(1F, 1F, 1F, 1F);
				appleskin$drawShank(gui, x, screenY, newHunger);
			}
			if (newHunger > oldHunger) {
				GL11.glColor4f(1F, 1F, 1F, alpha);
				appleskin$drawShank(gui, x, screenY, newHunger);
			}
		}
	}

	/** How full icon i is (0..perIcon), given a total measured in that icon's units. */
	@Unique
	private static int appleskin$fill(int total, int i, int perIcon) {
		int full = total / perIcon;
		if (i < full) return perIcon;
		if (i == full) return total % perIcon;
		return 0;
	}

	/** Same rounding as BTW: fat is drawn in eighths of an icon. */
	@Unique
	private static int appleskin$fatPips(float fat) {
		return (int) ((fat + 0.124F) * 4F);
	}

	@Unique
	private static void appleskin$drawShank(Gui gui, int x, int y, int sixths) {
		if (sixths <= 0) return;
		if (sixths >= 6) {
			gui.drawTexturedModalRect(x, y, 52, 27, 9, 9);
		} else {
			gui.drawTexturedModalRect(x + 7 - sixths, y, 52 + 7 - sixths, 27, 3 + sixths, 9);
		}
	}

	@Unique
	private static void appleskin$drawFat(Gui gui, int x, int y, int eighths) {
		if (eighths <= 0) return;
		if (eighths >= 8) {
			gui.drawTexturedModalRect(x, y, 25, 27, 9, 9);
		} else {
			gui.drawTexturedModalRect(x + 8 - eighths, y, 25 + 8 - eighths, 27, 1 + eighths, 9);
		}
	}

	/**
	 * Progress (0..1) to the next hunger or fat loss. Exhaustion is never sent to the client, so this reads the
	 * integrated server's copy: singleplayer only, -1 elsewhere.
	 */
	@Unique
	private float appleskin$exhaustionProgress() {
		IntegratedServer server = mc.getIntegratedServer();
		if (server == null || server.getConfigurationManager() == null) return -1F;
		EntityPlayerMP serverPlayer = server.getConfigurationManager().getPlayerEntity(mc.thePlayer.username);
		if (serverPlayer == null) return -1F;
		FoodStats stats = serverPlayer.getFoodStats();
		// FoodStats.onUpdate burns fat (0.5 per step) while fat sits above the last hunger shank, else hunger (1.33).
		int food = stats.getFoodLevel();
		float fat = stats.getSaturationLevel();
		boolean burningFat = fat > (float) ((food + 5) / 6) * 2F;
		float step = burningFat ? 0.5F : 1.33F;
		return Math.max(0F, Math.min(1F, stats.foodExhaustionLevel / step));
	}
}
