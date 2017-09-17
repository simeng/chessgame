package yacm.engine;

/*
 * Siste endret av: $Author: epoxy $
 */

/**
 * Verktøy for testing av metoder og klasser
 *
 * @version $Revision: 1.1 $, $Date: 2003/04/25 13:19:14 $
 * @author 	Morten L. Andersen
 */

public class TestTools {
	private static void printHead(String method)
	{
		System.out.println("\t\t/*");
		System.out.println("\t\t * Testing av:");
		System.out.print("\t\t * ");
		System.out.println(method);
		System.out.println("\t\t **\n");
	}

	public static void printTestResult(String method, String[] testVariables, String[][] testResult)
	{

		printHead(method);

		System.out.println("\t\tTestdataSett:");
		System.out.print("\t\tnr\t");
		for(int i = 0; i < testVariables.length; i++)
		{
			System.out.print(testVariables[i]);
			String mellomrom = "";
			for(int j = 0; j < (15 - testVariables[i].length()); j++)
			{
				mellomrom += " ";
			}
			System.out.print(mellomrom);
		}
		System.out.println("Forventet      Resultat");

		for(int setNo = 0; setNo < testResult.length; setNo++)
		{
			System.out.print("\t\t" + (setNo + 1) + "\t");
			for(int i = 0; i < testResult[setNo].length; i++)
			{

				String mellomrom = "";
				int numberOfChar = (i < testResult[setNo].length - 1 ? 15: 0) - testResult[setNo][i].length();
				for(int j = 0; j < numberOfChar; j++)
				{
					mellomrom += " ";
				}

				System.out.print(testResult[setNo][i] + mellomrom);

			}
			System.out.println();
		}
		System.out.println("\t\t**/");
	}
}