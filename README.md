# AppleSkin CE

Food information for Better Than Wolves CE 3.0.0 and above (Minecraft 1.6.4), inspired by AppleSkin.

BTW's hunger system is one of the hardest parts of the mod, and the game never tells you the numbers. AppleSkin CE shows them.

## Features

### Food tooltips
Every food and drinkable bucket shows:
- **Hunger**: how many shanks it restores.
- **Overflow to fat**: in BTW, food only turns into fat when it overflows a full hunger bar. This is how much of that overflow becomes fat. For example, 60% means each overflowing shank gives 0.6 of a fat icon.
- **Now**: exactly what eating it right now would do, given your current hunger and fat. It also tells you when you're too full, or when the Hunger effect stops you eating.

### Hunger bar preview
Hold a food and the shanks and fat it would add flash on your hunger bar, so you can see whether you'd waste it.

### Exhaustion line
A thin line under the hunger bar fills up as you get closer to losing the next bit of hunger, or fat when you're burning fat (yellow). Exhaustion is never sent to the client, so this works in singleplayer only.

## Settings
Each feature can be turned off in `config/appleskin-ce.properties`:

```
showTooltips=true
showTooltipNowLine=true
showHudPreview=true
showExhaustion=true
```

## Compatibility
Client-side. Works in singleplayer and on servers (the exhaustion line only appears in singleplayer). Requires Better Than Wolves CE.

## Credits
Inspired by [AppleSkin](https://github.com/squeek502/AppleSkin) by squeek502. AppleSkin CE is a separate implementation written for BTW's hunger and fat system.
