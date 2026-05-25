package Model;

/**
 * A generic immutable pair that stores two related values.
 *
 * @param <V> the type of the first element
 * @param <E> the type of the second element
 *
 * This record is typically used to group two values without
 * creating a dedicated class.
 */
public record Pair<V, E> (V first, E second) {}
