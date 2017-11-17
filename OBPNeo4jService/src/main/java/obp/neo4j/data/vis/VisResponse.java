package obp.neo4j.data.vis;

import java.util.Collection;

public class VisResponse 
{
	private Collection<VisEdge> edges;
	private Collection<VisNode> nodes;
	
	public Collection<VisEdge> getEdges() {
		return edges;
	}
	public void setEdges(Collection<VisEdge> edges) {
		this.edges = edges;
	}
	public Collection<VisNode> getNodes() {
		return nodes;
	}
	public void setNodes(Collection<VisNode> nodes) {
		this.nodes = nodes;
	}
}
