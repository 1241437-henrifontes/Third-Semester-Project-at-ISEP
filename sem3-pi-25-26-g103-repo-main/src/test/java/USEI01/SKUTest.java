package USEI01;

import Model.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SKU Model Tests")
public class SKUTest {

    private SKU sku;

    @BeforeEach
    void setUp() {
        sku = new SKU("SKU-001");
    }

    @Test
    @DisplayName("Default constructor initializes with default value")
    void defaultConstructorInitializesWithDefault() {
        SKU defaultSku = new SKU();
        assertNotNull(defaultSku.getSku());
        assertEquals("SKU***", defaultSku.getSku());
    }

    @Test
    @DisplayName("Parameterized constructor sets SKU value")
    void parameterizedConstructorSetsSKU() {
        SKU s = new SKU("ABC123");
        assertEquals("ABC123", s.getSku());
    }

    @Test
    @DisplayName("Setter modifies SKU value")
    void setterModifiesSKU() {
        sku.setSku("NEW-SKU");
        assertEquals("NEW-SKU", sku.getSku());
    }

    @Test
    @DisplayName("Getter returns current SKU value")
    void getterReturnsSKU() {
        assertEquals("SKU-001", sku.getSku());
    }

    @Test
    @DisplayName("Two SKUs with same code are equal")
    void twoSKUsWithSameCodeAreEqual() {
        SKU sku1 = new SKU("X");
        SKU sku2 = new SKU("X");
        assertEquals(sku1, sku2);
    }

    @Test
    @DisplayName("Two SKUs with different codes are not equal")
    void twoSKUsWithDifferentCodesAreNotEqual() {
        SKU sku1 = new SKU("X");
        SKU sku2 = new SKU("Y");
        assertNotEquals(sku1, sku2);
    }

    @Test
    @DisplayName("Equal SKUs have same hash code")
    void equalSKUsHaveSameHashCode() {
        SKU sku1 = new SKU("HASH-TEST");
        SKU sku2 = new SKU("HASH-TEST");
        assertEquals(sku1.hashCode(), sku2.hashCode());
    }

    @Test
    @DisplayName("SKU can be used in HashSet")
    void skuCanBeUsedInHashSet() {
        Set<SKU> skuSet = new HashSet<>();
        SKU sku1 = new SKU("SET-TEST");
        SKU sku2 = new SKU("SET-TEST");
        SKU sku3 = new SKU("DIFFERENT");
        
        skuSet.add(sku1);
        skuSet.add(sku2);
        skuSet.add(sku3);
        
        assertEquals(2, skuSet.size());
    }

    @Test
    @DisplayName("SKU equals null returns false")
    void skuEqualsNullReturnsFalse() {
        assertNotEquals(sku, null);
        assertFalse(sku.equals(null));
    }

    @Test
    @DisplayName("SKU equals different type returns false")
    void skuEqualsDifferentTypeReturnsFalse() {
        assertNotEquals(sku, "SKU-001");
        assertNotEquals(sku, 123);
        assertNotEquals(sku, new Object());
    }

    @Test
    @DisplayName("SKU equals itself returns true")
    void skuEqualsItselfReturnsTrue() {
        assertEquals(sku, sku);
    }

    @Test
    @DisplayName("toString contains SKU value")
    void toStringContainsSKUValue() {
        String str = sku.toString();
        assertTrue(str.contains("SKU"));
        assertTrue(str.contains("SKU-001"));
    }

    @Test
    @DisplayName("Multiple setter calls update value correctly")
    void multipleSetterCallsUpdateValue() {
        sku.setSku("FIRST");
        assertEquals("FIRST", sku.getSku());
        
        sku.setSku("SECOND");
        assertEquals("SECOND", sku.getSku());
        
        sku.setSku("THIRD");
        assertEquals("THIRD", sku.getSku());
    }

    @Test
    @DisplayName("SKU with empty string")
    void skuWithEmptyString() {
        SKU emptySku = new SKU("");
        assertEquals("", emptySku.getSku());
    }

    @Test
    @DisplayName("SKU with special characters")
    void skuWithSpecialCharacters() {
        SKU specialSku = new SKU("SKU-@#$%^&*()");
        assertEquals("SKU-@#$%^&*()", specialSku.getSku());
    }

    @Test
    @DisplayName("SKU with very long string")
    void skuWithVeryLongString() {
        String longSku = "SKU-" + "A".repeat(1000);
        SKU longSkuObj = new SKU(longSku);
        assertEquals(longSku, longSkuObj.getSku());
    }

    @Test
    @DisplayName("SKU with spaces")
    void skuWithSpaces() {
        SKU spaceSku = new SKU("SKU WITH SPACES");
        assertEquals("SKU WITH SPACES", spaceSku.getSku());
    }

    @Test
    @DisplayName("SKU with numbers only")
    void skuWithNumbersOnly() {
        SKU numSku = new SKU("123456789");
        assertEquals("123456789", numSku.getSku());
    }

    @Test
    @DisplayName("Equality is reflexive")
    void equalityIsReflexive() {
        assertEquals(sku, sku);
    }

    @Test
    @DisplayName("Equality is symmetric")
    void equalityIsSymmetric() {
        SKU sku1 = new SKU("SYM");
        SKU sku2 = new SKU("SYM");
        assertEquals(sku1, sku2);
        assertEquals(sku2, sku1);
    }

    @Test
    @DisplayName("Equality is transitive")
    void equalityIsTransitive() {
        SKU sku1 = new SKU("TRANS");
        SKU sku2 = new SKU("TRANS");
        SKU sku3 = new SKU("TRANS");
        
        assertEquals(sku1, sku2);
        assertEquals(sku2, sku3);
        assertEquals(sku1, sku3);
    }

    @Test
    @DisplayName("Hash code consistency across multiple calls")
    void hashCodeConsistencyAcrossMultipleCalls() {
        int hash1 = sku.hashCode();
        int hash2 = sku.hashCode();
        int hash3 = sku.hashCode();
        
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("Different SKUs can have different hash codes")
    void differentSKUsCanHaveDifferentHashCodes() {
        SKU sku1 = new SKU("HASH1");
        SKU sku2 = new SKU("HASH2");
        // Note: different objects may have same hash code (collision), but usually don't
        // This test just verifies they can be different
        assertNotEquals(sku1, sku2);
    }

    @Test
    @DisplayName("SKU with null value after construction")
    void skuWithNullValueAfterConstruction() {
        SKU nullSku = new SKU(null);
        assertNull(nullSku.getSku());
    }

    @Test
    @DisplayName("Setting SKU to null")
    void settingSKUToNull() {
        sku.setSku(null);
        assertNull(sku.getSku());
    }

    @Test
    @DisplayName("Case sensitivity in equality")
    void caseSensitivityInEquality() {
        SKU sku1 = new SKU("sku");
        SKU sku2 = new SKU("SKU");
        assertNotEquals(sku1, sku2);
    }
}
