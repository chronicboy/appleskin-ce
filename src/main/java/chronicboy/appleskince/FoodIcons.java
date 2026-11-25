package chronicboy.appleskince;

import net.minecraft.src.DynamicTexture;
import net.minecraft.src.Gui;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * Drawing shared by the HUD and the tooltips. Shank and fat icons come from the game's own icons.png
 * (as replaced by BTW), so they follow resource packs; the u/v numbers match GuiIngame.drawFoodOverlay.
 */
public final class FoodIcons {
	public static final int ICON = 9;
	public static final int TOOLTIP_SPACING = 9;

	// Tooltip placeholder lines. A row of spaces reserves room; the marker says which row to paint there.
	// The codes are invisible because the line holds nothing but spaces.
	public static final String MARK_HUNGER = "§3§0§3§r";
	public static final String MARK_FAT = "§3§1§3§r";

	/** The stack whose tooltip is being drawn in an inventory screen, or null. */
	public static net.minecraft.src.ItemStack tooltipStack;

	private static final Gui GUI = new Gui();
	private static DynamicTexture hatch;

	private FoodIcons() {
	}

	// ---- icons ----

	public static void bindIcons() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
	}

	public static void setZ(float z) {
		GUI.zLevel = z;
	}

	/** Empty shank outline (the background every shank sits on). */
	public static void drawShankBackground(int x, int y) {
		GUI.drawTexturedModalRect(x, y, 16, 27, ICON, ICON);
	}

	/** A shank filled in sixths, 6 = full. Partial shanks fill from the right, like BTW's bar. */
	public static void drawShank(int x, int y, int sixths) {
		if (sixths <= 0) return;
		if (sixths >= 6) {
			GUI.drawTexturedModalRect(x, y, 52, 27, ICON, ICON);
		} else {
			GUI.drawTexturedModalRect(x + 7 - sixths, y, 52 + 7 - sixths, 27, 3 + sixths, ICON);
		}
	}

	/** BTW's yellow fat background, filled in eighths, 8 = full. */
	public static void drawFat(int x, int y, int eighths) {
		if (eighths <= 0) return;
		if (eighths >= 8) {
			GUI.drawTexturedModalRect(x, y, 25, 27, ICON, ICON);
		} else {
			GUI.drawTexturedModalRect(x + 8 - eighths, y, 25 + 8 - eighths, 27, 1 + eighths, ICON);
		}
	}

	// ---- tooltip rows ----

	/** Number of icons a row needs, rounding a partial icon up. */
	public static int hungerIcons(FoodInfo info) {
		return (info.hunger + 5) / 6;
	}

	/** Fat if all of this food overflowed, in eighths of an icon. */
	public static int fatEighths(FoodInfo info) {
		float icons = info.hunger / (float) FoodInfo.UNITS_PER_SHANK * info.fatMultiplier;
		return Math.round(icons * 8F);
	}

	public static int fatIcons(FoodInfo info) {
		return (fatEighths(info) + 7) / 8;
	}

	public static int rowWidth(int icons) {
		return icons <= 0 ? 0 : (icons - 1) * TOOLTIP_SPACING + ICON;
	}

	/** Draws the hunger row (left to right) for a tooltip. Texture must be icons.png. */
	public static void drawHungerRow(FoodInfo info, int x, int y) {
		int remaining = info.hunger;
		int n = hungerIcons(info);
		for (int i = 0; i < n; i++) {
			int ix = x + i * TOOLTIP_SPACING;
			drawShankBackground(ix, y);
			drawShank(ix, y, Math.min(6, remaining));
			remaining -= 6;
		}
	}

	/** Draws the fat row (left to right) for a tooltip. */
	public static void drawFatRow(FoodInfo info, int x, int y) {
		int remaining = fatEighths(info);
		int n = fatIcons(info);
		for (int i = 0; i < n; i++) {
			int ix = x + i * TOOLTIP_SPACING;
			drawShankBackground(ix, y);
			drawFat(ix, y, Math.min(8, remaining));
			remaining -= 8;
		}
	}

	/**
	 * Draws whichever row a tooltip line marks, at the line's position. Returns false for ordinary lines.
	 */
	public static boolean drawMarkedLine(String line, FoodInfo info, int x, int y) {
		if (info == null || line == null) return false;
		if (line.contains(MARK_HUNGER)) {
			drawHungerRow(info, x, y - 1);
			return true;
		}
		if (line.contains(MARK_FAT)) {
			drawFatRow(info, x, y - 1);
			return true;
		}
		return false;
	}

	public static boolean isMarked(String line) {
		return line != null && (line.contains(MARK_HUNGER) || line.contains(MARK_FAT));
	}

	// ---- exhaustion hatching ----

	/**
	 * AppleSkin-style exhaustion underlay: an 81 x 9 checker behind the hunger bar, growing from the right as
	 * exhaustion builds. Drawn before BTW's shanks; it shows in the gaps around them.
	 *
	 * @param screenX the right edge BTW passes to drawFoodOverlay (shank 0 spans screenX - 9 .. screenX)
	 * @param z       GuiIngame's zLevel (-90 while the bars draw). The HUD has depth testing on, so drawing in
	 *                front of that would stop the shanks drawing where the checker is.
	 */
	public static void drawExhaustionHatch(int screenX, int screenY, float z, float progress) {
		if (progress <= 0F) return;
		int full = 81;
		int width = Math.round(full * Math.min(1F, progress));
		if (width <= 0) return;

		if (hatch == null) hatch = makeHatch(full, ICON);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, hatch.getGlTextureId());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		// Never block what's drawn over it.
		GL11.glDepthMask(false);

		double x1 = screenX;
		double x0 = x1 - width;
		double y0 = screenY;
		double y1 = screenY + ICON;
		double u0 = (full - width) / (double) full;
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(x0, y1, z, u0, 1);
		t.addVertexWithUV(x1, y1, z, 1, 1);
		t.addVertexWithUV(x1, y0, z, 1, 0);
		t.addVertexWithUV(x0, y0, z, u0, 0);
		t.draw();

		GL11.glDepthMask(true);
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	/** A one-pixel checker of dark grey and clear pixels, like AppleSkin's exhaustion texture. */
	private static DynamicTexture makeHatch(int w, int h) {
		DynamicTexture tex = new DynamicTexture(w, h);
		int[] data = tex.getTextureData();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				boolean dark = ((x + y) & 1) == 0;
				data[y * w + x] = dark ? 0xC0303030 : 0x00000000;
			}
		}
		tex.updateDynamicTexture();
		return tex;
	}
}
