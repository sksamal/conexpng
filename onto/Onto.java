// declare the import statements

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.io.*;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class Onto
{

	//declare the global variables
	
	 public static Statement stmt;
	
	 public static String name;	
	 public static int patient_num;
	 
	 public static int p_aggr_indep_activepsych;
 	 public static int p_social_skill_deficit;
 	 public static int p_socially_unacc_behavior;
 	 public static int p_pyschdysreg_multsys;
 	 
 	 public static int bprs;
 	 public static int nabs;
 	 public static int ps;
 	 public static int hare;
 	 public static int his_arc;
 	 public static int std_cli;
 	 public static int prob_type;
 	
	
	
	public static void main(String[] args) 
	{		
		// call to function InsertValuesIntoDB()
		//InsertValuesIntoDB();
		OntoDev();
		
	}
	
	
	public static void InsertValuesIntoDB()
	{
		//Inside 'InsertValues into database' function-->this function takes input from the user
		// and inserts into DB2
	   
		 patient_num = 1;
		
		
		while(patient_num <3)
		{
		
		// TODO Auto-generated method stub
		// connecting to the database
		Connection connection = null;
		String driver = "com.ibm.db2.jcc.DB2Driver";
		String url = "jdbc:db2://localhost:50000/Anger:user=Swathi;password=SwathiSwathi;";
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url);
			if(connection != null){
				System.out.println("connected");
				
			}else{
				System.out.println("Failed");
			}
	      
	      
			 stmt = connection.createStatement();
			 
	     	     
	      System.out.println("Please answer the following questions in just YES or NO ");
	      System.out.println("Press '0' to exit "); 
	      
	      try {        
		         
		        	 
		      String yes="yes";
		      String no="no";
		      
		
		      //BASIC DETAILS 
		      System.out.println(" PART 1: BASIC DETAILS ");
		      System.out.println("------------------------");
		      
		       //QUESTION a =====
		       System.out.print(" Enter NAME : ");
		       BufferedReader br111 = new BufferedReader(new InputStreamReader(System.in));
		       name = br111.readLine();	  
		       stmt.executeUpdate(" insert into patient_records (id, name) values ('"+patient_num+"','"+name+"' )" );
		       
		       
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
		      System.out.println(" PART 2: HISTORICAL ARCHIVE ");
		      System.out.println("----------------------------");
		      
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
					     	
					     	 stmt.executeUpdate("update patient_records set h_sexualprom_heterosex_preoccu=0 where name='"+name+"' ");
						}
					  else
					    {
					       	 System.out.println( "Please enter either Yes or No !");
					     	 System.exit(0);
					    }		   
								 
					
					   
					   
				//STD_CLINICAL_ASSESSMENT
  			    System.out.println(" PART 3: STANDARD CLINICAL ASSESSMENT ");
  			    System.out.println("--------------------------------------");
					      
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
				System.out.print("22. Enter score of Hinting Task item : ");
				BufferedReader br22 = new BufferedReader(new InputStreamReader(System.in));
				String ans22;	       
				ans22 = br22.readLine();	         
		        stmt.executeUpdate("update patient_records set ps_x='"+ans22+"' where name='"+name+"' ");
				
				
		        //QUESTION 23 =====
				System.out.print("23. Enter score of Sociopathy Screen- HARE item 1 : ");
				BufferedReader br25 = new BufferedReader(new InputStreamReader(System.in));
				String ans25;	       
				ans25 = br25.readLine();         
		        stmt.executeUpdate("update patient_records set hare_x='"+ans25+"' where name='"+name+"' ");
				
		        //QUESTION 24 =====
				System.out.print("24. Enter score of Sociopathy Screen- HARE item 2 : ");
				BufferedReader br26 = new BufferedReader(new InputStreamReader(System.in));
				String ans26;	       
				ans26 = br26.readLine();         
		        stmt.executeUpdate("update patient_records set hare_y='"+ans26+"' where name='"+name+"' ");
				
		        //QUESTION 25 =====
				System.out.print("25. Enter score of Sociopathy Screen- HARE item 3 : ");
				BufferedReader br27 = new BufferedReader(new InputStreamReader(System.in));
				String ans27;	       
				ans27 = br27.readLine();         
		        stmt.executeUpdate("update patient_records set hare_z='"+ans27+"' where name='"+name+"' ");
				
		        
		        System.out.println("Questionnaire Done ! "); 
		        
		        //call to function GetValuesFromDB()
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
		        
		        
		         
	       } //end of second try block
	      catch (IOException e) {
	           System.out.println("Error!");
	           System.exit(1);
	         }
	       
     
      
	}//end of first try block
	catch (ClassNotFoundException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
   }//end of while statement
		
		System.out.println("Done"); 
		
}//end of FUNCTION InsertValuesIntoDB()
	
	
	
// GET VALUES FROM DB FUNCTION ====================
	
	public static void GetValuesFromDB () 
	{
			// this function retrieves values from the database as required	
						    
		
			// TODO Auto-generated method stub
			Connection connection = null;
			String driver = "com.ibm.db2.jcc.DB2Driver";
			String url = "jdbc:db2://localhost:50000/Anger:user=Swathi;password=SwathiSwathi;";
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url);
				if(connection != null){
					System.out.println("connected");
					
				}else{
					System.out.println("Failed");
				}
		      
		      Statement stmt1 = connection.createStatement();
		       
		      ResultSet result = stmt1.executeQuery		      
					("SELECT h_aggr,h_aggr_acute_psychosis,h_aggr_antipsychotic,h_aggr_mood_stabilizer," +
							" h_global_personal_inadequacy,h_aggr_insti," +
							" h_nonaggr_sub,h_nonaggr_acute_psychosis,h_nonaggr_antipsychotic, h_nonaggr_mood_stabilizer," +
							" h_nonaggr_sub_dysreg, h_nonsuicidal_selfharm, h_polysubstance_abuse, h_sexualprom_heterosex_preoccu," +
							" bprs_x, bprs_y, bprs_z, nab_x, nab_y, nab_z, ps_x, hare_x, hare_y, hare_z"+
							" FROM patient_records"+
							" WHERE name='"+name+"' ");

		      		      
		      while(result.next())    // process results one row at a time
		      {
		  	
		   	  // compute the various probabilities
		    	  
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
		     
		      
		     		      
		     
		      int h_severe_global_personal_inad ; //0
		      if(result.getInt("h_global_personal_inadequacy")== 0)
		      {
		    	  
		    	  h_severe_global_personal_inad = 1;
		      }
		      else
		      {
		    	  h_severe_global_personal_inad = 0;
		      }
		      
		      int h_aggr_insti_context ; //0
		      if(result.getInt("h_aggr_insti") == 0)
		      {
		    	  
		    	  h_aggr_insti_context = 1;
		      }
		      else
		      {
		    	  h_aggr_insti_context = 0;
		      }

		      
		     
		      int h_nonaggr_sub ;//0
		      if(result.getInt("h_nonaggr_sub")== 0)
		      {
		    	  
		    	  h_nonaggr_sub = 1;
		      }
		      else
		      {
		    	  h_nonaggr_sub = 0;
		      }
		      
		      int h_nonaggr_acpsych ; //1
		      if(result.getInt("h_nonaggr_acute_psychosis") == 1)
		      {
		    	  
		    	  h_nonaggr_acpsych = 1;
		      }
		      else
		      {
		    	  h_nonaggr_acpsych = 0;
		      }
		     
		      int h_nonaggr_maintenance_antipsych ; //0
		      if(result.getInt("h_nonaggr_antipsychotic")== 0)
		      {
		    	  
		    	  h_nonaggr_maintenance_antipsych = 1;
		      }
		      else
		      {
		    	  h_nonaggr_maintenance_antipsych = 0;
		      }
		      
		      int h_nonaggr_moodstblzr ; //0
		      if(result.getInt(10)== 0)
		      {
		    	 
		    	  h_nonaggr_moodstblzr = 1;
		      }
		      else
		      {
		    	  h_nonaggr_moodstblzr = 0;
		      }
		      

		      
		      int h_nonaggr_sub1 ; //0
		      if(result.getInt("h_nonaggr_sub_dysreg")== 0)
		      {
		    	  
		    	  h_nonaggr_sub1 = 1;
		      }
		      else
		      {
		    	  h_nonaggr_sub1 = 0;
		      }
		      
		      
		      int h_nonsuicidal_selfharm  ; //0
		      if(result.getInt("h_nonsuicidal_selfharm")== 0)
		      {
		    	 
		    	  h_nonsuicidal_selfharm = 1;
		      }
		      else
		      {
		    	  h_nonsuicidal_selfharm = 0;
		      }
		      
		      
		      int h_polysub_abuse  ; //0
		      if(result.getInt("h_polysubstance_abuse")== 0)
		      {
		    	  
		    	  h_polysub_abuse = 1;
		      }
		      else
		      {
		    	  h_polysub_abuse = 0;
		      }
		      
		      
		      int h_sexualprom_hetsex_preoccu ; //0
		      if(result.getInt("h_sexualprom_heterosex_preoccu")== 0)
		      {
		    	 
		    	  h_sexualprom_hetsex_preoccu = 1;
		      }
		      else
		      {
		    	  h_sexualprom_hetsex_preoccu = 0;
		      }


		      
		      
		      //Calculation of intermediate variables for those Historical_Archive probabilities
		       p_aggr_indep_activepsych = h_aggr + h_aggr_acutepsych + h_aggr_antipsych + h_aggr_moodstblzr;
		     		      

		       p_social_skill_deficit = h_severe_global_personal_inad + h_aggr_insti_context ;
		      		      

		       p_socially_unacc_behavior = h_nonaggr_sub + h_nonaggr_acpsych + h_nonaggr_maintenance_antipsych + h_nonaggr_moodstblzr ;
		      

		       p_pyschdysreg_multsys = h_nonaggr_sub1 + h_nonsuicidal_selfharm + h_polysub_abuse + h_sexualprom_hetsex_preoccu ;
		      
		       System.out.println("\n\n\n");
		      
		      
		      //STD CLINICAL ASSESSMENT
		      //bprs calculation
		      double b1 = result.getDouble("bprs_x");
		      double b2 = result.getDouble("bprs_y");
		      double b3 = result.getDouble("bprs_z");
		     
		      if((b1>3)&& (b2>3) && (b3>3))
		      {
		    	   bprs = 1;// PPD present
		      }
		      else
		      {
		    	   bprs = 0;
		      }
		      
		      
		      //nab-s calculation		      
		      double n1 = result.getDouble("nab_x");
		      double n2 = result.getDouble("nab_y");
		      double n3 = result.getDouble("nab_z");
		     
		      if((n1<40)&& (n2<40) && (n3<40))
		      {
		    	   nabs = 1; // good neurocognitive skills and so PPD present
		      }
		      else
		      {
		    	   nabs = 0; // PPD not present
		      }
		      
		      
		      //hinting task calculation
		      double p1 = result.getDouble("ps_x");
		      
		      if((p1>15))
		      {
		    	   ps = 1; //good prob solving skills and so PPD present
		      }
		      else
		      {
		    	  ps = 0; //PPD absent
		      }
		      
		      
		      //sociopathy screen-hare
		      double h1 = result.getDouble("hare_x");
		      double h2 = result.getDouble("hare_y");
		      double h3 = result.getDouble("hare_z");
		     
		      if((h1<80)&& (h2<80) && (h3<80))
		      {
		    	   hare = 1;  //PPD present since sociopathy skills not present 
		      }
		      else
		      {
		    	   hare = 0; 
		      }
		      
		      
		    //call to function OntoDev()
		      OntoDev();	
		      
		    		    
		      

		      } //end of While(resultset.next())
		      
		   	      
		      
		    stmt1.close();
		    //stmt.close();

		
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}// end of GETVALUESFROMDB function !!


	
	
	
	
	public static void OntoDev()
	{
		//function creates the ontology and performs the required functions to get the desired o/p
		
		  String uri = "file:/home/ssamal/workspace/conexpng/onto/OntologyPsych.owl";
		  //String uri = "http://www.owl-ontologies.com/OntologyPsych.owl";
		
		  try
		  {
			  
		//OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
	    OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
			  
			  
		// create the entire class structure of the ontology
		OWLNamedClass patientC = owlModel.createOWLNamedClass("Patient");
		// Create subclass (easy version)
		OWLNamedClass basdetC = owlModel.createOWLNamedSubclass("Basic_Details", patientC);
	   
		OWLNamedClass histarchC = owlModel.createOWLNamedSubclass("Historical_Archive", patientC);
		OWLNamedClass paggrIndepAcPsyC = owlModel.createOWLNamedSubclass("P_of_aggr-indep_of_acute_psychosis", histarchC);
		OWLNamedClass aggrC = owlModel.createOWLNamedSubclass("hx_of_aggr", paggrIndepAcPsyC);
		OWLNamedClass aggrAcPsyC = owlModel.createOWLNamedSubclass("hx_of_aggr-acute_psychosis", paggrIndepAcPsyC);
		OWLNamedClass aggrAntiPsyC = owlModel.createOWLNamedSubclass("hx_of_aggr-antipsychotic", paggrIndepAcPsyC);
		OWLNamedClass aggrMoodStabC = owlModel.createOWLNamedSubclass("hx_of_aggr-mood_stabilizer", paggrIndepAcPsyC);
		
		OWLNamedClass proPsyDysregC = owlModel.createOWLNamedSubclass("P_of_r/o-psychosis_dysreg", histarchC);
		OWLNamedClass nonaggrC = owlModel.createOWLNamedSubclass("hx_of_non_aggr_sub", proPsyDysregC);
		OWLNamedClass nonSuicC = owlModel.createOWLNamedSubclass("hx_of_nonsuicidal_self-harm", proPsyDysregC);
		OWLNamedClass polySubC = owlModel.createOWLNamedSubclass("hx_of_polysub_abuse", proPsyDysregC);
		OWLNamedClass sexPromC = owlModel.createOWLNamedSubclass("hx_of_sexual_promiscuity", proPsyDysregC);
		
		OWLNamedClass proSocSkiDefC = owlModel.createOWLNamedSubclass("P_of_r/o-social_skill_deficit", histarchC);
		OWLNamedClass aggrInstiConC = owlModel.createOWLNamedSubclass("hx_of_aggr-institutional_context", proSocSkiDefC);
		OWLNamedClass gloInadeC = owlModel.createOWLNamedSubclass("hx_of_global_personal_inadequacy", proSocSkiDefC);
		
		OWLNamedClass proUnaccBehC = owlModel.createOWLNamedSubclass("P_of_r/o-unacceptable_behavior", histarchC);
		OWLNamedClass nonaggrsubC = owlModel.createOWLNamedSubclass("hx_of_nonaggrsub", proUnaccBehC);
		OWLNamedClass nonaggrMoodStabC = owlModel.createOWLNamedSubclass("hx_of_nonaggrsub-_mood_stabilizer", proUnaccBehC);
		OWLNamedClass nonaggrAcuPsyC = owlModel.createOWLNamedSubclass("hx_of_nonaggrsub-acute_psychosis", proUnaccBehC);
		OWLNamedClass nonaggrAntiPsyC = owlModel.createOWLNamedSubclass("hx_of_nonaggrsub-antipsychotic", proUnaccBehC);
		
						

		OWLNamedClass stdCliAssessC = owlModel.createOWLNamedSubclass("Std_Clinical_Assessment", histarchC);
		OWLNamedClass mentalStatExC = owlModel.createOWLNamedSubclass("mental_status_exam", stdCliAssessC);
		OWLNamedClass bprsC = owlModel.createOWLNamedSubclass("BPRS_items", mentalStatExC);
		OWLNamedClass bprsXC = owlModel.createOWLNamedSubclass("BPRS-x", bprsC);
		OWLNamedClass bprsYC = owlModel.createOWLNamedSubclass("BPRS-y", bprsC);
		OWLNamedClass bprsZC = owlModel.createOWLNamedSubclass("BPRS-z", bprsC);
		
		OWLNamedClass nabC = owlModel.createOWLNamedSubclass("neuropsych_assessment_battery", stdCliAssessC);
		OWLNamedClass nabSC = owlModel.createOWLNamedSubclass("NAB-S", nabC);
		OWLNamedClass nabXC = owlModel.createOWLNamedSubclass("NAB-x", nabSC);
		OWLNamedClass nabYC = owlModel.createOWLNamedSubclass("NAB-y", nabSC);
		OWLNamedClass nabZC = owlModel.createOWLNamedSubclass("NAB-z", nabSC);
		
		OWLNamedClass rabC = owlModel.createOWLNamedSubclass("risk_assessment_battery", stdCliAssessC);
		OWLNamedClass hareC = owlModel.createOWLNamedSubclass("sociopathy_screen-Hare", rabC);
		OWLNamedClass hareXC = owlModel.createOWLNamedSubclass("HARE-x", hareC);
		OWLNamedClass hareYC = owlModel.createOWLNamedSubclass("HARE-y", hareC);
		OWLNamedClass hareZC = owlModel.createOWLNamedSubclass("HARE-z", hareC);
		
		OWLNamedClass socCogBatC = owlModel.createOWLNamedSubclass("social_cognition&skills_battery", stdCliAssessC);
		OWLNamedClass aipssC = owlModel.createOWLNamedSubclass("AIPSS", socCogBatC);
		OWLNamedClass probSolvC = owlModel.createOWLNamedSubclass("Hinting Task", socCogBatC);
		OWLNamedClass psXC = owlModel.createOWLNamedSubclass("PS-x", probSolvC);
				
		
		OWLNamedClass probTypeC = owlModel.createOWLNamedSubclass("Problem_Type", histarchC);
		OWLNamedClass defC = owlModel.createOWLNamedSubclass("Defined_in_terms_of", probTypeC);
		OWLNamedClass keyCharC = owlModel.createOWLNamedSubclass("Key_characteristics", defC);
		OWLNamedClass preCauC = owlModel.createOWLNamedSubclass("Presumptive_cause", defC);
		OWLNamedClass txIndC = owlModel.createOWLNamedSubclass("Tx_indications", defC);
		OWLNamedClass expTxRspnsC = owlModel.createOWLNamedSubclass("Expected_tx_response", defC);
		OWLNamedClass modFacC = owlModel.createOWLNamedSubclass("Moderating_factors", defC);
		OWLNamedClass probDescC = owlModel.createOWLNamedSubclass("Problem_description", probTypeC);
		OWLNamedClass priorityC = owlModel.createOWLNamedSubclass("Priority_level", probTypeC);
		OWLNamedClass longTermC = owlModel.createOWLNamedSubclass("Long_term_goal", probTypeC);
		OWLNamedClass shortTermC = owlModel.createOWLNamedSubclass("Short_term_goal", probTypeC);
		OWLNamedClass keyIndC = owlModel.createOWLNamedSubclass("Key_indicators", probTypeC);
		
		OWLNamedClass intervC = owlModel.createOWLNamedSubclass("Intervention", histarchC);
		OWLNamedClass formulC = owlModel.createOWLNamedSubclass("Formulary", intervC);
		OWLNamedClass primTxC = owlModel.createOWLNamedSubclass("Primary_tx", formulC);
		OWLNamedClass adjTxC = owlModel.createOWLNamedSubclass("Adjunctive_tx", formulC);
		
		OWLNamedClass probPriorityC = owlModel.createOWLNamedSubclass("Problem_Priority", histarchC);
		OWLNamedClass grtEq3C = owlModel.createOWLNamedSubclass("Greater_than_Equals_3", probPriorityC);
		OWLNamedClass lessThan3C = owlModel.createOWLNamedSubclass("Less_than_3", probPriorityC);
		OWLNamedClass txSelecC = owlModel.createOWLNamedSubclass("Tx_selection", lessThan3C);
		OWLNamedClass reqImmedC = owlModel.createOWLNamedSubclass("Requiring_immediate_management", txSelecC);
		OWLNamedClass prefAngerC = owlModel.createOWLNamedSubclass("Pref_for_anger_mgmt_over_dcbt", txSelecC);
		OWLNamedClass prefCounterC = owlModel.createOWLNamedSubclass("Pref_for_countercondi_over_grprelax", txSelecC);
		OWLNamedClass txMonitC = owlModel.createOWLNamedSubclass("Tx_monitoring", lessThan3C);
		OWLNamedClass less24C = owlModel.createOWLNamedSubclass("Last_24_hours", txMonitC);
		OWLNamedClass pastDayC = owlModel.createOWLNamedSubclass("Past_day", txMonitC);
		OWLNamedClass pastWeekC = owlModel.createOWLNamedSubclass("Past_week", txMonitC);
		OWLNamedClass pastMonthC = owlModel.createOWLNamedSubclass("Past_month", txMonitC);
		OWLNamedClass threeMonthC = owlModel.createOWLNamedSubclass("3-month_interval", txMonitC);  	
		
		
		
		
		//to print
		
		//call printClassTree() function to print the class structure	
	    printClassTree(patientC, "");

		// create all the datatype properties for Basic_Details class!
	    OWLDatatypeProperty namePrp = owlModel.createOWLDatatypeProperty("pname");
	    namePrp.setRange(owlModel.getXSDstring());
	    namePrp.setDomain(basdetC);
	    
	    OWLDatatypeProperty agePrp = owlModel.createOWLDatatypeProperty("page");
	    agePrp.setRange(owlModel.getXSDint());
	    agePrp.setDomain(basdetC);
	    
	    OWLDatatypeProperty sexPrp = owlModel.createOWLDatatypeProperty("psex");
	    sexPrp.setRange(owlModel.getXSDint());
	    sexPrp.setDomain(basdetC);
	    
	    //data type prop for the first four probabilities in Historical Archive
	    OWLDatatypeProperty p1Prp = owlModel.createOWLDatatypeProperty("p1");
	    p1Prp.setRange(owlModel.getXSDint());
	    p1Prp.setDomain(paggrIndepAcPsyC);

	    OWLDatatypeProperty p2Prp = owlModel.createOWLDatatypeProperty("p2");
	    p2Prp.setRange(owlModel.getXSDint());
	    p2Prp.setDomain(paggrIndepAcPsyC);
	    
	    OWLDatatypeProperty p3Prp = owlModel.createOWLDatatypeProperty("p3");
	    p3Prp.setRange(owlModel.getXSDint());
	    p3Prp.setDomain(paggrIndepAcPsyC);
	    
	    OWLDatatypeProperty p4Prp = owlModel.createOWLDatatypeProperty("p4");
	    p4Prp.setRange(owlModel.getXSDint());
	    p4Prp.setDomain(paggrIndepAcPsyC);
	    
		// data props for Std_Clinical_Assessment
	    //BPRS-1 items
	    OWLDatatypeProperty bprsPrp = owlModel.createOWLDatatypeProperty("bprsv");
	    bprsPrp.setRange(owlModel.getXSDint());
	    bprsPrp.setDomain(bprsC);
	    
	    OWLDatatypeProperty bprsX1Prp = owlModel.createOWLDatatypeProperty("bprsX1");
	    bprsX1Prp.setRange(owlModel.getXSDint());
	    bprsX1Prp.setDomain(bprsC);
	    
	    OWLDatatypeProperty bprsY1Prp = owlModel.createOWLDatatypeProperty("bprsY1");
	    bprsY1Prp.setRange(owlModel.getXSDint());
	    bprsY1Prp.setDomain(bprsC);
	    
	    OWLDatatypeProperty bprsZ1Prp = owlModel.createOWLDatatypeProperty("bprsZ1");
	    bprsZ1Prp.setRange(owlModel.getXSDint());
	    bprsZ1Prp.setDomain(bprsC);
	    
	    //NAB-S items
	    OWLDatatypeProperty nabPrp = owlModel.createOWLDatatypeProperty("nabv");
	    nabPrp.setRange(owlModel.getXSDint());
	    nabPrp.setDomain(nabC);
	    
	    OWLDatatypeProperty nabX1Prp = owlModel.createOWLDatatypeProperty("nabX1");
	    nabX1Prp.setRange(owlModel.getXSDint());
	    nabX1Prp.setDomain(nabC);
	    
	    OWLDatatypeProperty nabY1Prp = owlModel.createOWLDatatypeProperty("nabY1");
	    nabY1Prp.setRange(owlModel.getXSDint());
	    nabY1Prp.setDomain(nabC);
	    
	    OWLDatatypeProperty nabZ1Prp = owlModel.createOWLDatatypeProperty("nabZ1");
	    nabZ1Prp.setRange(owlModel.getXSDint());
	    nabZ1Prp.setDomain(nabC);
	    
	    //HARE items
	    OWLDatatypeProperty harePrp = owlModel.createOWLDatatypeProperty("harev");
	    harePrp.setRange(owlModel.getXSDint());
	    harePrp.setDomain(hareC);
	    
	    OWLDatatypeProperty hareX1Prp = owlModel.createOWLDatatypeProperty("hareX1");
	    hareX1Prp.setRange(owlModel.getXSDint());
	    hareX1Prp.setDomain(hareC);
	    
	    OWLDatatypeProperty hareY1Prp = owlModel.createOWLDatatypeProperty("hareY1");
	    hareY1Prp.setRange(owlModel.getXSDint());
	    hareY1Prp.setDomain(hareC);
	    
	    OWLDatatypeProperty hareZ1Prp = owlModel.createOWLDatatypeProperty("hareZ1");
	    hareZ1Prp.setRange(owlModel.getXSDint());
	    hareZ1Prp.setDomain(hareC);
	    
	    //Problem Solving items problem_solving_intact
	    OWLDatatypeProperty psPrp = owlModel.createOWLDatatypeProperty("psv");
	    psPrp.setRange(owlModel.getXSDint());
	    psPrp.setDomain(probSolvC);
	    
	    OWLDatatypeProperty psX1Prp = owlModel.createOWLDatatypeProperty("psX1");
	    psX1Prp.setRange(owlModel.getXSDint());
	    psX1Prp.setDomain(probSolvC);
	    
	    OWLDatatypeProperty psY1Prp = owlModel.createOWLDatatypeProperty("psY1");
	    psY1Prp.setRange(owlModel.getXSDint());
	    psY1Prp.setDomain(probSolvC);
	    
	    OWLDatatypeProperty psZ1Prp = owlModel.createOWLDatatypeProperty("psZ1");
	    psZ1Prp.setRange(owlModel.getXSDint());
	    psZ1Prp.setDomain(probSolvC);
	    
	 	   
	    
	    //start creating Individuals and assigning values to them from the answers obtained in the questionnaire
	    RDFIndividual darwin = basdetC.createRDFIndividual(name);
	    darwin.setPropertyValue(p1Prp,p_aggr_indep_activepsych );
	    Integer intValue1 = (Integer) darwin.getPropertyValue(p1Prp);
	   
	    
	    darwin.setPropertyValue(p2Prp,p_social_skill_deficit );
	    Integer intValue2 = (Integer) darwin.getPropertyValue(p2Prp);
	    
	    
	    darwin.setPropertyValue(p3Prp,p_socially_unacc_behavior );
	    Integer intValue3 = (Integer) darwin.getPropertyValue(p3Prp);
	    
	    
	    darwin.setPropertyValue(p4Prp,p_pyschdysreg_multsys);
	    Integer intValue4 = (Integer) darwin.getPropertyValue(p4Prp);
	   
	    
	    
	    if((intValue1==4) && (intValue2==2) && (intValue3==4) && (intValue4==4))
	    {
	    	his_arc = 1;
	    	
	    }
	    else
	    {
	    	his_arc = 0;
	    	
	    }
	    
	    //STD CLINICAL ASSESSMENT calc
	    darwin.setPropertyValue(bprsPrp,bprs );
	    Integer intValue5 = (Integer) darwin.getPropertyValue(bprsPrp);
	  
	    
	    darwin.setPropertyValue(nabPrp,nabs );
	    Integer intValue6 = (Integer) darwin.getPropertyValue(nabPrp);
	   
	    
	    darwin.setPropertyValue(psPrp,ps );
	    Integer intValue7 = (Integer) darwin.getPropertyValue(psPrp);
	   
	    
	    darwin.setPropertyValue(harePrp,hare );
	    Integer intValue8 = (Integer) darwin.getPropertyValue(harePrp);
	   
	    
	    
	    
	      if((bprs==1) && (nabs==1) && (ps==1) && (hare==1))
	      {
	    	   std_cli = 1;
	      }
	      else
	      {
	    	   std_cli = 0;
	      }
	      
	           	      
	      
		    
	      //finalllll decision for the problem type!
	     
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
	    	  System.out.println("Name of the patient:"+name);
	    	  System.out.println("PROBLEM TYPE : Psychophysiological Dysregulation of ANGER/AGGRESSION present ");
	    	  
	    	  //call to PPDTreatmentPlan()
	    	  PPDTreatmentPlan();
	    	  
	    	  System.out.println("LIST of POSSIBLE TREATMENTS -------> ");
	    	  System.out.println("PRIMARY Treatments:  ");
	    	  System.out.println("\t 1. Dialectical cognitive-behavioral therapy   ");
	    	  System.out.println("\t 2. Physical relaxation/stress management therapy   ");
	    	  System.out.println("\t 3. Counterconditioning therapy   ");
	    	  System.out.println("\t 4. Anger management therapy   ");
	    	  System.out.println("ADJUNCTIVE Treatments:  ");
	    	  System.out.println("\t 5. Social skills training    ");
	    	  System.out.println("\t 6. Psychopharmacotherapy to temporarily moderate extreme physiological arousal components    ");
	    	  
	      }
	      else
	      {
	    	  System.out.println("Name of the patient:"+name);
	    	  System.out.println("PROBLEM TYPE : Psychophysiological Dysregulation of ANGER/AGGRESSION NOT present ");
	    	  
	    	  //call to PPDTreatmentPlan()
	    	  PPDTreatmentPlan();
	      }
	    	  
	      
	      
	    
	    
	    
		  }
		  catch (OntologyLoadException e) {
				
				e.printStackTrace();
			}
			System.out.println("done");
		  
	    
	}// end of ONTODEV FUNCTION !!!
	
	

	private static void printClassTree(RDFSClass cls, String indentation) 
	{
		// function to print the entire class structure of the ontology just created
		
		System.out.println(indentation + cls.getName());
		        for (Iterator it = cls.getSubclasses(false).iterator(); it.hasNext();) 
		        {
			           RDFSClass subclass = (RDFSClass) it.next();
			           printClassTree(subclass, indentation + "    ");
			    }
	 }// end of PRINTCLASSTREE FUNCTION!! 


	
	public static void PPDTreatmentPlan()
	{
		//Inside 'PPDTreatmentPlan' function--> just updates the database about the problem type present
		//i.e yes or no !
	    
				
		
		// TODO Auto-generated method stub
		Connection connection = null;
		String driver = "com.ibm.db2.jcc.DB2Driver";
		String url = "jdbc:db2://localhost:50000/Anger:user=Swathi;password=SwathiSwathi;";
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url);
			if(connection != null){
				System.out.println("connected");
				
			}else{
				System.out.println("Failed");
			}
	      
	      Statement stmt2 = connection.createStatement();
	      
	      if(prob_type == 1)
	      {
	    	  stmt2.executeUpdate("update patient_records set problem_type='PPDA PRESENT' where name='"+name+"' ");
	    	  
	      }
	      else
	      {
	    	  stmt2.executeUpdate("update patient_records set problem_type='PPDA NOT PRESENT' where name='"+name+"' ");
	      }
      
	 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}//end of class Eigth



