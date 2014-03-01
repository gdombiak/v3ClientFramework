package com.jivesoftware.v3.client.framework.type;

import com.jivesoftware.v3client.framework.AbstractJiveClient;
import com.jivesoftware.v3client.framework.entity.AbstractEntity;
import com.jivesoftware.v3client.framework.entity.ContentEntity;
import com.jivesoftware.v3client.framework.entity.PlaceEntity;
import com.jivesoftware.v3client.framework.type.EntityType;
import com.jivesoftware.v3client.framework.type.EntityTypeLibrary;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EntityTypeLibraryUnitTest {

    @Before
    public void setUp() {
        EntityTypeLibrary.ROOT.clear();
    }

    @Test
    public void testIndividualTypes() throws Exception {
        populateIndividualTypes();
        EntityType entityType;
        entityType = EntityTypeLibrary.ROOT.lookupByType("bar");
        assertEquals("bar", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.lookupByType("baz");
        assertEquals("baz", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.lookupByType("bop");
        assertEquals("bop", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.lookupByType("foo");
        assertEquals("foo", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.lookupByType("notregistered");
        assertNull(entityType);
    }

    @Test
    public void testSingleTypes() throws Exception {
        populateIndividualTypes();
        EntityType entityType;
        entityType = EntityTypeLibrary.ROOT.subLibrary(BarEntity.class).lookupByType("bar");
        assertEquals("bar", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.subLibrary(BarEntity.class).lookupByType("baz");
        assertNull(entityType);
    }

    @Test
    public void testPolymorphicTypes() throws Exception {
        populateIndividualTypes();
        EntityType entityType;
        entityType = EntityTypeLibrary.ROOT.subLibrary(ContentEntity.class).lookupByType("bar");
        assertNull(entityType);
        entityType = EntityTypeLibrary.ROOT.subLibrary(ContentEntity.class).lookupByType("baz");
        assertEquals("baz", entityType.getName());
        entityType = EntityTypeLibrary.ROOT.subLibrary(ContentEntity.class).lookupByType("bop");
        assertNull(entityType);
        entityType = EntityTypeLibrary.ROOT.subLibrary(ContentEntity.class).lookupByType("foo");
        assertNull(entityType);
        entityType = EntityTypeLibrary.ROOT.subLibrary(ContentEntity.class).lookupByType("notregistered");
        assertNull(entityType);
    }

    private void populateIndividualTypes() {
        EntityTypeLibrary.ROOT.add(new EntityType<BarEntity>(BarEntity.class, "bar", "bars"));
        EntityTypeLibrary.ROOT.add(new EntityType<BazEntity>(BazEntity.class, "baz", "bazs"));
        EntityTypeLibrary.ROOT.add(new EntityType<BopEntity>(BopEntity.class, "bop", "bops"));
        EntityTypeLibrary.ROOT.add(new EntityType<FooEntity>(FooEntity.class, "foo", "foos"));
    }

    class BarEntity extends AbstractEntity {

        BarEntity(AbstractJiveClient jiveClient) {
            super(jiveClient, "bar");
        }

        @Override
        protected EntityType<?> lookupResourceType(String resourceName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    class BazEntity extends ContentEntity {

        BazEntity(AbstractJiveClient jiveClient) {
            super(jiveClient, "baz");
        }

        @Override
        protected EntityType<?> lookupResourceType(String resourceName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    class BopEntity extends PlaceEntity {

        BopEntity(AbstractJiveClient jiveClient) {
            super(jiveClient, "bop");
        }

        @Override
        protected EntityType<?> lookupResourceType(String resourceName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    class FooEntity extends AbstractEntity {

        FooEntity(AbstractJiveClient jiveClient) {
            super(jiveClient, "foo");
        }

        @Override
        protected EntityType<?> lookupResourceType(String resourceName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
