package com.maaackz.weirdcraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class BedDestroyMonitorMixin {

    @Shadow public abstract void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch);

    private BlockPos previousBedPos = null; // Store the previous bed/spawn position for monitoring
    private Formatting lastBedFormatting = Formatting.RED; // Default color if bed color is unknown
    private String lastBedName = "Bed"; // Default name if the bed block cannot be determined

    // Map of DyeColor to Minecraft Formatting colors
    private static final Map<DyeColor, Formatting> COLOR_FORMATTING_MAP = new HashMap<>();

    static {
        COLOR_FORMATTING_MAP.put(DyeColor.WHITE, Formatting.WHITE);
        COLOR_FORMATTING_MAP.put(DyeColor.ORANGE, Formatting.GOLD);
        COLOR_FORMATTING_MAP.put(DyeColor.MAGENTA, Formatting.LIGHT_PURPLE);
        COLOR_FORMATTING_MAP.put(DyeColor.LIGHT_BLUE, Formatting.AQUA);
        COLOR_FORMATTING_MAP.put(DyeColor.YELLOW, Formatting.YELLOW);
        COLOR_FORMATTING_MAP.put(DyeColor.LIME, Formatting.GREEN);
        COLOR_FORMATTING_MAP.put(DyeColor.PINK, Formatting.LIGHT_PURPLE);
        COLOR_FORMATTING_MAP.put(DyeColor.GRAY, Formatting.DARK_GRAY);
        COLOR_FORMATTING_MAP.put(DyeColor.LIGHT_GRAY, Formatting.GRAY);
        COLOR_FORMATTING_MAP.put(DyeColor.CYAN, Formatting.DARK_AQUA);
        COLOR_FORMATTING_MAP.put(DyeColor.PURPLE, Formatting.DARK_PURPLE);
        COLOR_FORMATTING_MAP.put(DyeColor.BLUE, Formatting.BLUE);
        COLOR_FORMATTING_MAP.put(DyeColor.BROWN, Formatting.GOLD);
        COLOR_FORMATTING_MAP.put(DyeColor.GREEN, Formatting.DARK_GREEN);
        COLOR_FORMATTING_MAP.put(DyeColor.RED, Formatting.DARK_RED);
        COLOR_FORMATTING_MAP.put(DyeColor.BLACK, Formatting.BLACK);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkBedDestroyed(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        World world = player.getWorld();
        BlockPos spawnPos = player.getSpawnPointPosition();

        // Only proceed if there is a spawn position set
        if (spawnPos != null) {
            Block currentBlock = world.getBlockState(spawnPos).getBlock();

            // If the block at spawn position is a bed, update stored bed information
            if (currentBlock instanceof BedBlock) {
                previousBedPos = spawnPos;
                DyeColor dyeColor = ((BedBlock) currentBlock).getColor();
                lastBedName = currentBlock.getName().getString(); // Capture bed name

                // Update bed formatting based on color, default to RED if color is unknown
                lastBedFormatting = COLOR_FORMATTING_MAP.getOrDefault(dyeColor, Formatting.RED);

            } else if (previousBedPos != null && previousBedPos.equals(spawnPos)) {
                // If the bed block at spawn position was destroyed
                previousBedPos = null; // Reset previous bed position to avoid repeating alerts

                // Find the nearest player to the destroyed bed position
                PlayerEntity nearestPlayer = world.getClosestPlayer(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 100, false);

                // Send messages and effects if there’s a nearby player to "blame"
                if (nearestPlayer != null) {
                    String playerName = nearestPlayer.getName().getString();

                    player.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 25, 5));
                    player.networkHandler.sendPacket(new TitleS2CPacket(Text.of("BED DESTROYED!").copy().styled(style -> style.withColor(0xFF0000))));
                    player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("You will no longer respawn!")));

                    world.playSound(null, nearestPlayer.getBlockPos(), net.minecraft.sound.SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                            net.minecraft.sound.SoundCategory.HOSTILE, 0.5F, 1.0F);
                    this.playSoundToPlayer(net.minecraft.sound.SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                            net.minecraft.sound.SoundCategory.HOSTILE, 0.5F, 1.0F);
                    for (ServerPlayerEntity playerEntity : world.getServer().getPlayerManager().getPlayerList()) {
                        playerEntity.sendMessage(
                                Text.empty()
                                        .append(Text.literal("\n"))
                                        .append(Text.literal("BED DESTRUCTION > ").setStyle(Style.EMPTY.withBold(true)))
                                        .append(Text.literal(lastBedName) // Use stored bed name
                                                .setStyle(Style.EMPTY.withColor(lastBedFormatting))) // Use stored bed color
                                        .append(Text.literal(" "))
                                        .append(Text.literal("was destroyed by ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                                        .append(Text.literal(playerName)
                                                .setStyle(Style.EMPTY.withColor(Formatting.RED)))
                                        .append(Text.literal("!").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                                        .append(Text.literal("\n")),
                                false
                        );
                    }
                }
            }
        }
    }
}
