package com.indemnity83.irontanks.core;

/**
 * How a bottle right-click on a tank ended. The point of the three-way split is the difference between
 * <em>we refused</em> and <em>this was never ours</em>: a loader that reports both as "not handled"
 * lets the client fall through to the item's own use behavior, so a player whose potion the tank
 * refused ends up drinking it instead.
 *
 * <ul>
 *   <li>{@link #TRANSFERRED} — a full bottle moved; the tank consumed the interaction.
 *   <li>{@link #REFUSED} — the tank owned the interaction and said no (full, wrong fluid, mixed
 *       column, nothing to give). Nothing else may act on the item, so a loader must map this to a
 *       result that <em>consumes</em> the interaction — not a "failed" one, which the game modes
 *       treat the same as a hand-off and still fall through.
 *   <li>{@link #NOT_HANDLED} — the item is not one the tank knows how to move; the game may handle
 *       it normally.
 * </ul>
 */
public enum BottleOutcome {
    TRANSFERRED,
    REFUSED,
    NOT_HANDLED;

    /**
     * Outcome of depositing a held potion. A potion item carrying no potion contents is not a real
     * potion (another mod's or a broken stack) and stays {@link #NOT_HANDLED} so normal use — drinking
     * — still works; a real potion the tank would not take is {@link #REFUSED}, never a fall-through.
     */
    public static BottleOutcome deposit(boolean hasPotionContents, boolean transferred) {
        if (!hasPotionContents) {
            return NOT_HANDLED;
        }
        return transferred ? TRANSFERRED : REFUSED;
    }

    /**
     * Outcome of drawing a bottle out of the tank with an empty glass bottle. The player aimed at the
     * tank, so the tank owns the interaction either way — an empty or non-water tank is a refusal, not
     * a hand-off that would let the bottle scoop up something the player never aimed at.
     */
    public static BottleOutcome draw(boolean transferred) {
        return transferred ? TRANSFERRED : REFUSED;
    }
}
