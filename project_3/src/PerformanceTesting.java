public class PerformanceTesting
{
	public static void main(String[] args)
	{
		long startTime, endTime;
		double duration;
		Table temp;
		
		for (int n = 500;n <= 16000;n *= 2)
		{
			System.out.println("Tuples - " + n);
			Table tables[] = generateTables(n);
			int studentID = (int)tables[0].getTuple(n / 2)[0];
			
			startTime = System.nanoTime();
			for (Comparable[] tuple : tables[0].tuples)
			{
				if (tuple[0].compareTo(studentID) == 0)
				{
					break;
				}
			}
			endTime = System.nanoTime();
			duration = (endTime - startTime) / 1000000.0;
			System.out.println("Point Select - TableScan");
			System.out.println("Time - " + duration + " ms");
			
			for (int index = 0;index <= 2;index ++)
			{
				startTime = System.nanoTime();
				temp = tables[0].select(new KeyType(studentID), index);
				endTime = System.nanoTime();
				duration = (endTime - startTime) / 1000000.0;
				System.out.print("Point Select - ");
				switch(index)
				{
					case 0:
						System.out.println("TreeMap");
						break;
					case 1:
						System.out.println("BPTreeMap");
						break;
					case 2:
						System.out.println("LinHashMap");
						break;
					default:
						break;
				}
				System.out.println("Time - " + duration + " ms");
			}
			
			startTime = System.nanoTime();
			temp = tables[0].join("id", "studId", tables[1], 0);
			endTime = System.nanoTime();
			duration = (endTime - startTime) / 1000000.0;
			System.out.println("Join - Nested Loop Join");
			System.out.println("Time - " + duration + " ms");
			
			for (int index = 0;index <= 2; index ++)
			{
				startTime = System.nanoTime();
				temp = tables[0].indexJoin(tables[1], index);
				endTime = System.nanoTime();
				System.out.println(temp.tuples.size());
				duration = (endTime - startTime) / 1000000.0;
				System.out.print("Join - ");
				switch(index)
				{
					case 0:
						System.out.println("TreeMap");
						break;
					case 1:
						System.out.println("BPTreeMap");
						break;
					case 2:
						System.out.println("LinHashMap");
						break;
					default:
						break;
				}
				System.out.println("Time - " + duration + " ms");
			}
			
			System.out.println("--------\n");
		}
	}
	
	public static Table [] generateTables(int n)
	{
		TupleGenerator test = new TupleGeneratorImpl ();

        test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);
        
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id",
                           null);
        
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode",
                           null);
        
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crcCode semester",
                           new String [][] {{ "profId", "Professor", "id" },
                                            { "crsCode", "Course", "crsCode" }});
        
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           new String [][] {{ "studId", "Student", "id"},
                                            { "crsCode", "Course", "crsCode" },
                                            { "crsCode semester", "Teaching", "crsCode semester" }});

        String [] tables = { "Student", "Professor", "Course", "Teaching", "Transcript" };
        
        int tups [] = new int [] { n, n, n, n, n };
    
        Comparable [][][] resultTest = test.generate (tups);
        
        Table students = new Table("Student", "id name address status", "Integer String String String", "id");
        Table transcripts = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester");
        
        for (Comparable [] tup : resultTest[0])
        {
        	students.insert(tup);
        }
        
        for (Comparable [] tup : resultTest[4])
        {
        	transcripts.insert(tup);
        }
        
        Table tableArray[] = {students, transcripts};
        return tableArray;
	}
}
