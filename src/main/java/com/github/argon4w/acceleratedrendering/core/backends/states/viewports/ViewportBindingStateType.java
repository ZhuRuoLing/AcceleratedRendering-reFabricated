package com.github.argon4w.acceleratedrendering.core.backends.states.viewports;

import com.github.argon4w.acceleratedrendering.core.backends.states.EmptyBindingState;
import com.github.argon4w.acceleratedrendering.core.backends.states.IBindingState;

public enum ViewportBindingStateType {

	IGNORED,
	MOJANG,
	OPENGL;

	public IBindingState create() {
		return create(this);
	}

	public static IBindingState create(ViewportBindingStateType type) {
		return switch (type) {
			case IGNORED	-> EmptyBindingState.INSTANCE;
			case MOJANG		-> new MojangViewportBindingState();
			case OPENGL		-> new OpenGLViewportBindingState();
		};
	}
}
