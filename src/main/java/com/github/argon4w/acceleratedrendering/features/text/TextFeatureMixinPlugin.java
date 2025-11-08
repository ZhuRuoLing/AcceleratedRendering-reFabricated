package com.github.argon4w.acceleratedrendering.features.text;

import net.fabricmc.loader.api.FabricLoader;
import org.luaj.vm2.ast.Str;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class TextFeatureMixinPlugin implements IMixinConfigPlugin {
    public static final ILogger LOGGER	= MixinService.getService().getLogger("Accelerated Rendering");
    public static final String RENDER_INT = "method_2025";
    public static final String RENDER_MOJ = "render";
    public static final String RENDER_SIG_INT = "(ZFFLorg/joml/Matrix4f;Lnet/minecraft/class_4588;FFFFI)V";
    public static final String RENDER_SIG_MOJ = "(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V";
    public static final String RENDER_FAST = "renderFast";
    public static final String RENDER_FAST_SIG_INT = "(ZFFLorg/joml/Matrix4f;Lnet/minecraft/class_4588;FFFFILorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V";
    public static final String RENDER_FAST_SIG_MOJ = "(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFILorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V";
    public static final String BAKED_GLYPH_OBF = "net/minecraft/class_382";
    public static final String BAKED_GLYPH_MOJ = "net/minecraft/client/gui/font/glyphs/BakedGlyph";
    public static final boolean IS_OBF_ENV = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals("intermediary");
    public static final String RENDER_FAST_SIG = IS_OBF_ENV ? RENDER_FAST_SIG_INT : RENDER_FAST_SIG_MOJ;
    public static final String BAKED_GLYPH = IS_OBF_ENV ? BAKED_GLYPH_OBF : BAKED_GLYPH_MOJ;

    public static final String CI = Type.getInternalName(CallbackInfo.class);

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!targetClassName.replace(".", "/").equals(BAKED_GLYPH)) return;
        for (MethodNode method : targetClass.methods) {
            boolean nameEquals = method.name.equals(RENDER_MOJ) || method.name.equals(RENDER_INT);
            boolean descEquals = method.desc.equals(RENDER_SIG_INT) || method.desc.equals(RENDER_SIG_MOJ);
            if (!nameEquals || !descEquals) {
                continue;
            }
            int cirLocalSlot = method.maxLocals + 1;
            InsnList instructions = method.instructions;
            ListIterator<AbstractInsnNode> it = instructions.iterator();
            it.add(new TypeInsnNode(Opcodes.NEW, CI));
            it.add(new InsnNode(Opcodes.DUP));
            it.add(new LdcInsnNode("ARMagicInjectionCI_invoke_BakedGlyphMixin;renderFast(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFILorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V"));
            it.add(
                new InsnNode(
                    Opcodes.ICONST_1
                )
            );
            it.add(
                new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    CI,
                    "<init>",
                    "(Ljava/lang/String;Z)V"
                )
            );
            it.add(new VarInsnNode(Opcodes.ASTORE, cirLocalSlot));
            LabelNode continueLabel = new LabelNode();
            int i = 0;
            it.add(new VarInsnNode(Opcodes.ALOAD, i++));
            it.add(new VarInsnNode(Opcodes.ILOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.ALOAD, i++));
            it.add(new VarInsnNode(Opcodes.ALOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.FLOAD, i++));
            it.add(new VarInsnNode(Opcodes.ILOAD, i));
            it.add(new VarInsnNode(Opcodes.ALOAD, cirLocalSlot));
            it.add(
                new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    BAKED_GLYPH,
                    RENDER_FAST,
                    RENDER_FAST_SIG
                )
            );
            it.add(new VarInsnNode(Opcodes.ALOAD, cirLocalSlot));
            it.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                CI,
                "isCancelled",
                "()Z"
            ));
            it.add(
                new JumpInsnNode(
                    Opcodes.IFEQ,
                    continueLabel
                )
            );
            it.add(new InsnNode(Opcodes.RETURN));
            it.add(continueLabel);
        }
    }
}
