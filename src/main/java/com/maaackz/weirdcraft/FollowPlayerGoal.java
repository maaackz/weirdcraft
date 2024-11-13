package com.maaackz.weirdcraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class FollowPlayerGoal extends Goal {
    private final BeeEntity bee;
    private int tick = 0;

    public FollowPlayerGoal(BeeEntity bee, UUID targetUuid) {
        this.bee = bee;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        Optional<ServerPlayerEntity> player = findNearestPlayer();
        if (player.isEmpty()) {
            return false;
        }

        // Check if the bee should follow based on its current state and distance to the player
        double squaredDistance = bee.squaredDistanceTo(player.get());
        return (bee.getNavigation().isIdle() && squaredDistance > 144D) || squaredDistance > 400D;
    }

    @Override
    public boolean shouldContinue() {
        Optional<ServerPlayerEntity> player = findNearestPlayer();
        if (player.isEmpty()) {
            return false;
        }

        double squaredDistance = bee.squaredDistanceTo(player.get());
        return squaredDistance >= 9D; // Continue following if too far
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public void stop() {
        bee.getNavigation().resetRangeMultiplier();
        bee.getNavigation().stop();
    }

    @Override
    public void tick() {
        Optional<ServerPlayerEntity> player = findNearestPlayer();
        if (player.isPresent()) {
            PlayerEntity targetPlayer = player.get();
            bee.getLookControl().lookAt(targetPlayer, 10F, (float) bee.getMaxLookPitchChange());

            if (tick++ % 10 == 0) {
                tick = 0;
                double speed = targetPlayer.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + 1D;
                double squaredDistance = bee.squaredDistanceTo(targetPlayer);

                if (squaredDistance > 256D) {
                    speed *= 10D;
                }

                // Bee moves towards the player
                if (squaredDistance < 1024D) {
                    bee.getNavigation().setRangeMultiplier(32F);
                    bee.getNavigation().startMovingTo(targetPlayer, speed);
                } else {
                    // Random movement near player if too far
                    for (int i = 0; i < 10; i++) {
                        int dx = bee.getRandom().nextInt(5) - 2;
                        int dy = bee.getRandom().nextInt(2) + 1;
                        int dz = bee.getRandom().nextInt(5) - 2;
                        BlockPos offset = new BlockPos(dx, dy, dz);

                        if (bee.getWorld().isSpaceEmpty(bee, bee.getBoundingBox().offset(offset))) {
                            BlockPos position = targetPlayer.getBlockPos().add(offset);
                            double x = position.getX() + 0.5D;
                            double y = position.getY() + 0.5D;
                            double z = position.getZ() + 0.5D;
                            bee.getNavigation().stop();
                            bee.refreshPositionAndAngles(x, y, z, bee.getYaw(), bee.getPitch());
                            break;
                        }
                    }
                }
            }
        }
    }

    // Helper method to find the nearest player
    private Optional<ServerPlayerEntity> findNearestPlayer() {
        World world = bee.getWorld();
        return (Optional<ServerPlayerEntity>) world.getPlayers().stream()
                .min((p1, p2) -> Double.compare(bee.squaredDistanceTo(p1), bee.squaredDistanceTo(p2)));
    }
}
