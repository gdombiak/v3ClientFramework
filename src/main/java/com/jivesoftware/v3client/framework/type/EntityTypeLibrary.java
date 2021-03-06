package com.jivesoftware.v3client.framework.type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public interface EntityTypeLibrary<BASE> {

    RootEntityTypeLibrary ROOT = new RootEntityTypeLibrary();

    EntityType<? extends BASE> lookupByType(String type);

    boolean isPossible(Class<?> type);

    BASE cast(Object o) throws ClassCastException;

    Class<BASE> getSuperType();

    <SUB extends BASE> EntityTypeLibrary<SUB> subLibrary(final Class<SUB> filter);

    public static class RootEntityTypeLibrary implements EntityTypeLibrary<Object> {

        protected final Map<String,EntityType<?>> library = new HashMap<String,EntityType<?>>();
        protected final ReadWriteLock lock = new ReentrantReadWriteLock();

        // For testing only
        public void clear() {
            lock.writeLock().lock();
            try {
                library.clear();
            } finally {
                lock.writeLock().unlock();
            }
        }

        {
            add(new EntityType<>(Void.TYPE, "void", "void"));
        }

        @Override
        public EntityType<?> lookupByType(String type) {
            return lookupImpl(type);
        }

        private EntityType<?> lookupImpl(String type) {
            lock.readLock().lock();
            try {
                return library.get(type);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean isPossible(Class<?> type) {
            return true;
        }

        @Override
        public Object cast(Object o) {
            return o;
        }

        @Override
        public Class<Object> getSuperType() {
            return Object.class;
        }

        @Override
        public <SUB> EntityTypeLibrary<SUB> subLibrary(final Class<SUB> filter) {
            return new EntityTypeLibrary<SUB>() {
                @SuppressWarnings("unchecked")
                @Override
                public EntityType<? extends SUB> lookupByType(String type) {
                    EntityType result = lookupImpl(type);
                    return result != null && filter.isAssignableFrom(result.getType()) ? result : null;
                }

                @Override
                public <SUB1 extends SUB> EntityTypeLibrary<SUB1> subLibrary(Class<SUB1> filter) {
                    return ROOT.subLibrary(filter);
                }

                @Override
                public boolean isPossible(Class<?> type) {
                    return filter.isAssignableFrom(type);
                }

                @Override
                public SUB cast(Object o) throws ClassCastException {
                    return filter.cast(o);
                }

                @Override
                public Class<SUB> getSuperType() {
                    return filter;
                }
            };
        }

        // call once for every entity known
        public void add(EntityType<?> entityType) {
            lock.writeLock().lock();
            try {
                library.put(entityType.getName(), entityType);
            } finally {
                lock.writeLock().unlock();
            }

        }
    }
}
