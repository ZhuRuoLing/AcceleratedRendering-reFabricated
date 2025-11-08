package com.github.argon4w.acceleratedrendering.compat.iris.mixins.plugin;

import com.github.argon4w.acceleratedrendering.compat.AbstractCompatMixinPlugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.HashMap;
import java.util.List;

public class IrisCompatMixinPlugin extends AbstractCompatMixinPlugin {

	public static final ILogger	LOGGER	= MixinService.getService().getLogger("Accelerated Rendering");

	public static final String	MIXIN_CLASS				= "com.github.argon4w.acceleratedrendering.compat.iris.mixins.vanilla.BufferBuilderMixin";
	public static final String	TARGET_CLASS			= "com.mojang.blaze3d.vertex.BufferBuilder";
	public static final String	TARGET_METHOD			= "fillExtendedData";
	public static final int		TARGET_OPCODE			= Opcodes.BIPUSH;

	public static final int		TARGET_OPERAND_TANGENT	= 6;
	public static final int		TARGET_OPERAND_MID_U	= 14;
	public static final int		TARGET_OPERAND_MID_V	= 10;

	public static final int		FIXED_OPERAND_TANGENT	= 4;
	public static final int		FIXED_OPERAND_MID_U		= 12;
	public static final int		FIXED_OPERAND_MID_V		= 8;

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
			LOGGER.info("- Target class name: \"{}\".",	targetClassName);
			LOGGER.info("- Mixin class name: \"{}\".",	mixinClassName);

			var fixNode = new Int2ObjectOpenHashMap<ObjectIntPair<String>>();

			fixNode.put(TARGET_OPERAND_TANGENT,	ObjectIntPair.of("at_tangent",	FIXED_OPERAND_TANGENT));
			fixNode.put(TARGET_OPERAND_MID_U,	ObjectIntPair.of("midU", 		FIXED_OPERAND_MID_U));
			fixNode.put(TARGET_OPERAND_MID_V,	ObjectIntPair.of("midV",		FIXED_OPERAND_MID_V));

			for (var method : targetClass.methods) {
				if (method.name.equals(TARGET_METHOD)) {
					LOGGER.info("Found target method: ");
					LOGGER.info("- Target method name: \"{}\".",	method.name);
					LOGGER.info("- Target method desc: \"{}\".",	method.desc);

					for (var instruction : method.instructions) {
						if (instruction	instanceof IntInsnNode intNode) {
							var targetOperand	= intNode.operand;
							var targetOpcode	= intNode.getOpcode	();
							var fixedOperand	= fixNode.get		(targetOperand);

							if (		targetOpcode	== TARGET_OPCODE
									&&	fixedOperand != null
							) {
								LOGGER.info("Found instruction of mismatched \"{}\" attribute offset: ",	fixedOperand.left());
								LOGGER.info("- Instruction operand: \"{}\".",								targetOperand);
								LOGGER.info("- Instruction opcode: \"{}\".",								targetOpcode);

								intNode.operand = fixedOperand	.rightInt	();
								fixNode							.remove		(targetOperand);

								LOGGER.info(
										"Successfully modified mismatched \"{}\" attribute offset to \"{}\" from \"{}\".",
										fixedOperand.left		(),
										fixedOperand.rightInt	(),
										targetOperand
								);
							}
						}
					}

					if (!fixNode.isEmpty()) {
						LOGGER.warn("Modification failed.");
						LOGGER.warn("Cannot found mismatched attribute offset, {} left.", fixNode.size());
						LOGGER.warn("Mismatched attribute offset not found: ");

						for (var entry : fixNode.int2ObjectEntrySet()) {
							LOGGER.warn(
									"- \"{}\" with value \"{}\" was supposed to be changed to \"{}\".",
									entry.getIntKey	(),
									entry.getValue	().left		(),
									entry.getValue	().rightInt	()
							);
						}
					}

					return;
				}
			}

			LOGGER.warn("Cannot found target method \"{}\".", TARGET_METHOD);
			LOGGER.warn("Modification failed.");
		}
	}
}
