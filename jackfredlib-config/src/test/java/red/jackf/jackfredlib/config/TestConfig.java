package red.jackf.jackfredlib.config;

import net.minecraft.resources.ResourceLocation;
import red.jackf.jackfredlib.api.config.Config;

import java.util.*;

/**
 * JSON:

 String modified = """
     {
         range: 14,
         smol: 3,
         nullable: null,
         test: "my string!",
         pojo: {
            flag1: false,
            flag2: false,
            flag3: true,
         },
         idList: [
            "minecraft:diamond",
            "mekanism:steel",
            "randomconcepts:aquamarine",
         ],
         map: {
            2: 1.5,
            "-7": 0.0,
            3: 300.0,
         },
     }""";
 */

public class TestConfig implements Config<TestConfig> {

    public int range = 14;

    public byte smol = 3;

    public String nullable = null;

    public String test = "my string!";

    public Pojo pojo = new Pojo();

    public List<ResourceLocation> idList = new ArrayList<>(List.of(
            ResourceLocation.withDefaultNamespace("diamond"),
            ResourceLocation.fromNamespaceAndPath("mekanism", "steel"),
            ResourceLocation.fromNamespaceAndPath("randomconcepts", "aquamarine")
    ));

    public Map<String, Double> map = new HashMap<>(Map.of(
            "2", 1.5,
            "-7", 0.0,
            "3", 300.0
    ));

    public static class Pojo {
        public boolean flag1 = false;
        public boolean flag2 = false;
        public boolean flag3 = true;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pojo pojo = (Pojo) o;
            return flag1 == pojo.flag1 && flag2 == pojo.flag2 && flag3 == pojo.flag3;
        }

        @Override
        public int hashCode() {
            return Objects.hash(flag1, flag2, flag3);
        }

        @Override
        public String toString() {
            return "Pojo{" +
                    "flag1=" + flag1 +
                    ", flag2=" + flag2 +
                    ", flag3=" + flag3 +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "range=" + range +
                ", smol=" + smol +
                ", nullable='" + nullable + '\'' +
                ", test='" + test + '\'' +
                ", pojo=" + pojo +
                ", idList=" + idList +
                ", map=" + map +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestConfig that = (TestConfig) o;
        return range == that.range && smol == that.smol && Objects.equals(nullable, that.nullable) && Objects.equals(test, that.test) && Objects.equals(pojo, that.pojo) && Objects.equals(idList, that.idList) && Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, smol, nullable, test, pojo, idList, map);
    }
}
