package MultigraphADT;

import java.util.Objects;

public class IndexedNode implements Node {
    private final String id;
    private final String name;

    public IndexedNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public int hashCode() {
    	return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        final IndexedNode other = (IndexedNode) obj;
        return Objects.equals(this.getID(), other.getID());
    }
}
