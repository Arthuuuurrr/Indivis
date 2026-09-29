import fr.hautecapitale.skillsitems.CapSkillsTourbillonTagAccess152;
import java.util.*;

public class TagAccessHarness {
    static class Named {
        private final Set<String> tags = new LinkedHashSet<>();
        public Collection<String> getCommandTags() { return tags; }
        public boolean removeCommandTag(String tag) { return tags.remove(tag); }
    }
    static class Obf {
        private final Set<String> tags = new LinkedHashSet<>();
        public Collection<String> method_5752() { return tags; }
        public boolean method_5738(String tag) { return tags.remove(tag); }
    }
    static class FieldOnly {
        @SuppressWarnings("unused") private final Set<String> field_6029 = new LinkedHashSet<>();
        Set<String> tags() { return field_6029; }
    }
    static class BasePrivate {
        private final Set<String> commandTags = new LinkedHashSet<>();
        @SuppressWarnings("unused") private Collection<String> getCommandTags() { return commandTags; }
        @SuppressWarnings("unused") private boolean removeCommandTag(String tag) { return commandTags.remove(tag); }
        Set<String> tags() { return commandTags; }
    }
    static class ChildPrivate extends BasePrivate {}

    static void req(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        Named named = new Named();
        named.tags.add("a");
        named.tags.add("b");
        req(CapSkillsTourbillonTagAccess152.hasTag(named, "a"), "named hasTag");
        req(CapSkillsTourbillonTagAccess152.snapshot(named).equals(new LinkedHashSet<>(List.of("a", "b"))), "snapshot");
        req(CapSkillsTourbillonTagAccess152.removeTag(named, "a"), "named remove true");
        req(!CapSkillsTourbillonTagAccess152.removeTag(named, "missing"), "named remove false");

        Obf obf = new Obf();
        obf.tags.add("x");
        req(CapSkillsTourbillonTagAccess152.hasTag(obf, "x"), "obf has");
        req(CapSkillsTourbillonTagAccess152.removeTag(obf, "x"), "obf remove");

        FieldOnly field = new FieldOnly();
        field.tags().add("f");
        req(CapSkillsTourbillonTagAccess152.hasTag(field, "f"), "field has");
        req(CapSkillsTourbillonTagAccess152.removeTag(field, "f"), "field fallback remove");

        ChildPrivate child = new ChildPrivate();
        child.tags().add("p");
        req(CapSkillsTourbillonTagAccess152.hasTag(child, "p"), "inherited private getter");
        req(CapSkillsTourbillonTagAccess152.removeTag(child, "p"), "inherited private remover");

        req(!CapSkillsTourbillonTagAccess152.removeTag(null, "x"), "null player");
        req(!CapSkillsTourbillonTagAccess152.removeTag(named, null), "null tag");

        System.out.println("TAG_ACCESS_HARNESS_OK");
    }
}
