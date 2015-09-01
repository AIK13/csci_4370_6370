import static java.lang.System.out;
import static org.junit.Assert.*;

import org.junit.Test;

public class TableTest
{
	public Table createMovieTable()
	{
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        return movie;
	}
	
	public Table createCinemaTable()
	{
		Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
		Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
		Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        return cinema;
	}
	
	public Table createStudioTable()
	{
		Table studio = new Table ("studio", "name address presNo",
                "String String Integer", "name");
		Comparable [] studio0 = { "Fox", "Los_Angeles", 7777 };
        Comparable [] studio1 = { "Universal", "Universal_City", 8888 };
        Comparable [] studio2 = { "DreamWorks", "Universal_City", 9999 };
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
		return studio;
	}
	
	@Test
	public void testProject()
	{
		Table movie = this.createMovieTable();
		Table movie_project = movie.project ("title year");
		
		assertEquals(movie_project.col("title"), 0);
		assertEquals(movie_project.col("year"), 1);
		assertEquals(movie_project.col("length"), -1);
	}
	
	@Test
	public void testSelect()
	{
		Table movie = this.createMovieTable();
		Table movie_select = movie.select(new KeyType("Star_Wars"));
		Comparable[] starWars = movie_select.getTuple(0);
		
		assertEquals(movie_select.getTableLength(), 1);
		assertEquals(starWars[0], "Star_Wars");
	}
	
	@Test
	public void testUnion()
	{
		Table movie = this.createMovieTable();
		Table cinema = this.createCinemaTable();
		Table union = movie.union(cinema);
		
		assertEquals(union.getTableLength(), 5);
	}
	
	@Test
	public void testMinus()
	{
		Table movie = this.createMovieTable();
		Table cinema = this.createCinemaTable();
		Table minus = movie.minus(cinema);
		
		assertEquals(minus.getTableLength(), 2);
	}
	
	@Test
	public void testEquiJoin()
	{
		Table movie = this.createMovieTable();
		Table studio = this.createStudioTable();
		Table eJoin = movie.join("studioName", "name", studio);
		
		Comparable studioName = eJoin.getTuple(0)[eJoin.col("studioName")];
		Comparable name = eJoin.getTuple(0)[eJoin.col("name")];
		
		assertEquals(studioName.compareTo(name), 0);
	}
	
	@Test
	public void testNaturalJoin()
	{
		Table movie = this.createMovieTable();
		Table cinema = this.createCinemaTable();
		Table eJoin = movie.join(cinema);
		
		Comparable title = eJoin.getTuple(0)[eJoin.col("title")];
		Comparable title2 = eJoin.getTuple(0)[eJoin.col("title2")];
		
		assertEquals(title.compareTo(title2), 0);
	}
}
