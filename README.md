# AppleSkin CE

Food information for Better Than Wolves CE 3.0.0 and above (Minecraft 1.6.4), inspired by AppleSkin.

BTW's hunger system is one of the hardest parts of the mod, and the game never tells you the numbers. AppleSkin CE shows them.

## Features

### Food tooltips
Every food and drinkable bucket shows two rows of icons in its tooltip, in the inventory and in EMI:
- **Shanks**: how much hunger it restores.
- **Fat**: how much fat it gives if all of it overflows a full hunger bar. In BTW, food only turns into fat when your hunger bar is already full.

Foods that can make you sick say so, for example raw meat's chance of the Hunger effect, or Poison from spider eyes, with the chance and how long it lasts.

Turn on `showTooltipNumbers` for exact numbers, plus a line saying what eating it right now would do.

### Hunger bar preview
Hold a food and the shanks and fat it would add flash on your hunger bar, so you can see whether you'd waste it.

### Exhaustion
A checkered bar behind the hunger bar, like AppleSkin's, grows from the right as you get closer to losing the next bit of hunger (or fat, when you're burning fat). Exhaustion is never sent to the client, so this works in singleplayer only.

## Settings
Each feature can be turned off in `config/appleskin-ce.conf`:

```
showTooltips=true
showTooltipEffects=true
showTooltipNumbers=false
showHudPreview=true
showExhaustion=true
```

## Compatibility
Client-side. Works in singleplayer and on servers (the exhaustion bar only appears in singleplayer). Requires Better Than Wolves CE.

## Credits
Inspired by [AppleSkin](https://github.com/squeek502/AppleSkin) by squeek502. AppleSkin CE is a separate implementation written for BTW's hunger and fat system.
