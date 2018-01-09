package com.unl.onto;
// declare the import statements

import java.util.Iterator;
import java.io.*;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class ColcanOntology
{

	//declare the global variables
	
	
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
		try {
		//Create the ontology, classes, subclasses and their ptoperties
		  String uri = "file:/home/ssamal/workspace/conexpng/onto/OntologyColcan.owl";
		  OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
			
		  	// create the entire class structure of the ontology
			OWLNamedClass patient_dgs = owlModel.createOWLNamedClass("patient_diagnosis");
			OWLNamedClass name = owlModel.createOWLNamedSubclass("name", patient_dgs);
			OWLNamedClass gender = owlModel.createOWLNamedSubclass("gender", patient_dgs);
			OWLNamedClass age = owlModel.createOWLNamedSubclass("age", patient_dgs);
			OWLNamedClass ph_any_cancer = owlModel.createOWLNamedSubclass("ph_any_cancer", patient_dgs);
			OWLNamedClass ph_any_colo = owlModel.createOWLNamedSubclass("ph_any_colo", patient_dgs);
			OWLNamedClass llessize = owlModel.createOWLNamedSubclass("llessize", patient_dgs);
			OWLNamedClass polyp_size = owlModel.createOWLNamedSubclass("polyp_size", patient_dgs);
			OWLNamedClass polyp_number = owlModel.createOWLNamedSubclass("polyp_number", patient_dgs);
			OWLNamedClass dgs_location = owlModel.createOWLNamedSubclass("dgs_location", patient_dgs);
			OWLNamedClass dgs_procedure = owlModel.createOWLNamedSubclass("dgs_procedure", patient_dgs);
			OWLNamedClass dgs_type = owlModel.createOWLNamedSubclass("dgs_type", patient_dgs);
			OWLNamedClass dgs_stage = owlModel.createOWLNamedSubclass("dgs_stage", patient_dgs);
			
			   
			OWLNamedClass treatment_log = owlModel.createOWLNamedClass("Treatment_Log");
			OWLNamedClass patient_trt = owlModel.createOWLNamedClass("Patient_Treatment");

			printClassTree(patient_dgs, "");
			

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
		
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
	    
		try {		
		      
//	      if(prob_type == 1)
//	      {
//	    	  stmt2.executeUpdate("update patient_records set problem_type='PPDA PRESENT' where name='"+name+"' ");
//	    	  
//	      }
//	      else
//	      {
//	    	  stmt2.executeUpdate("update patient_records set problem_type='PPDA NOT PRESENT' where name='"+name+"' ");
//	      }
//      
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	
}//end of class Eigth



