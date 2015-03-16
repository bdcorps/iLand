package org.andengine.util.modifier.ease;

public class EaseBackInCustom implements IEaseFunction {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final float OVERSHOOT_CONSTANT = .6f;

	// ===========================================================
	// Fields
	// ===========================================================

	private static EaseBackInCustom INSTANCE;

	// ===========================================================
	// Constructors
	// ===========================================================

	private EaseBackInCustom() {

	}

	public static EaseBackInCustom getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new EaseBackInCustom();
		}
		return INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public float getPercentage(final float pSecondsElapsed,
			final float pDuration) {
		return EaseBackInCustom.getValue(pSecondsElapsed / pDuration);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static float getValue(final float pPercentage) {
		return pPercentage * pPercentage
				* ((OVERSHOOT_CONSTANT + 1) * pPercentage - OVERSHOOT_CONSTANT);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
