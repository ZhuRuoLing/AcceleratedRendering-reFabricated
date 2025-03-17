package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreEnvironment;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 2)
    )
    void modifyGlMajorVersion(int hint, int value, Operation<Void> original) {
        if (CoreEnvironment.BYPASS_FORCE_OPENGL_VERSION) {
            original.call(hint, value);
            return;
        }
        original.call(hint, 4);
    }


    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 3)
    )
    void modifyGlMinorVersion(int hint, int value, Operation<Void> original) {
        if (CoreEnvironment.BYPASS_FORCE_OPENGL_VERSION) {
            original.call(hint, value);
            return;
        }
        original.call(hint, 6);
    }

    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateWindow(IILjava/lang/CharSequence;JJ)J")
    )
    long wrapCreateWindow(int titleEncoded, int width, CharSequence height, long title, long monitor, Operation<Long> original) {
        long hwnd = original.call(titleEncoded, width, height, title, monitor);
        if (CoreEnvironment.BYPASS_FORCE_OPENGL_VERSION) {
            return hwnd;
        }
        if (hwnd == 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer buf = stack.mallocPointer(1);
                int error = GLFW.glfwGetError(buf);
                if (error != GLFW.GLFW_NO_ERROR) {
                    long descPtr = buf.get();
                    String desc = descPtr != 0L ? "" : MemoryUtil.memUTF8(descPtr);
                    String message = "Trying OpenGL version 4.6: GLFW error: [%d]%s".formatted( error, desc);
                    LOGGER.error(message);
                    throw new IllegalStateException(message);
                }
            }
        }
        return hwnd;
    }
}
