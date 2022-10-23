package com.github.cao.awa.trtr.ref.block.fire;

import com.google.common.collect.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.item.*;
import net.minecraft.server.world.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FireBlock extends net.minecraft.block.FireBlock {
    public static final int field_31093 = 15;
    public static final IntProperty AGE;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES;
    private static final VoxelShape UP_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private final Map<BlockState, VoxelShape> shapesByState;
    private static final int field_31085 = 60;
    private static final int field_31086 = 30;
    private static final int field_31087 = 15;
    private static final int field_31088 = 5;
    private static final int field_31089 = 100;
    private static final int field_31090 = 60;
    private static final int field_31091 = 20;
    private static final int field_31092 = 5;
    private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap<>();
    private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap<>();

    public FireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                                              .with(AGE, 0)
                                              .with(NORTH, false)
                                              .with(EAST, false)
                                              .with(SOUTH, false)
                                              .with(WEST, false)
                                              .with(UP, false));
        this.shapesByState = ImmutableMap.copyOf(this.stateManager.getStates().stream().filter((state) -> {
            return state.get(AGE) == 0;
        }).collect(Collectors.toMap(Function.identity(), FireBlock::getShapeForState)));
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();
        if (state.get(UP)) {
            voxelShape = UP_SHAPE;
        }

        if (state.get(NORTH)) {
            voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
        }

        if (state.get(SOUTH)) {
            voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
        }

        if (state.get(EAST)) {
            voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
        }

        if (state.get(WEST)) {
            voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
        }

        return voxelShape.isEmpty() ? BASE_SHAPE : voxelShape;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return this.canPlaceAt(state, world, pos) ? this.getStateWithAge(world, pos,
                                                                         state.get(AGE)
        ) : Blocks.AIR.getDefaultState();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shapesByState.get(state.with(AGE, 0));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
    }

    protected BlockState getStateForPosition(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
            BlockState blockState2 = this.getDefaultState();
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for (Direction direction : var6) {
                BooleanProperty booleanProperty = DIRECTION_PROPERTIES.get(direction);
                if (booleanProperty != null) {
                    blockState2 = blockState2.with(
                            booleanProperty,
                            this.isFlammable(world.getBlockState(pos.offset(direction)))
                    );
                }
            }

            return blockState2;
        } else {
            return this.getDefaultState();
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || this.areBlocksAroundFlammable(world, pos);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            if (!state.canPlaceAt(world, pos)) {
                world.removeBlock(pos, false);
            }

            BlockState blockState = world.getBlockState(pos.down());
            boolean bl = blockState.isIn(world.getDimension().infiniburn());
            int i = state.get(AGE);
            if (!bl && world.isRaining() && this.isRainingAround(world, pos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
                world.removeBlock(pos, false);
            } else {
                int j = Math.min(15, i + random.nextInt(3) / 2);
                if (i != j) {
                    state = state.with(AGE, j);
                    world.setBlockState(pos, state, 4);
                }

                if (!bl) {
                    if (!this.areBlocksAroundFlammable(world, pos)) {
                        BlockPos blockPos = pos.down();
                        if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || i > 3) {
                            world.removeBlock(pos, false);
                        }

                        return;
                    }

                    if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(pos.down()))) {
                        world.removeBlock(pos, false);
                        return;
                    }
                }

                boolean bl2 = world.hasHighHumidity(pos);
                int k = bl2 ? -50 : 0;
                this.trySpreadingFire(world, pos.east(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.west(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.down(), 250 + k, random, i);
                this.trySpreadingFire(world, pos.up(), 250 + k, random, i);
                this.trySpreadingFire(world, pos.north(), 300 + k, random, i);
                this.trySpreadingFire(world, pos.south(), 300 + k, random, i);
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for(int l = -1; l <= 1; ++l) {
                    for(int m = -1; m <= 1; ++m) {
                        for(int n = -1; n <= 4; ++n) {
                            if (l != 0 || n != 0 || m != 0) {
                                int o = 100;
                                if (n > 1) {
                                    o += (n - 1) * 100;
                                }

                                mutable.set(pos, l, n, m);
                                int p = this.getBurnChance(world, mutable);
                                if (p > 0) {
                                    int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
                                    if (bl2) {
                                        q /= 2;
                                    }

                                    if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.isRainingAround(world, mutable))) {
                                        int r = Math.min(15, i + random.nextInt(5) / 4);
                                        world.setBlockState(mutable, this.getStateWithAge(world, mutable, r), 3);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    protected boolean isRainingAround(World world, BlockPos pos) {
        return world.hasRain(pos) || world.hasRain(pos.west()) || world.hasRain(pos.east()) || world.hasRain(pos.north()) || world.hasRain(pos.south());
    }

    private int getSpreadChance(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? 0 : this.spreadChances.getInt(state.getBlock());
    }

    private int getBurnChance(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? 0 : this.burnChances.getInt(state.getBlock());
    }

    private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, net.minecraft.util.math.random.Random random, int currentAge) {
        int i = this.getSpreadChance(world.getBlockState(pos));
        if (random.nextInt(spreadFactor) < i) {
            BlockState blockState = world.getBlockState(pos);
            if (random.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
                int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                world.setBlockState(pos, this.getStateWithAge(world, pos, j), 3);
            } else {
                world.removeBlock(pos, false);
            }

            Block block = blockState.getBlock();
            if (block instanceof TntBlock) {
                TntBlock.primeTnt(world, pos);
            }
        }

    }

    private BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age) {
        BlockState blockState = getState(world, pos);
        return blockState.isOf(Blocks.FIRE) ?
               blockState.with(AGE, age) : blockState;
    }

    private boolean areBlocksAroundFlammable(BlockView world, BlockPos pos) {
        Direction[] var3 = Direction.values();
        int var4 = var3.length;

        for (Direction direction : var3) {
            if (this.isFlammable(world.getBlockState(pos.offset(direction)))) {
                return true;
            }
        }

        return false;
    }

    private int getBurnChance(WorldView world, BlockPos pos) {
        if (!world.isAir(pos)) {
            return 0;
        } else {
            int i = 0;
            Direction[] var4 = Direction.values();
            int var5 = var4.length;

            for (Direction direction : var4) {
                BlockState blockState = world.getBlockState(pos.offset(direction));
                i = Math.max(
                        this.getBurnChance(blockState),
                        i
                );
            }

            return i;
        }
    }

    protected boolean isFlammable(BlockState state) {
        return this.getBurnChance(state) > 0;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));
    }

    private static int getFireTickDelay(Random random) {
        return 30 + random.nextInt(10);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                AGE,
                NORTH,
                EAST,
                SOUTH,
                WEST,
                UP
        );
    }

    private void registerFlammableBlock(Block block, int burnChance, int spreadChance) {
        this.burnChances.put(block, burnChance);
        this.spreadChances.put(block, spreadChance);
    }

    public static void registerDefaultFlammables() {
        FireBlock fireBlock = (FireBlock)Blocks.FIRE;
        fireBlock.registerFlammableBlock(Blocks.OAK_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_MANGROVE_LOG, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.STRIPPED_MANGROVE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.OAK_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_ROOTS, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.OAK_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.SPRUCE_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BIRCH_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.JUNGLE_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.ACACIA_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.MANGROVE_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BOOKSHELF, 30, 20);
        fireBlock.registerFlammableBlock(Blocks.TNT, 15, 100);
        fireBlock.registerFlammableBlock(Blocks.GRASS, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.FERN, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.DEAD_BUSH, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.SUNFLOWER, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.LILAC, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.ROSE_BUSH, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.PEONY, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.TALL_GRASS, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.LARGE_FERN, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.DANDELION, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.POPPY, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.BLUE_ORCHID, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.ALLIUM, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.AZURE_BLUET, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.RED_TULIP, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.ORANGE_TULIP, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.WHITE_TULIP, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.PINK_TULIP, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.OXEYE_DAISY, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.CORNFLOWER, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.WITHER_ROSE, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.WHITE_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.ORANGE_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.MAGENTA_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.YELLOW_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.LIME_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.PINK_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.GRAY_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.CYAN_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.PURPLE_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BLUE_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BROWN_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.GREEN_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.RED_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BLACK_WOOL, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.VINE, 15, 100);
        fireBlock.registerFlammableBlock(Blocks.COAL_BLOCK, 5, 5);
        fireBlock.registerFlammableBlock(Blocks.HAY_BLOCK, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.TARGET, 15, 20);
        fireBlock.registerFlammableBlock(Blocks.WHITE_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.ORANGE_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.MAGENTA_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.YELLOW_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.LIME_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.PINK_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.GRAY_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.CYAN_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.PURPLE_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.BLUE_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.BROWN_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.GREEN_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.RED_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.BLACK_CARPET, 60, 20);
        fireBlock.registerFlammableBlock(Blocks.DRIED_KELP_BLOCK, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BAMBOO, 60, 60);
        fireBlock.registerFlammableBlock(Blocks.SCAFFOLDING, 60, 60);
        fireBlock.registerFlammableBlock(Blocks.LECTERN, 30, 20);
        fireBlock.registerFlammableBlock(Blocks.COMPOSTER, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.SWEET_BERRY_BUSH, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.BEEHIVE, 5, 20);
        fireBlock.registerFlammableBlock(Blocks.BEE_NEST, 30, 20);
        fireBlock.registerFlammableBlock(Blocks.AZALEA_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.FLOWERING_AZALEA_LEAVES, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.CAVE_VINES, 15, 60);
        fireBlock.registerFlammableBlock(Blocks.CAVE_VINES_PLANT, 15, 60);
        fireBlock.registerFlammableBlock(Blocks.SPORE_BLOSSOM, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.AZALEA, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.FLOWERING_AZALEA, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.BIG_DRIPLEAF, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.BIG_DRIPLEAF_STEM, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.SMALL_DRIPLEAF, 60, 100);
        fireBlock.registerFlammableBlock(Blocks.HANGING_ROOTS, 30, 60);
        fireBlock.registerFlammableBlock(Blocks.GLOW_LICHEN, 15, 100);
    }

    static {
        AGE = Properties.AGE_15;
        NORTH = ConnectingBlock.NORTH;
        EAST = ConnectingBlock.EAST;
        SOUTH = ConnectingBlock.SOUTH;
        WEST = ConnectingBlock.WEST;
        UP = ConnectingBlock.UP;
        DIRECTION_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter((entry) -> {
            return entry.getKey() != Direction.DOWN;
        }).collect(Util.toMap());
        UP_SHAPE = Block.createCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
        EAST_SHAPE = Block.createCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
        SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    }
}