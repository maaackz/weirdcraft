//package com.maaackz.weirdcraft.mixin;
//
//import net.minecraft.block.*;
//import net.minecraft.block.enums.BedPart;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
//import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
//import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Style;
//import net.minecraft.text.Text;
//import net.minecraft.util.DyeColor;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.World;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static net.minecraft.block.BedBlock.PART;
//
//@Mixin(BedBlock.class)
//public abstract class BedDestroyMixin {
//
//    @Shadow
//    public abstract DyeColor getColor(); // Access the bed color
//
//    // Map of DyeColor to Minecraft Formatting colors
//    private static final Map<DyeColor, Formatting> COLOR_FORMATTING_MAP = new HashMap<>();
//
//    static {
//        COLOR_FORMATTING_MAP.put(DyeColor.WHITE, Formatting.WHITE);
//        COLOR_FORMATTING_MAP.put(DyeColor.ORANGE, Formatting.GOLD);
//        COLOR_FORMATTING_MAP.put(DyeColor.MAGENTA, Formatting.LIGHT_PURPLE);
//        COLOR_FORMATTING_MAP.put(DyeColor.LIGHT_BLUE, Formatting.AQUA);
//        COLOR_FORMATTING_MAP.put(DyeColor.YELLOW, Formatting.YELLOW);
//        COLOR_FORMATTING_MAP.put(DyeColor.LIME, Formatting.GREEN);
//        COLOR_FORMATTING_MAP.put(DyeColor.PINK, Formatting.LIGHT_PURPLE);
//        COLOR_FORMATTING_MAP.put(DyeColor.GRAY, Formatting.DARK_GRAY);
//        COLOR_FORMATTING_MAP.put(DyeColor.LIGHT_GRAY, Formatting.GRAY);
//        COLOR_FORMATTING_MAP.put(DyeColor.CYAN, Formatting.DARK_AQUA);
//        COLOR_FORMATTING_MAP.put(DyeColor.PURPLE, Formatting.DARK_PURPLE);
//        COLOR_FORMATTING_MAP.put(DyeColor.BLUE, Formatting.BLUE);
//        COLOR_FORMATTING_MAP.put(DyeColor.BROWN, Formatting.GOLD);
//        COLOR_FORMATTING_MAP.put(DyeColor.GREEN, Formatting.DARK_GREEN);
//        COLOR_FORMATTING_MAP.put(DyeColor.RED, Formatting.DARK_RED);
//        COLOR_FORMATTING_MAP.put(DyeColor.BLACK, Formatting.BLACK);
//    }
//
//    @Inject(method = "onBreak", at = @At("HEAD"))
//    private void onBedBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
//        if (!world.isClient) {
//            if (player == null) {
//                player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
//            }
//
//            if (player != null) {
//                DyeColor bedColor = this.getColor();
//                String bedName = getBedName(state);
//                String playerName = player.getName().getString();
//
//                BlockPos footPos = null;
//                BlockState footState = null;
//
//                if (state.get(PART) == BedPart.HEAD) {
//                    Direction bedDirection = state.get(BedBlock.FACING);
//                    footPos = pos.offset(bedDirection.getOpposite());
//                    footState = world.getBlockState(footPos);
//                } else if (state.get(PART) == BedPart.FOOT) {
//                    Direction bedDirection = state.get(BedBlock.FACING);
//                    footPos = pos.offset(bedDirection);
//                    footState = world.getBlockState(footPos);
//                }
//
//                System.out.println(pos);
//                System.out.println(footPos);
//
//                if (footState != null && footState.getBlock() instanceof BedBlock && footState.get(PART) == BedPart.FOOT) {
//                    System.out.println("Foot part located at: " + footPos);
//                }
//
//                for (ServerPlayerEntity serverPlayer : world.getServer().getPlayerManager().getPlayerList()) {
//                    if (serverPlayer.getSpawnPointPosition() != null &&
//                            (serverPlayer.getSpawnPointPosition().equals(pos) || serverPlayer.getSpawnPointPosition().equals(footPos))) {
//                        serverPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 25, 5));
//                        serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.of("BED DESTROYED!").copy().styled(style -> style.withColor(0xFF0000))));
//                        serverPlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("You will no longer respawn!")));
//
//                        world.playSound(null, pos, net.minecraft.sound.SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
//                                net.minecraft.sound.SoundCategory.HOSTILE, 1.0F, 1.0F);
//
//                        Formatting bedFormatting = COLOR_FORMATTING_MAP.getOrDefault(bedColor, Formatting.WHITE);
//                        for (ServerPlayerEntity playerEntity : world.getServer().getPlayerManager().getPlayerList()) {
//                            playerEntity.sendMessage(
//                                    Text.empty()
//                                            .append(Text.literal("\n"))
//                                            .append(Text.literal("BED DESTRUCTION > ").setStyle(Style.EMPTY.withBold(true)))
//                                            .append(Text.literal(bedName)
//                                                    .setStyle(Style.EMPTY.withColor(bedFormatting)))
//                                            .append(Text.literal(" "))
//                                            .append(Text.literal("was destroyed by ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
//                                            .append(Text.literal(playerName)
//                                                    .setStyle(Style.EMPTY.withColor(Formatting.RED)))
//                                            .append(Text.literal("!").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
//                                            .append(Text.literal("\n")),
//                                    false
//                            );
//                        }
//
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
//    private String getBedName(BlockState state) {
//        return state.getBlock().getName().getString();
//    }
//}
