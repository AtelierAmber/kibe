package io.github.lucaargolo.kibe.blocks.vacuum

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import io.github.lucaargolo.kibe.utils.BlockScreenHandlerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class VacuumHopper: BlockWithEntity(FabricBlockSettings.of(Material.METAL, MaterialColor.IRON).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque()), AttributeProvider {

    override fun createBlockEntity(view: BlockView?) = VacuumHopperEntity(this)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        player.openHandledScreen(BlockScreenHandlerFactory(this, pos))
        return ActionResult.SUCCESS
    }

    @Suppress("DEPRECATION")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, notify: Boolean) {
        if (!state.isOf(newState.block)) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is Inventory) {
                ItemScatterer.spawn(world, pos, blockEntity as Inventory?)
            }
            super.onStateReplaced(state, world, pos, newState, notify)
        }
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        repeat(4) {
            var x = pos.x.toDouble() + random.nextDouble()
            val y = pos.y.toDouble() + random.nextDouble()
            var z = pos.z.toDouble() + random.nextDouble()

            var velocityX = (random.nextFloat().toDouble() - 0.5) * 0.5
            val velocityY = (random.nextFloat().toDouble() - 0.5) * 0.5
            var velocityZ = (random.nextFloat().toDouble() - 0.5) * 0.5

            val k = random.nextInt(2) * 2 - 1
            if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
                x = pos.x.toDouble() + 0.5 + 0.25 * k.toDouble()
                velocityX = (random.nextFloat() * 2.0f * k.toFloat()).toDouble()
            } else {
                z = pos.z.toDouble() + 0.5 + 0.25 * k.toDouble()
                velocityZ = (random.nextFloat() * 2.0f * k.toFloat()).toDouble()
            }

            world.addParticle(ParticleTypes.PORTAL, x, y, z, velocityX, velocityY, velocityZ)
        }
    }

    override fun isTranslucent(state: BlockState?, view: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun hasSidedTransparency(state: BlockState?): Boolean {
        return true
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, to: AttributeList<*>) {
        (world.getBlockEntity(pos) as? VacuumHopperEntity)?.let {
            to.offer(it)
        }
    }

}