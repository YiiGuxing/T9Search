package cn.tinkling.t9;

/**
 * Pool of objects.
 *
 * @param <T> The pooled type.
 */
final class Pool<T> {
    private final Object mLock = new Object();

    private final Object[] mPool;

    private int mPoolSize;

    /**
     * Creates a new instance.
     *
     * @param maxPoolSize The max pool size.
     * @throws IllegalArgumentException If the max pool size is less than zero.
     */
    public Pool(int maxPoolSize) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("The max pool size must be > 0");
        }

        mPool = new Object[maxPoolSize];
    }

    /**
     * @return An instance from the pool if such, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public T acquire() {
        synchronized (mLock) {
            if (mPoolSize > 0) {
                final int lastPooledIndex = mPoolSize - 1;
                T instance = (T) mPool[lastPooledIndex];
                mPool[lastPooledIndex] = null;
                mPoolSize--;

                return instance;
            }

            return null;
        }
    }

    /**
     * Release an instance to the pool.
     *
     * @param instance The instance to release.
     * @return Whether the instance was put in the pool.
     * @throws IllegalStateException If the instance is already in the pool.
     */
    public boolean release(T instance) {
        synchronized (mLock) {
            if (isInPool(instance)) {
                throw new IllegalStateException("Already in the pool!");
            }

            if (mPoolSize < mPool.length) {
                mPool[mPoolSize] = instance;
                mPoolSize++;

                return true;
            }

            return false;
        }
    }

    private boolean isInPool(T instance) {
        for (int i = 0; i < mPoolSize; i++) {
            if (mPool[i] == instance) {
                return true;
            }
        }

        return false;
    }
}