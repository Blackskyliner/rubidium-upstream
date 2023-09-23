package me.jellysquid.mods.sodium.mixin.compat.fabric;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldSlice.class)
public class WorldSliceMixin implements FabricBlockView {

    @Shadow
    private int originX, originY, originZ;

    @Shadow
    @Final
    private BiomeSlice biomeSlice;

    @Shadow
    @Final
    private @Nullable Int2ReferenceMap<Object>[] blockEntityRenderDataArrays;

    @Override
    public @Nullable Object getBlockEntityRenderData(BlockPos pos) {
        int relX = pos.getX() - this.originX;
        int relY = pos.getY() - this.originY;
        int relZ = pos.getZ() - this.originZ;

        var blockEntityRenderDataMap = this.blockEntityRenderDataArrays[WorldSlice.getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4)];

        if (blockEntityRenderDataMap == null) {
            return null;
        }

        return blockEntityRenderDataMap.get(WorldSlice.getLocalBlockIndex(relX & 15, relY & 15, relZ & 15));
    }

    @Override
    public boolean hasBiomes() {
        return true;
    }

    @Override
    public RegistryEntry<Biome> getBiomeFabric(BlockPos pos) {
        return this.biomeSlice.getBiome(pos.getX(), pos.getY(), pos.getZ());
    }

}
