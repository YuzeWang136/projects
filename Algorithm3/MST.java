import java.util.ArrayList;
import java.util.HashMap;

public class MST {
	double weight;
	ArrayList<City> tour;
	private Edge[] edgeTo;
	private HashMap<City, Double> distTo;
	private HashMap<City, Boolean> marked;
	public MST(ArrayList<City> cities) {
		weight = 0;
		edgeTo = new Edge[cities.size()*(cities.size()-1)/2];
		marked = new HashMap<City, Boolean>();
		distTo = new HashMap<City, Double>();
		tour = new ArrayList<City>();
		 
		int index = 0;
		for(int i = 0; i < cities.size(); i++) {
			for(int j = i + 1; j < cities.size(); j++) {
				Edge newEdge = new Edge(cities.get(i),cities.get(j),cities.get(i).distance(cities.get(j)));
				edgeTo[index] = newEdge;
				index++;
			}
			marked.put(cities.get(i), false);
			distTo.put(cities.get(i), Double.POSITIVE_INFINITY);
		}
		marked.put(cities.get(0), true);
		distTo.put(cities.get(0), 0.0);
		int v = 0;
		while(v < cities.size()-1) {
			Double min = Double.POSITIVE_INFINITY;
			City newCity = new City("", 0.0, 0.0);
			City oldCity = newCity;
			Edge minEdge = new Edge(newCity, newCity, newCity.distance(newCity));
			
			for(int i = 0; i < edgeTo.length; i++) {
				if(edgeTo[i].weight < min) {
					if(!marked.get(edgeTo[i].v) && marked.get(edgeTo[i].w)) {
						minEdge = edgeTo[i];
						newCity = edgeTo[i].v;
						oldCity = edgeTo[i].w;
						min = edgeTo[i].weight;
					}
					else if(marked.get(edgeTo[i].v) && !marked.get(edgeTo[i].w)) {
						minEdge = edgeTo[i];
						newCity = edgeTo[i].w;
						oldCity = edgeTo[i].v;
						min = edgeTo[i].weight;
					}
				}
				
			}
			this.tour.add(newCity);
			marked.put(newCity, true);
			distTo.put(newCity, distTo.get(oldCity)+minEdge.weight);
			weight += minEdge.weight;
			v++;
			
		}
	}
	public double weight() {
		return weight;
	}
	public ArrayList<City> tour(){
		
		return tour;
	}
}

class Edge{
	public City v;
	public City w;
	public double weight;
	public Edge(City v, City w, double weight){
		this.v = v;
		this.w = w;
		this.weight = weight;
	}
	public String toString() {
		return v.name + " " + w.name + " " + weight;
	}
}

