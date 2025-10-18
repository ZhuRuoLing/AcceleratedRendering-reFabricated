package com.github.argon4w.acceleratedrendering.core.utils;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.minecraft.core.Registry;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class RegistryFilter {

	public static <T> Set<T> filterValues(Registry<T> registry, List<? extends String> values) {
		if (values.isEmpty()) {
			return ReferenceSets.emptySet();
		}

		var patterns = new ReferenceArrayList	<Pattern>	();
		var filtered = new ReferenceOpenHashSet	<T>			();

		for (var pattern : values) {
			patterns.add(Pattern.compile(pattern));
		}

		for		(var key		: registry.keySet()) {
			for	(var pattern	: patterns) {
				if (pattern
						.matcher(key.toString())
						.matches()
				) {
					filtered.add(registry.get(key));
					break;
				}
			}
		}

		return filtered;
	}
}
