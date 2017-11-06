//package com.ibm.db2connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.*;
//import java.lang.*;

public class Six
{

	//global vars
	 public static String name;	
	 public static int patient_num;
	
	
	/**
	 * @param args
	 */
	//Connection connection = null;
	//String driver = "com.ibm.db2.jcc.DB2Driver";
	//String url = "jdbc:db2://localhost:50000/Aggr:user=Swathi;password=Rowthupillu1!;";
	public static void main(String[] args) 
	{
		
		//global vars
		
		
		
		//call funtion1
		InsertValuesIntoDB();
		//GetValuesFromDB();
	}
	
	
	public static void InsertValuesIntoDB()
	{
		System.out.println ("Inside 'InsertValues into database' funtion");
	   
		 patient_num = 1;
		
		
		while(patient_num <2)
		{
		
		// TODO Auto-generated method stub
		Connection connection = null;
		String driver = "com.ibm.db2.jcc.DB2Driver";
		String url = "jdbc:db2://localhost:50000/Anger:user=Swathi;password=Rowthupillu1!;";
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url);
			if(connection != null){
				System.out.println("connected");
				
			}else{
				System.out.println("Failed");
			}
	      
	      Statement stmt = connection.createStatement();
	      
	      //stmt.executeUpdate(" insert into patient_records (id) values ('"+patient_num+"')" );    
	     
	      System.out.println("Please answer the following questions in just YES or NO ");
	      System.out.println("Press '0' to exit "); 
	      
	      try {        
		         
		        	 
		      String yes="yes";
		      String no="no";
		      
		
		      //BASIC DETAILS 
		      System.out.println(" PART 1: BASIC DETAILS ");
		      
		       //QUESTION a =====
		       System.out.print(" Enter NAME : ");
		       BufferedReader br111 = new BufferedReader(new InputStreamReader(System.in));
		       //String name;	       
		       name = br111.readLine();	  
		       stmt.executeUpdate(" insert into patient_records (id, name) values ('"+patient_num+"','"+name+"' )" );
		       //stmt.executeUpdate("update patient_records set name='"+name+"' where id='"+patient_num+"' ");
		       
		       //QUESTION b =====
		       System.out.print(" Enter AGE : ");
			   BufferedReader br112 = new BufferedReader(new InputStreamReader(System.in));
			   String age;	       
			   age = br112.readLine();	         
			   stmt.executeUpdate("update patient_records set age='"+age+"' where name='"+name+"' ");
			      
			   //QUESTION c =====
		       System.out.print(" Enter SEX : ");
			   BufferedReader br113 = new BufferedReader(new InputStreamReader(System.in));
			   String sex;	       
			   sex = br113.readLine();	         
			   stmt.executeUpdate("update patient_records set sex='"+sex+"' where name='"+name+"' ");
			      
			   //QUESTION c =====
		       System.out.print(" Enter TODAY'S DATE in this format yyyy-mm-dd : ");
			   BufferedReader br114 = new BufferedReader(new InputStreamReader(System.in));
			   String dt;	       
			   dt = br114.readLine();	         
			   stmt.executeUpdate("update patient_records set todays_date='"+dt+"' where name='"+name+"' ");
			      
		     
		      
		      //HISTORICAL_ARCHIVE
		      System.out.println(" PART 1: HISTORICAL ARCHIVE ");
		      
		       //QUESTION 1 =====
		       System.out.print("1. Is there any history of aggression? : ");
		       BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		       String ans1;	       
		         ans1 = br1.readLine();	         
		         
		         if( ans1.equals(yes))
		         {
		        	
		        	 stmt.executeUpdate("update patient_records set h_aggr=1 where name='"+name+"' ");
		         }
		         else if(ans1.equals(no))
		         {
		        	 //System.out.println( "entered no");
		        	 stmt.executeUpdate("update patient_records set h_aggr=0 where name='"+name+"' ");
		         }
		         else
		         {
		        	 System.out.println( "Please enter either Yes or No !");
		        	 System.exit(0);
		         }
		         
		         
		       //QUESTION 2 =====
			        System.out.print("2. Is history of aggression specific to acute psychosis ? : ");
				    BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
				    String ans2;	       
				       ans2 = br2.readLine();	         
				         
				         if( ans2.equals(yes))
				         {
				        	
				        	 stmt.executeUpdate("update patient_records set h_aggr_acute_psychosis=1 where name='"+name+"' ");
				         }
				         else if(ans2.equals(no))
				         {
				        	 //System.out.println( "entered no");
				        	 stmt.executeUpdate("update patient_records set h_aggr_acute_psychosis=0 where name='"+name+"' ");
				         }
				         else
				         {
				        	 System.out.println( "Please enter either Yes or No !");
				        	 System.exit(0);
				         }
			      
				         
				         
				     //QUESTION 3 =====
				     System.out.print("3. Is there any history of aggression while on maintenance antipsychotic ? : ");
					 BufferedReader br3 = new BufferedReader(new InputStreamReader(System.in));
					 String ans3;	       
					     ans3 = br3.readLine();	         
					         
					         if( ans3.equals(yes))
					         {
					        	
					        	 stmt.executeUpdate("update patient_records set h_aggr_antipsychotic=1 where name='"+name+"' ");
					         }
					         else if(ans3.equals(no))
					         {
					        	 //System.out.println( "entered no");
					        	 stmt.executeUpdate("update patient_records set h_aggr_antipsychotic=0 where name='"+name+"' ");
					         }
					         else
					         {
					        	 System.out.println( "Please enter either Yes or No !");
					        	 System.exit(0);
					        	 
					         }
					          

					         
					         
					//QUESTION 4 =====
					System.out.print("4. Is there any history of aggression while on maintenance mood stabilizer ? : ");
					BufferedReader br4 = new BufferedReader(new InputStreamReader(System.in));
					String ans4;	       
					   ans4 = br4.readLine();	         
						         
					      if( ans4.equals(yes))
					        {
					        	 stmt.executeUpdate("update patient_records set h_aggr_mood_stabilizer=1 where name='"+name+"' ");
					        }
						  else if(ans4.equals(no))
						    {
						       	 //System.out.println( "entered no");
						       	 stmt.executeUpdate("update patient_records set h_aggr_mood_stabilizer=0 where name='"+name+"' ");
						    }
						  else
						    {
						       	 System.out.println( "Please enter either Yes or No !");
						       	 System.exit(0);
						    }
						         

				//QUESTION 5 =====
				System.out.print("5. Is there any history of severe global personal inadequacy  ? : ");
				BufferedReader br5 = new BufferedReader(new InputStreamReader(System.in));
				String ans5; 
				ans5 = br5.readLine();	         
		        
			      if( ans5.equals(yes))
			        {
			        	 stmt.executeUpdate("update patient_records set h_global_personal_inadequacy = 1 where name='"+name+"' ");
			        }
				  else if(ans5.equals(no))
				    {
				       	// System.out.println( "5. entered no");
				       	 stmt.executeUpdate("update patient_records set h_global_personal_inadequacy = 0 where name='"+name+"' ");
				    }
				  else
				    {
				       	 System.out.println( "Please enter either Yes or No !");
				       	 System.exit(0);
				    }
					      

			      
					//QUESTION 6 =====
					System.out.print("6. Is there any history of aggression specific to institutional context   ? : ");
					BufferedReader br6 = new BufferedReader(new InputStreamReader(System.in));
					String ans6;
					ans6 = br6.readLine();
					
				      if( ans6.equals(yes))
				        {
				        	 stmt.executeUpdate("update patient_records set h_aggr_insti = 1 where name='"+name+"' ");
				        }
					  else if(ans6.equals(no))
					    {
					       	// System.out.println( "6. entered no");
					       	 stmt.executeUpdate("update patient_records set h_aggr_insti = 0  where name='"+name+"' ");
			 			}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }
				         

				      
				      
					//QUESTION 7 =====
					System.out.print("7. Is there any history of non-aggressive SUB ? : ");
					BufferedReader br7 = new BufferedReader(new InputStreamReader(System.in));
					String ans7;
					ans7 = br7.readLine();
						
					   if( ans7.equals(yes))
				        {
					      	 stmt.executeUpdate("update patient_records set h_nonaggr_sub=1 where name='"+name+"' ");
			            }
					  else if(ans7.equals(no))
					    {
						     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonaggr_sub=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }

					   
					   
					   
					//QUESTION 8 =====
					System.out.print("8. Is there any history of non-aggressive SUB specific to acute psychosis ? : ");
					BufferedReader br8 = new BufferedReader(new InputStreamReader(System.in));
					String ans8;
					ans8 = br8.readLine();
							
					   if( ans8.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_nonaggr_acute_psychosis=1 where name='"+name+"' ");
				         }
					  else if(ans8.equals(no))
					    {
							     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonaggr_acute_psychosis=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }		  
					   
					   
					   
					   
					//QUESTION 9 =====
					System.out.print("9. Is there any history of non-aggressive SUB while on maintenance antipsychotic ? : ");
					BufferedReader br9 = new BufferedReader(new InputStreamReader(System.in));
					String ans9;
					ans9 = br9.readLine();
								
					   if( ans9.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_nonaggr_antipsychotic=1 where name='"+name+"' ");
					     }
					  else if(ans9.equals(no))
					    {
								     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonaggr_antipsychotic=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }		  
						   
				   			   
				   
					   
				    //QUESTION 10 =====
					System.out.print("10. Is there any history of non-aggressive SUB while on maintenance mood stabilizer ? : ");
					BufferedReader br10 = new BufferedReader(new InputStreamReader(System.in));
					String ans10;
					ans10 = br10.readLine();
									
					   if( ans10.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_nonaggr_mood_stabilizer=1 where name='"+name+"' ");
					     }
					  else if(ans10.equals(no))
					    {
								     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonaggr_mood_stabilizer=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }		  
							  
					   
					   
			
					   
				    //QUESTION 11 =====
					System.out.print("11. Is there any history of non-aggressive SUB ? : ");
					BufferedReader br11 = new BufferedReader(new InputStreamReader(System.in));
					String ans11;
					ans11 = br11.readLine();
										
					   if( ans11.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_nonaggr_sub_dysreg=1 where name='"+name+"' ");
					     }
					  else if(ans11.equals(no))
					    {
					     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonaggr_sub_dysreg=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					       	 System.exit(0);
					    }		  
						
			
					   
					   
					//QUESTION 12 =====
					System.out.print("12. Is there any history of non-suicidal self-harm  ? : ");
					BufferedReader br12 = new BufferedReader(new InputStreamReader(System.in));
					String ans12;
					ans12 = br12.readLine();
											
					   if( ans12.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_nonsuicidal_selfharm=1 where name='"+name+"' ");
					     }
					  else if(ans12.equals(no))
					    {
					     	 //System.out.println( "entered no");
					       	 stmt.executeUpdate("update patient_records set h_nonsuicidal_selfharm=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					      	 System.exit(0);
					    }			   
						 
					   
					   
					//QUESTION 13 =====
					System.out.print("13. Is there any history of poly-substance abuse ? : ");
					BufferedReader br13 = new BufferedReader(new InputStreamReader(System.in));
					String ans13;
					ans13 = br13.readLine();
												
					   if( ans13.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_polysubstance_abuse=1 where name='"+name+"' ");
					     }
					  else if(ans13.equals(no))
					    {
					     	 //System.out.println( "entered no");
					     	 stmt.executeUpdate("update patient_records set h_polysubstance_abuse=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					     	 System.exit(0);
					    }			   
							 
						   
						   
					//QUESTION 14 =====
					System.out.print("14. Is there any history of sexual promiscuity/heterosexual preoccupation ? : ");
					BufferedReader br14 = new BufferedReader(new InputStreamReader(System.in));
					String ans14;
					ans14 = br14.readLine();
													
					   if( ans14.equals(yes))
					     {
					      	 stmt.executeUpdate("update patient_records set h_sexualprom_heterosex_preoccu=1 where name='"+name+"' ");
					     }
					  else if(ans14.equals(no))
					    {
					     	 //System.out.println( "entered no");
					     	 stmt.executeUpdate("update patient_records set h_sexualprom_heterosex_preoccu=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					     	 System.exit(0);
					    }		   
								 
					
					   
					   
				//STD_CLINICAL_ASSESSMENT
  			    System.out.println(" PART : STANDARD CLINICAL ASSESSMENT ");
					      
 		        //QUESTION 15 =====
				System.out.print("15. Enter score of BPRS item 1 : ");
				BufferedReader br15 = new BufferedReader(new InputStreamReader(System.in));
				String ans15;	       
				ans15 = br15.readLine();	         
				stmt.executeUpdate("update patient_records set bprs_x='"+ans15+"' where name='"+name+"' ");
				
				
				//QUESTION 16 =====
				System.out.print("16. Enter score of BPRS item 2 : ");
				BufferedReader br16 = new BufferedReader(new InputStreamReader(System.in));
				String ans16;	       
				ans16 = br16.readLine();	         
		        stmt.executeUpdate("update patient_records set bprs_y='"+ans16+"' where name='"+name+"' ");
				
				   
				//QUESTION 17 =====
				System.out.print("17. Enter score of BPRS item 3 : ");
				BufferedReader br17 = new BufferedReader(new InputStreamReader(System.in));
				String ans17;	       
				ans17 = br17.readLine();	         
		        stmt.executeUpdate("update patient_records set bprs_z='"+ans17+"' where name='"+name+"' ");
				
				
				//QUESTION 18 =====
				System.out.print("18. Enter score of NAB-S item 1 : ");
				BufferedReader br18 = new BufferedReader(new InputStreamReader(System.in));
				String ans18;	       
				ans18 = br18.readLine();	         
		        stmt.executeUpdate("update patient_records set nab_x='"+ans18+"' where name='"+name+"' ");
				
				
				//QUESTION 19 =====
				System.out.print("19. Enter score of NAB-S item 2 : ");
				BufferedReader br19 = new BufferedReader(new InputStreamReader(System.in));
				String ans19;	       
				ans19 = br19.readLine();	         
		        stmt.executeUpdate("update patient_records set nab_y='"+ans19+"' where name='"+name+"' ");
				
				
				//QUESTION 20 =====
				System.out.print("20. Enter score of NAB-S item 3 : ");
				BufferedReader br20 = new BufferedReader(new InputStreamReader(System.in));
				String ans20;	       
				ans20 = br20.readLine();	         
		        stmt.executeUpdate("update patient_records set nab_z='"+ans20+"' where name='"+name+"' ");
				
				
				//QUESTION 21 =====
				System.out.print("21. Enter AIPSS value : ");
				BufferedReader br21 = new BufferedReader(new InputStreamReader(System.in));
				String ans21;	       
				ans21 = br21.readLine();	         
		        stmt.executeUpdate("update patient_records set aipss='"+ans21+"' where name='"+name+"' ");
				
				
				//QUESTION 22 =====
				System.out.print("22. Enter score of Problem Solving item 1 : ");
				BufferedReader br22 = new BufferedReader(new InputStreamReader(System.in));
				String ans22;	       
				ans22 = br22.readLine();	         
		        stmt.executeUpdate("update patient_records set ps_x='"+ans22+"' where name='"+name+"' ");
				
				
				//QUESTION 23 =====
				System.out.print("23. Enter score of Problem Solving item 2 : ");
				BufferedReader br23 = new BufferedReader(new InputStreamReader(System.in));
				String ans23;	       
				ans23 = br23.readLine();         
		        stmt.executeUpdate("update patient_records set ps_y='"+ans23+"' where name='"+name+"' ");
				
				
		        //QUESTION 24 =====
				System.out.print("24. Enter score of Problem Solving item 3 : ");
				BufferedReader br24 = new BufferedReader(new InputStreamReader(System.in));
				String ans24;	       
				ans24 = br24.readLine();         
		        stmt.executeUpdate("update patient_records set ps_z='"+ans24+"' where name='"+name+"' ");
				
		        //QUESTION 25 =====
				System.out.print("25. Enter score of Sociopathy Screen- HARE item 1 : ");
				BufferedReader br25 = new BufferedReader(new InputStreamReader(System.in));
				String ans25;	       
				ans25 = br25.readLine();         
		        stmt.executeUpdate("update patient_records set hare_x='"+ans25+"' where name='"+name+"' ");
				
		        //QUESTION 26 =====
				System.out.print("26. Enter score of Sociopathy Screen- HARE item 2 : ");
				BufferedReader br26 = new BufferedReader(new InputStreamReader(System.in));
				String ans26;	       
				ans26 = br26.readLine();         
		        stmt.executeUpdate("update patient_records set hare_y='"+ans26+"' where name='"+name+"' ");
				
		        //QUESTION 27 =====
				System.out.print("27. Enter score of Sociopathy Screen- HARE item 3 : ");
				BufferedReader br27 = new BufferedReader(new InputStreamReader(System.in));
				String ans27;	       
				ans27 = br27.readLine();         
		        stmt.executeUpdate("update patient_records set hare_z='"+ans27+"' where name='"+name+"' ");
				
		        
		        System.out.println("Questionnaire Done ! "); 
		        
		        GetValuesFromDB();
		        

		        //enter multiple patient records?
		        System.out.println( "Do you want to enter info of another patient? Press yes/no only");
		        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		        String ans;	       
		         ans = br.readLine();
		        if( ans.equals(yes))
		        {
		        	
		          patient_num++;
		        }
		        else
		        {
		        	System.out.println( "THANK YOU !");
		        	break;
		        }
		        
		        
		         
	       } //end of second try
	      catch (IOException e) {
	           System.out.println("Error!");
	           System.exit(1);
	         }
	       
     
      
	}//end of first try
	catch (ClassNotFoundException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//patient_num++;
	
	
	
   }//end of while stmt
		
		System.out.println("Done"); 
		
}//end of FUNCTION InsertValuesIntoDB()
	
	
	
// GET VALUES FROM DB FUNCTION ====================
	
	public static void GetValuesFromDB () 
	{
				
			System.out.println ("Inside GetValues from database funtion");
			    
		
			// TODO Auto-generated method stub
			Connection connection = null;
			String driver = "com.ibm.db2.jcc.DB2Driver";
			String url = "jdbc:db2://localhost:50000/Anger:user=Swathi;password=Rowthupillu1!;";
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url);
				if(connection != null){
					System.out.println("connected");
					
				}else{
					System.out.println("Failed");
				}
		      //Probability of aggression independent of active psychosis
		      Statement stmt1 = connection.createStatement();
		      ResultSet result = stmt1.executeQuery
					("SELECT h_aggr,h_aggr_acute_psychosis,h_aggr_antipsychotic,h_aggr_mood_stabilizer," +
							" h_global_personal_inadequacy,h_aggr_insti," +
							" h_nonaggr_sub,h_nonaggr_acute_psychosis,h_nonaggr_antipsychotic, h_nonaggr_mood_stabilizer," +
							" h_nonaggr_sub_dysreg, h_nonsuicidal_selfharm, h_polysubstance_abuse, h_sexualprom_heterosex_preoccu," +
							" bprs_x, bprs_y, bprs_z, nab_x, nab_y, nab_z, ps_x, ps_y, ps_z, hare_x, hare_y, hare_z"+
							" FROM patient_records");

		      System.out.println("Got results from hist_arch:");
		      while(result.next())    // process results one row at a time
		      {
		  

	
		   // for P(aggr indep. of active psychosis) 	  
		      int h_aggr; //1
		      if(result.getInt("h_aggr") == 1)
		      {
		    	  h_aggr = 1;
		      }
		      else
		      {
		    	  h_aggr = 0;
		      }
		      
		      int h_aggr_acutepsych; //0
		      if(result.getInt("h_aggr_acute_psychosis")== 0)
		      {
		    	  h_aggr_acutepsych = 1;
		      }
		      else
		      {
		    	  h_aggr_acutepsych = 0;
		      }
		      
		      int h_aggr_antipsych; //1
		      if(result.getInt("h_aggr_antipsychotic") == 1)
		      {
		    	  h_aggr_antipsych = 1;
		      }
		      else
		      {
		    	  h_aggr_antipsych = 0;
		      }
		      
		      int h_aggr_moodstblzr; //1
		      if(result.getInt(4) == 1)
		      {
		    	  h_aggr_moodstblzr = 1;
		      }
		      else
		      {
		    	  h_aggr_moodstblzr = 0;
		      }
		     
		      
		     		      
		    // for P(r/o social skill deficit)  
		      int h_severe_global_personal_inad ; //0
		      if(result.getInt("h_global_personal_inadequacy")== 0)
		      {
		    	  //System.out.println("5: entered 0\n");
		    	  h_severe_global_personal_inad = 1;
		      }
		      else
		      {
		    	  h_severe_global_personal_inad = 0;
		      }
		      
		      int h_aggr_insti_context ; //0
		      if(result.getInt("h_aggr_insti") == 0)
		      {
		    	  //System.out.println("6: entered 0\n");
		    	  h_aggr_insti_context = 1;
		      }
		      else
		      {
		    	  h_aggr_insti_context = 0;
		      }

		      
		    //for P(r/o socially unacceptable behavior)  
		      int h_nonaggr_sub ;//0
		      if(result.getInt("h_nonaggr_sub")== 0)
		      {
		    	  //System.out.println("1: entered 0\n");
		    	  h_nonaggr_sub = 1;
		      }
		      else
		      {
		    	  h_nonaggr_sub = 0;
		      }
		      
		      int h_nonaggr_acpsych ; //1
		      if(result.getInt("h_nonaggr_acute_psychosis") == 1)
		      {
		    	  //System.out.println("2: entered 1\n");
		    	  h_nonaggr_acpsych = 1;
		      }
		      else
		      {
		    	  h_nonaggr_acpsych = 0;
		      }
		     
		      int h_nonaggr_maintenance_antipsych ; //0
		      if(result.getInt("h_nonaggr_antipsychotic")== 0)
		      {
		    	  //System.out.println("3: entered 0\n");
		    	  h_nonaggr_maintenance_antipsych = 1;
		      }
		      else
		      {
		    	  h_nonaggr_maintenance_antipsych = 0;
		      }
		      
		      int h_nonaggr_moodstblzr ; //0
		      if(result.getInt(10)== 0)
		      {
		    	  //System.out.println("4: entered 0\n");
		    	  h_nonaggr_moodstblzr = 1;
		      }
		      else
		      {
		    	  h_nonaggr_moodstblzr = 0;
		      }
		      

		      
		    //for P(r/o psychophys. dysreg/nultiple systems)
		      int h_nonaggr_sub1 ; //0
		      if(result.getInt("h_nonaggr_sub_dysreg")== 0)
		      {
		    	  //System.out.println("5: entered 0\n");
		    	  h_nonaggr_sub1 = 1;
		      }
		      else
		      {
		    	  h_nonaggr_sub1 = 0;
		      }
		      
		      
		      int h_nonsuicidal_selfharm  ; //0
		      if(result.getInt("h_nonsuicidal_selfharm")== 0)
		      {
		    	 // System.out.println("6: entered 0\n");
		    	  h_nonsuicidal_selfharm = 1;
		      }
		      else
		      {
		    	  h_nonsuicidal_selfharm = 0;
		      }
		      
		      
		      int h_polysub_abuse  ; //0
		      if(result.getInt("h_polysubstance_abuse")== 0)
		      {
		    	  //System.out.println("7: entered 0\n");
		    	  h_polysub_abuse = 1;
		      }
		      else
		      {
		    	  h_polysub_abuse = 0;
		      }
		      
		      
		      int h_sexualprom_hetsex_preoccu ; //0
		      if(result.getInt("h_sexualprom_heterosex_preoccu")== 0)
		      {
		    	 // System.out.println("8: entered 0\n");
		    	  h_sexualprom_hetsex_preoccu = 1;
		      }
		      else
		      {
		    	  h_sexualprom_hetsex_preoccu = 0;
		      }


		      
		      
		      //Calculation of intermediate variables for those Historical_Archive probabilities
		      int p_aggr_indep_activepsych= h_aggr + h_aggr_acutepsych + h_aggr_antipsych + h_aggr_moodstblzr;
		      System.out.println("Probability of aggression indepenedent of active psychosis is = " + p_aggr_indep_activepsych);

		      

		      int p_social_skill_deficit= h_severe_global_personal_inad + h_aggr_insti_context ;
		      System.out.println("Probability of r/o social skill deficit is = " + p_social_skill_deficit);
		      
		      

		      int p_socially_unacc_behavior = h_nonaggr_sub + h_nonaggr_acpsych + h_nonaggr_maintenance_antipsych + h_nonaggr_moodstblzr ;
		      System.out.println("Probability of r/o socially unacceptable behavior is = " + p_socially_unacc_behavior);



		      int p_pyschdysreg_multsys = h_nonaggr_sub1 + h_nonsuicidal_selfharm + h_polysub_abuse + h_sexualprom_hetsex_preoccu ;
		      System.out.println("Probability of r/o psychosys.dysreg/multiple systems is = " + p_pyschdysreg_multsys);

		      int his_arc;
		      
		      if((p_aggr_indep_activepsych==4) && (p_social_skill_deficit==2) && (p_socially_unacc_behavior==4) && (p_pyschdysreg_multsys==4))
		      {
		    	   his_arc = 1;
		      }
		      else
		      {
		    	   his_arc = 0;
		      }
		      
		      
		      System.out.println("\n\n\n");
		      
		      
		      //STD CLINICAL ASSESSMENT
		      //bprs calc
		      double b1 = result.getDouble("bprs_x");
		      double b2 = result.getDouble("bprs_y");
		      double b3 = result.getDouble("bprs_z");
		      int bprs;
		      if((b1<50)&& (b2<50) && (b3<50))
		      {
		    	   bprs = 1;// PPD present
		      }
		      else
		      {
		    	   bprs = 0;
		      }
		      
		      
		      //nab-s calc		      
		      double n1 = result.getDouble("nab_x");
		      double n2 = result.getDouble("nab_y");
		      double n3 = result.getDouble("nab_z");
		      int nabs;
		      if((n1>80)&& (n2>80) && (n3>80))
		      {
		    	   nabs = 0; // good neurocognitive skills
		      }
		      else
		      {
		    	   nabs = 1; // PPD present
		      }
		      
		      
		      //problem solving calc
		      double p1 = result.getDouble("ps_x");
		      double p2 = result.getDouble("ps_y");
		      double p3 = result.getDouble("ps_z");
		      int ps;
		      if((p1>10)&& (p2>10) && (p3>10))
		      {
		    	   ps = 0; //good prob solving skills
		      }
		      else
		      {
		    	  ps = 1; //PPD present
		      }
		      
		      
		      //sociopathy screen-hare
		      double h1 = result.getDouble("hare_x");
		      double h2 = result.getDouble("hare_y");
		      double h3 = result.getDouble("hare_z");
		      int hare;
		      if((h1>80)&& (h2>80) && (h3>80))
		      {
		    	   hare = 1;  //PPD present since sociopathy skills present 
		      }
		      else
		      {
		    	   hare = 0; 
		      }
		      
		      int std_cli;
		      if((bprs==1) && (nabs==1) && (ps==1) && (hare==1))
		      {
		    	   std_cli = 1;
		      }
		      else
		      {
		    	   std_cli = 0;
		      }
		      
		      
		      //finalllll decision for the problem type!
		      int prob_type;
		      if((his_arc==1) && (std_cli==1))
		      {
		    	   prob_type = 1;
		      }
		      else
		      {
		    	   prob_type = 0;
		      }
		      
		      
		      //FINAL PROBLEM TYPE PRINT
		      System.out.println("OUTPUT---------------->");
		      if(prob_type==1)
		      {
		    	  System.out.println("PROBLEM TYPE : PSYCHOPHYSIOLOGICAL DYSREGULATION OF ANGER/AGGRESSION PRESENT ");
		      }
		      else
		      {
		    	  System.out.println("PROBLEM TYPE : PSYCHOPHYSIOLOGICAL DYSREGULATION OF ANGER/AGGRESSION not PRESENT ");
		      }
		    	  
		      
		      
		      

		      } //end of While(resultset.next())
		      
		      
		      
		      
		      
		      
		    stmt1.close();







			
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}// end of GETVALUESFROMDB funtion !!

	
	
	
	
}//end of class Five



