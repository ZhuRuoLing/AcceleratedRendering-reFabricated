package com.github.argon4w.acceleratedrendering.compat.iris.mixins.plugin;

import com.github.argon4w.acceleratedrendering.compat.AbstractCompatMixinPlugin;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;

public class IrisCompatMixinPlugin extends AbstractCompatMixinPlugin {

	public static final ILogger	LOGGER			= MixinService.getService().getLogger("Accelerated Rendering");

	public static final String	MIXIN_CLASS		= "com.github.argon4w.acceleratedrendering.compat.iris.mixins.vanilla.BufferBuilderMixin";
	public static final String	TARGET_CLASS	= "com.mojang.blaze3d.vertex.BufferBuilder";
	public static final String	TARGET_METHOD	= "fillExtendedData";
	public static final int		TARGET_OPCODE	= Opcodes.BIPUSH;
	public static final int		TARGET_OPERAND	= 6;

	@Override
	protected List<String> getModIDs() {
		return List.of("iris", "oculus");
	}

	@Override
	public void postApply(
			String		targetClassName,
			ClassNode	targetClass,
			String		mixinClassName,
			IMixinInfo	mixinInfo
	) {
		if (		mixinClassName	.equals(MIXIN_CLASS)
				||	targetClassName	.equals(TARGET_CLASS)
		) {
			LOGGER.info("Found target class: ");
			LOGGER.info("- Target class name: \"{}\"",	targetClassName);
			LOGGER.info("- Mixin class name: \"{}\"",	mixinClassName);

			for (var method : targetClass.methods) {
				if (method.name.equals(TARGET_METHOD)) {
					LOGGER.info("Found target method: ");
					LOGGER.info("- Target method name: \"{}\"",	method.name);
					LOGGER.info("- Target method desc: \"{}\"",	method.desc);

					for (var instruction : method.instructions) {
						if (instruction	instanceof IntInsnNode node) {
							var opcode	= node.getOpcode();
							var operand	= node.operand;

							if (		opcode	== TARGET_OPCODE
									&&	operand	== TARGET_OPERAND
							) {
								LOGGER.info("Found instruction of mismatched at_tangent attribute offset: ");
								LOGGER.info("- Instruction opcode: \"{}\"",		opcode);
								LOGGER.info("- Instruction operand: \"{}\"",	operand);

								node.operand = 4;
								LOGGER.info("Successfully modified mismatched at_tangent attribute offset from \"{}\" to 4.", operand);

								return;
							}
						}
					}

					LOGGER.warn("Cannot found mismatched at_tangent attribute offset.");
					LOGGER.warn("Modification failed.");

					return;
				}
			}

			LOGGER.warn("Cannot found target method \"{}\".", TARGET_METHOD);
			LOGGER.warn("Modification failed.");
		}
	}
}
