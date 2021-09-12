import java.util.ArrayList ;

public class TestMST {

  public static void main(String[] args) {
    ArrayList<City> cities ;
    MST mst ;
    cities = smallSetOfCities() ;
    mst = new MST(cities) ;
    System.out.println("Calculate weight of MST automatically: " + mst.weight()) ;
    System.out.println("Calculate weight of MST manually: " + (6*(Math.sqrt(2.0)/2.0)+2*1.0)) ;
  }

  public static ArrayList<City> smallSetOfCities() {
    ArrayList<City> cities = new ArrayList<City>() ;
    cities.add(new City("a",1.0,1.0)) ;
    cities.add(new City("b",2.0,1.0)) ;
    cities.add(new City("c",1.0,2.0)) ;
    cities.add(new City("d",2.0,2.0)) ;
    cities.add(new City("e",1.5,1.5)) ;
    cities.add(new City("f",3.0,1.0)) ;
    cities.add(new City("g",2.5,2.5)) ;
    cities.add(new City("h",2.0,3.0)) ;
    cities.add(new City("i",1.0,3.0)) ;
    return cities ;
  }

}
