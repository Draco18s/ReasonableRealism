package com.draco18s.hardlib.blockproperties;

import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public class Props {
	public static final PropertyInteger ORE_DENSITY = PropertyInteger.create("ore_density", 0, 15);
	public static final PropertyEnum<MillstoneOrientation> MILL_ORIENTATION = PropertyEnum.<MillstoneOrientation>create("mill_orientation", MillstoneOrientation.class);
	public static final PropertyEnum<AxelOrientation> AXEL_ORIENTATION = PropertyEnum.<AxelOrientation>create("axel_orientation", AxelOrientation.class);
	
	public static enum MillstoneOrientation implements IStringSerializable,IMetaLookup<MillstoneOrientation>
    {
		NONE(0,"none", false, false, 				BlockPos.ORIGIN),
		CENTER(1, "center", false, true, 			BlockPos.ORIGIN),
        NORTH(2, "north", true, false, 				BlockPos.ORIGIN.south()),
        SOUTH(3, "south", true, false, 				BlockPos.ORIGIN.north()),
        EAST(4, "east", true, false, 				BlockPos.ORIGIN.west()),
        WEST(5, "west", true, false, 				BlockPos.ORIGIN.east()),
        SOUTH_EAST(6, "south_east", false, false, 	BlockPos.ORIGIN.north().west()),
        SOUTH_WEST(7, "south_west", false, false, 	BlockPos.ORIGIN.north().east()),
        NORTH_WEST(8, "north_west", false, false, 	BlockPos.ORIGIN.south().east()),
        NORTH_EAST(9, "north_east", false, false, 	BlockPos.ORIGIN.south().west());

        private final int meta;
        private final String name;
        public final boolean canAcceptInput;
        public final boolean canAcceptOutput;
        public final BlockPos offset;

        private MillstoneOrientation(int meta, String name, boolean input, boolean output, BlockPos toCenter) {
            this.meta = meta;
            this.name = name;
            this.canAcceptInput = input;
            this.canAcceptOutput = output;
            this.offset = toCenter;
        }

		@Override
        public String getName() {
            return this.name;
        }

		@Override
		public String getID() {
			return "mill_orientation";
		}

		@Override
		public MillstoneOrientation getByOrdinal(int i) {
			return null;
		}

		@Override
		public String getVariantName() {
			return this.name;
		}

		@Override
		public int getOrdinal() {
			return this.meta;
		}
    }
	
	public static enum AxelOrientation implements IStringSerializable,IMetaLookup<AxelOrientation>
    {
		NONE(0,"none"),
        GEARS(1, "gears"),
        HUB(2, "hub"),
        UP(3, "up");

        private final int meta;
        private final String name;

        private AxelOrientation(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

		@Override
        public String getName() {
            return this.name;
        }

		@Override
		public String getID() {
			return "axel_orientation";
		}

		@Override
		public AxelOrientation getByOrdinal(int i) {
			return null;
		}

		@Override
		public String getVariantName() {
			return this.name;
		}

		@Override
		public int getOrdinal() {
			return this.meta;
		}
    }
}
