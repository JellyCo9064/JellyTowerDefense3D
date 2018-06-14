package net.there.tutorial.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityJelly extends EntityCreature implements IMob {
	private static final DataParameter<Integer> SLIME_SIZE = EntityDataManager.<Integer>createKey(EntityJelly.class,
			DataSerializers.VARINT);
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;
	private boolean wasOnGround;

	public EntityJelly(World worldIn) {
		super(worldIn);
		this.setMoveForward(10);
		this.moveHelper = new EntityJelly.EntityTutorialMoveHelper(this);
	}

	protected void initEntityAI() {
		this.tasks.addTask(1, new AIJellyMoveToBlock(this, 0.5f, 100000));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SLIME_SIZE, Integer.valueOf(1));
	}

	protected void setSlimeSize(int size, boolean resetHealth) {
		this.dataManager.set(SLIME_SIZE, Integer.valueOf(size));
		this.setSize(1.1f * (float) size, 2.0f * (float) size);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double) (size * 2));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
				.setBaseValue((double) (0.2F + 0.1F * (float) size));

		if (resetHealth) {
			this.setHealth(this.getMaxHealth());
		}

		this.experienceValue = size;
	}

	/**
	 * Returns the size of the slime.
	 */
	public int getSlimeSize() {
		return ((Integer) this.dataManager.get(SLIME_SIZE)).intValue();
	}

	public static void registerFixesSlime(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, EntityJelly.class);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Size", this.getSlimeSize() - 1);
		compound.setBoolean("wasOnGround", this.wasOnGround);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		int i = compound.getInteger("Size");

		if (i < 0) {
			i = 0;
		}

		this.setSlimeSize(i + 1, false);
		this.wasOnGround = compound.getBoolean("wasOnGround");
	}

	public boolean isSmallSlime() {
		return this.getSlimeSize() <= 1;
	}

	protected EnumParticleTypes getParticleType() {
		return EnumParticleTypes.SLIME;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
			this.isDead = true;
		}

		this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
		this.prevSquishFactor = this.squishFactor;
		super.onUpdate();

		if (this.onGround && !this.wasOnGround) {
			int i = this.getSlimeSize();
			if (spawnCustomParticles()) {
				i = 0;
			} // don't spawn particles if it's handled by the implementation itself
			for (int j = 0; j < i * 8; ++j) {
				float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
				float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
				World world = this.world;
				EnumParticleTypes enumparticletypes = this.getParticleType();
				double d0 = this.posX + (double) f2;
				double d1 = this.posZ + (double) f3;
				world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(),
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.squishAmount = -0.5F;
		} else if (!this.onGround && this.wasOnGround) {
			this.squishAmount = 1.0F;
		}

		this.wasOnGround = this.onGround;
		this.alterSquishAmount();
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.6F;
	}

	/**
	 * Gets the amount of time the slime needs to wait between jumps.
	 */
	protected int getJumpDelay() {
		return this.rand.nextInt(20) + 10;
	}

	protected EntityJelly createInstance() {
		return new EntityJelly(this.world);
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (SLIME_SIZE.equals(key)) {
			int i = this.getSlimeSize();
			this.setSize(0.51000005F * (float) i, 0.51000005F * (float) i);
			this.rotationYaw = this.rotationYawHead;
			this.renderYawOffset = this.rotationYawHead;

			if (this.isInWater() && this.rand.nextInt(20) == 0) {
				this.doWaterSplashEffect();
			}
		}

		super.notifyDataManagerChange(key);
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		int i = this.getSlimeSize();

		if (!this.world.isRemote && i > 1 && this.getHealth() <= 0.0F) {
			int j = 2 + this.rand.nextInt(3);

			for (int k = 0; k < j; ++k) {
				float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
				float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
				EntityJelly EntityTutorial = this.createInstance();

				if (this.hasCustomName()) {
					EntityTutorial.setCustomNameTag(this.getCustomNameTag());
				}

				if (this.isNoDespawnRequired()) {
					EntityTutorial.enablePersistence();
				}

				EntityTutorial.setSlimeSize(i / 2, true);
				EntityTutorial.setLocationAndAngles(this.posX + (double) f, this.posY + 0.5D, this.posZ + (double) f1,
						this.rand.nextFloat() * 360.0F, 0.0F);
				this.world.spawnEntity(EntityTutorial);
			}
		}

		super.setDead();
	}

	/**
	 * Applies a velocity to the entities, to push them away from eachother.
	 */
	public void applyEntityCollision(Entity entityIn) {
		super.applyEntityCollision(entityIn);

		if (entityIn instanceof EntityIronGolem && this.canDamagePlayer()) {
			this.dealDamage((EntityLivingBase) entityIn);
		}
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer entityIn) {
		if (this.canDamagePlayer()) {
			this.dealDamage(entityIn);
		}
	}

	protected void dealDamage(EntityLivingBase entityIn) {
		int i = this.getSlimeSize();

		if (this.canEntityBeSeen(entityIn) && this.getDistanceSq(entityIn) < 0.6D * (double) i * 0.6D * (double) i
				&& entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttackStrength())) {
			this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F,
					(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.applyEnchantments(this, entityIn);
		}
	}

	public float getEyeHeight() {
		return 0.625F * this.height;
	}

	/**
	 * Indicates weather the slime is able to damage the player (based upon the
	 * slime's size)
	 */
	protected boolean canDamagePlayer() {
		return !this.isSmallSlime();
	}

	/**
	 * Gets the amount of damage dealt to the player when "attacked" by the slime.
	 */
	protected int getAttackStrength() {
		return this.getSlimeSize();
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_SLIME_HURT : SoundEvents.ENTITY_SLIME_HURT;
	}

	protected SoundEvent getDeathSound() {
		return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_SLIME_DEATH : SoundEvents.ENTITY_SLIME_DEATH;
	}

	protected SoundEvent getSquishSound() {
		return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_SLIME_SQUISH : SoundEvents.ENTITY_SLIME_SQUISH;
	}

	protected Item getDropItem() {
		return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return this.getSlimeSize() == 1 ? LootTableList.ENTITIES_SLIME : LootTableList.EMPTY;
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
		Chunk chunk = this.world.getChunkFromBlockCoords(blockpos);

		if (this.world.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, world)) {
			return false;
		} else {
			if (this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
				Biome biome = this.world.getBiome(blockpos);

				if (biome == Biomes.SWAMPLAND && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F
						&& this.rand.nextFloat() < this.world.getCurrentMoonPhaseFactor()
						&& this.world.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
					return super.getCanSpawnHere();
				}

				if (this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0
						&& this.posY < 40.0D) {
					return super.getCanSpawnHere();
				}
			}

			return false;
		}
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F * (float) this.getSlimeSize();
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the
	 * faceEntity method. This is only currently use in wolves.
	 */
	public int getVerticalFaceSpeed() {
		return 0;
	}

	/**
	 * Returns true if the slime makes a sound when it jumps (based upon the slime's
	 * size)
	 */
	protected boolean makesSoundOnJump() {
		return this.getSlimeSize() > 0;
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jump() {
		this.motionY = 0.41999998688697815D;
		this.isAirBorne = true;
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob spawner,
	 * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
	 * used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		int i = this.rand.nextInt(3);

		if (i < 2 && this.rand.nextFloat() < 0.5F * difficulty.getClampedAdditionalDifficulty()) {
			++i;
		}

		int j = 1 << i;
		this.setSlimeSize(j, true);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	protected SoundEvent getJumpSound() {
		return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_SLIME_JUMP : SoundEvents.ENTITY_SLIME_JUMP;
	}


	static class AIJellyMoveToBlock extends EntityAIMoveToBlock {

		private EntityCreature entity;
		private boolean isAboveDestination;
		private int timeoutCounter;
		private double movementSpeed;

		public AIJellyMoveToBlock(EntityCreature entity, double speedIn, int searchLength) {
			super(entity, speedIn, searchLength);
			movementSpeed = speedIn;
			this.entity = entity;
			this.setMutexBits(5);
			this.destinationBlock = this.entity.getPosition();
			//System.out.println(this.destinationBlock.toString());
			//this.destinationBlock = new BlockPos(0, 0, 0);
		}

		@Override
		public void updateTask() {
			
			if(this.destinationBlock.equals(new BlockPos(0, 0, 0))) {
				this.destinationBlock = this.entity.getPosition();
			}
			
			if (this.entity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0D) {
				this.isAboveDestination = false;
				++this.timeoutCounter;

				if (this.timeoutCounter % 2 == 0) {
					// System.out.println(destinationBlock.toString());
					
					if(isWithinDestination()) {
						updateDestination();
					}
					
					this.entity.getNavigator().tryMoveToXYZ((double) ((float) this.destinationBlock.getX()) + 0.5D,
							(double) (this.destinationBlock.getY() + 1),
							(double) ((float) this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
					EntityJelly jelly = (EntityJelly) entity;
					jelly.moveHelper.action = EntityMoveHelper.Action.STRAFE;
				}
			} else {
				this.isAboveDestination = true;
				--this.timeoutCounter;
			}
		}
		
		private boolean isWithinDestination() {
			return this.entity.getDistanceSq(this.destinationBlock) < 3.5f;
		}
		
		private void updateDestination() {
			this.destinationBlock = this.destinationBlock.add(3, 0, 3);
		}

		@Override
		public boolean shouldExecute() {
			if (this.runDelay > 0) {
				--this.runDelay;
				return false;
			} else {
				this.runDelay = 200 + this.entity.getRNG().nextInt(200);
				return true;
			}
		}

		@Override
		protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
			return true;
		}
	}

	static class EntityTutorialMoveHelper extends EntityMoveHelper {

		private float yRot;
		private int jumpDelay;
		private final EntityJelly jelly;
		private boolean isAggressive;

		public EntityTutorialMoveHelper(EntityJelly jellyIn) {
			super(jellyIn);
			this.jelly = jellyIn;
		}

		/*
		 * public void setSpeed(double speedIn) { this.speed = speedIn; this.action =
		 * EntityMoveHelper.Action.MOVE_TO; }
		 */

		public void onUpdateMoveHelper() {
			if (this.action == EntityMoveHelper.Action.STRAFE) {
				// System.out.println("I'm strafing!");
				float f = (float) this.jelly.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
						.getAttributeValue();
				float f1 = (float) this.speed * f;
				float f2 = this.moveForward;
				float f3 = this.moveStrafe;
				float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

				if (f4 < 1.0F) {
					f4 = 1.0F;
				}

				f4 = f1 / f4;
				f2 = f2 * f4;
				f3 = f3 * f4;
				float f5 = MathHelper.sin(this.jelly.rotationYaw * 0.017453292F);
				float f6 = MathHelper.cos(this.jelly.rotationYaw * 0.017453292F);
				float f7 = f2 * f6 - f3 * f5;
				float f8 = f3 * f6 + f2 * f5;
				PathNavigate pathNavigate = this.jelly.getNavigator();

				if (pathNavigate != null) {
					NodeProcessor nodeprocessor = pathNavigate.getNodeProcessor();

					if (nodeprocessor != null && nodeprocessor.getPathNodeType(this.jelly.world,
							MathHelper.floor(this.jelly.posX + (double) f7), MathHelper.floor(this.jelly.posY),
							MathHelper.floor(this.jelly.posZ + (double) f8)) != PathNodeType.WALKABLE) {
						this.moveForward = 1.0F;
						this.moveStrafe = 0.0F;
						f1 = f;
					}
				}

				this.jelly.setAIMoveSpeed(f1);
				this.jelly.setMoveForward(this.moveForward);
				this.jelly.setMoveStrafing(this.moveStrafe);
				this.action = EntityMoveHelper.Action.WAIT;
			} else if (this.action == EntityMoveHelper.Action.MOVE_TO) {
				// System.out.println("I'm moving!");
				this.action = EntityMoveHelper.Action.WAIT;
				double d0 = this.posX - this.jelly.posX;
				double d1 = this.posZ - this.jelly.posZ;
				double d2 = this.posY - this.jelly.posY;
				double d3 = d0 * d0 + d2 * d2 + d1 * d1;

				if (d3 < 2.500000277905201E-7D) {
					this.jelly.setMoveForward(0.0F);
					return;
				}

				float f9 = (float) (MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
				this.jelly.rotationYaw = this.limitAngle(this.jelly.rotationYaw, f9, 90.0F);
				this.jelly.setAIMoveSpeed((float) (this.speed
						* this.jelly.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

				if (d2 > (double) this.jelly.stepHeight
						&& d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.jelly.width)) {
					this.jelly.getJumpHelper().setJumping();
					this.action = EntityMoveHelper.Action.JUMPING;
				}
			} else if (this.action == EntityMoveHelper.Action.JUMPING) {
				this.jelly.setAIMoveSpeed((float) (this.speed
						* this.jelly.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

				if (this.jelly.onGround) {
					this.action = EntityMoveHelper.Action.WAIT;
				}
			} else {
				this.jelly.setMoveForward(0.0F);
			}
		}
	}

	/**
	 * Called when the slime spawns particles on landing, see onUpdate. Return true
	 * to prevent the spawning of the default particles.
	 */
	protected boolean spawnCustomParticles() {
		return false;
	}

}
