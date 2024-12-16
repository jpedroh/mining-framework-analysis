package com.mojang.minecraft.render;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;
import com.mojang.util.Vec3D;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


public final class Renderer {
    public Minecraft minecraft;

    public float fogColorMultiplier = 1.0F;

    public boolean displayActive = false;

    public float fogEnd = 0.0F;

    public HeldBlock heldBlock;

    public int levelTicks;

    public Entity entity = null;

    public Random random = new Random();

    private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    public float fogRed;

    public float fogBlue;

    public float fogGreen;

    public Renderer(Minecraft var1) {
        minecraft = var1;
        heldBlock = new HeldBlock(var1);
    }

    public void applyBobbing(float var1, boolean enabled) {
        Player var4;
        float var2 = (var4 = minecraft.player).walkDist - var4.walkDistO;
        var2 = var4.walkDist + (var2 * var1);
        float var3 = var4.oBob + ((var4.bob - var4.oBob) * var1);
        float var5 = var4.oTilt + ((var4.tilt - var4.oTilt) * var1);
        if (enabled) {
            GL11.glTranslatef((MathHelper.sin(var2 * ((float) (Math.PI))) * var3) * 0.5F, -Math.abs(MathHelper.cos(var2 * ((float) (Math.PI))) * var3), 0.0F);
            GL11.glRotatef((MathHelper.sin(var2 * ((float) (Math.PI))) * var3) * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos((var2 * ((float) (Math.PI))) + 0.2F) * var3) * 5.0F, 1.0F, 0.0F, 0.0F);
        }
        GL11.glRotatef(var5, 1.0F, 0.0F, 0.0F);
    }

    private FloatBuffer createBuffer(float var1, float var2, float var3, float var4) {
        buffer.clear();
        buffer.put(var1).put(var2).put(var3).put(var4);
        buffer.flip();
        return buffer;
    }

    public final void enableGuiMode() {
        int var1 = minecraft.width * 240 / minecraft.height;
        int var2 = minecraft.height * 240 / minecraft.height;
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, var1, var2, 0.0D, 100.0D, 300.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
    }

    public Vec3D getPlayerVector(float var1) {
        Player var4;
        float var2 = (var4 = minecraft.player).xo + ((var4.x - var4.xo) * var1);
        float var3 = var4.yo + ((var4.y - var4.yo) * var1);
        float var5 = var4.zo + ((var4.z - var4.zo) * var1);
        return new Vec3D(var2, var3, var5);
    }

    public void hurtEffect(float var1) {
        Player var3;
        float var2 = (var3 = minecraft.player).hurtTime - var1;
        if (var3.health <= 0) {
            var1 += var3.deathTime;
            GL11.glRotatef(40.0F - (8000.0F / (var1 + 200.0F)), 0.0F, 0.0F, 1.0F);
        }
        if (var2 >= 0.0F) {
            var2 = MathHelper.sin(((((var2 /= var3.hurtDuration) * var2) * var2) * var2) * ((float) (Math.PI)));
            var1 = var3.hurtDir;
            GL11.glRotatef(-var3.hurtDir, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((-var2) * 14.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(var1, 0.0F, 1.0F, 0.0F);
        }
    }

    public final void setLighting(boolean var1) {
        if (!var1) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LIGHT0);
        } else {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_LIGHT0);
            GL11.glEnable(2903);
            GL11.glColorMaterial(1032, 5634);
            float var4 = 0.7F;
            float var2 = 0.3F;
            Vec3D var3 = new Vec3D(0.0F, -1.0F, 0.5F).normalize();
            GL11.glLight(16384, 4611, createBuffer(var3.x, var3.y, var3.z, 0.0F));
            GL11.glLight(16384, 4609, createBuffer(var2, var2, var2, 1.0F));
            GL11.glLight(16384, 4608, createBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GL11.glLightModel(2899, createBuffer(var4, var4, var4, 1.0F));
        }
    }

    public void updateFog() {
        Level var1 = minecraft.level;
        Player var2 = minecraft.player;
        GL11.glFog(2918, createBuffer(fogRed, fogBlue, fogGreen, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Block var5;
        if (((var5 = Block.blocks[var1.getTile(((int) (var2.x)), ((int) (var2.y + 0.12F)), ((int) (var2.z)))]) != null) && (var5.getLiquidType() != LiquidType.notLiquid)) {
            LiquidType var6 = var5.getLiquidType();
            GL11.glFogi(2917, 2048);
            float var3;
            float var4;
            float var7;
            float var8;
            if (var6 == LiquidType.water) {
                GL11.glFogf(2914, 0.1F);
                var7 = 0.4F;
                var8 = 0.4F;
                var3 = 0.9F;
                GL11.glLightModel(2899, createBuffer(var7, var8, var3, 1.0F));
            } else if (var6 == LiquidType.lava) {
                GL11.glFogf(2914, 2.0F);
                var7 = 0.4F;
                var8 = 0.3F;
                var3 = 0.3F;
                GL11.glLightModel(2899, createBuffer(var7, var8, var3, 1.0F));
            }
        } else {
            GL11.glFogi(2917, 9729);
            GL11.glFogf(2915, 0.0F);
            GL11.glFogf(2916, fogEnd);
            GL11.glLightModel(2899, createBuffer(1.0F, 1.0F, 1.0F, 1.0F));
        }
        GL11.glEnable(2903);
        GL11.glColorMaterial(1028, 4608);
    }
}