/*
 * Cameron Boyd
 * CECS 444
 * Full Scanner
 * 
 * Pulls state, action, and lookup tables from 
 * external Excel file. 
 * External text file is scanned for tokens
 * defined in the tables. 
 * A HashMap data structure saves all user-
 * defined identifiers and the count of times
 * that they have appeared.
 * The UI displays the tokens in text areas, 
 * where HTML is used to color certain token 
 * names.
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.io.IOException;
import java.net.*;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import jxl.read.biff.BiffException;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;

public class ScannerClass {
    private String token_under_construction;
    private int state;
    private int current_read;
     
    private File file;
    private String resultString;
    private String idTableString;
    
    private HashMap<String, Integer> reservedWordStore;
    private HashMap<String, Integer> userDefinedWords;
    
    private Workbook stateTable;
    private Workbook actionTable;
    private Workbook lookupTable;
    
    private int[][] state_table;
    private int[][] action_table;
    private int[][] look_up_table;
     
    ScannerClass(){
        token_under_construction = "";
        state = 0;
        current_read = 0;
         
        resultString = "";
        idTableString = "";
        
        reservedWordStore = new HashMap<String,Integer>();
        userDefinedWords = new HashMap<String,Integer>();
        
        try {
			stateTable = Workbook.getWorkbook(new File("C:\\Users\\Prince Cameron\\Downloads\\State Table.xls"));
			actionTable = Workbook.getWorkbook(new File("C:\\Users\\Prince Cameron\\Downloads\\Action Table.xls"));
			lookupTable = Workbook.getWorkbook(new File("C:\\Users\\Prince Cameron\\Downloads\\Lookup Table.xls"));
        } catch (BiffException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        state_table = new int[169][32];
		for(int i=1;i<170;i++){
			for(int j=1;j<33;j++){
				state_table[i-1][j-1] = 
						Integer.parseInt(stateTable.getSheet(0).getCell(j, i).getContents());
				
			}
		}
		for(int i=0; i<state_table.length;i++){
	    	   for(int j=0;j<state_table[i].length;j++){
	    		   if(state_table[i][j] != -1){
	    			   state_table[i][j] -= 1;
	    		   }
	    	   }
	       }
		
		action_table = new int[169][32];
		for(int i=1;i<170;i++){
			for(int j=1;j<33;j++){
				action_table[i-1][j-1] = 
						Integer.parseInt(actionTable.getSheet(0).getCell(j, i).getContents());
				
			}
		}
		
		look_up_table = new int[169][32];
		for(int i=1;i<170;i++){
			for(int j=1;j<33;j++){
				look_up_table[i-1][j-1] = 
						Integer.parseInt(lookupTable.getSheet(0).getCell(j, i).getContents());
				
			}
		}
    }
     
    public void read_characters(String filename) throws IOException{
        char current_char = ' ';
        boolean buffered = false;
        boolean eof = false;
        
        boolean comment = false;
         
        BufferedReader buffer = new BufferedReader(
                new FileReader(filename));
        int c = 0;
        while(!eof){
                 
            if(!buffered||(current_char == ' ')||(current_char == '\n')
            		||(current_char == 13) || (current_char == '\t')){
                c=buffer.read();
                current_char = (char)c;
                 
            }   
            if(c==-1){
                eof = true;
            }
                         
//          System.out.println("current_char = " + current_char);
          System.out.println("Status of EOF = " + eof + " " + c);
             
            if(current_char == '/'){
                current_read = 0;
            }
            else if(Character.isDigit(current_char)){
            	current_read = 1;
            }
            else if(current_char == '$'){
            	current_read = 2;
            }
            else if(Character.isAlphabetic(current_char)){
            	current_read = 3;
            }
            else if(current_char == '{'){
            	current_read = 4;
            }
            else if(current_char == '"'){
            	current_read = 5;
            }
            else if(current_char == '#'){
            	current_read = 6;
            }
            else if(current_char == '['){
            	current_read = 7;
            }
            else if(current_char == '-'){
            	current_read = 8;
            }
            else if(current_char == '+'){
            	current_read = 9;
            }
            else if(current_char == '>'){
            	current_read = 10;
            }
            else if(current_char == '<'){
            	current_read = 11;
            }
            else if(current_char == '!'){
            	current_read = 12;
            }
            else if(current_char == '='){
            	current_read = 13;
            }
            else if(current_char == ':'){
            	current_read = 14;
            }
            else if(current_char == '\''){
            	current_read = 15;
            }
            else if(current_char == ']'){
            	current_read = 16;
            }
            else if(current_char == '}'){
            	current_read = 17;
            }
            else if(current_char == '@'){
            	current_read = 18;
            }
            else if(current_char == '&'){
            	current_read = 19;
            }
            else if(current_char == '~'){
            	current_read = 20;
            }
            else if(current_char == ';'){
            	current_read = 21;
            }
            else if(current_char == '.'){
            	current_read = 22;
            }
            else if(current_char == ','){
            	current_read = 23;
            }
            else if(current_char == '*'){
            	current_read = 24;
            }
            else if(current_char == '^'){
            	current_read = 25;
            }
            else if(current_char == '('){
            	current_read = 26;
            }
            else if(current_char == ')'){
            	current_read = 27;
            }
            else if(current_char == '_'){
            	current_read = 28;
            }
            else if(current_char == '\\'){
            	current_read = 29;
            }
            else if(current_char == ' '){
            	current_read = 30;
            }
            else{
            	current_read = 31;
            }
           
          System.out.println("current state = " + (state+1) + "\n" + 
                              "current_char = " + current_char + "\n" +
                              "current_read = " + current_read + "\n" +
                              "token status = " + token_under_construction);
            if((next_state(state, current_read)!=-1)&&(action(state,current_read)==1)){
                buffered = false;
                token_under_construction = token_under_construction + current_char;
                state = next_state(state, current_read);
            }
            else if((next_state(state, current_read)==-1)&&(action(state,current_read)==2)){
              System.out.println("inside switch with state = " + state + " and char " +
                                  current_read);
              System.out.println("The lookup value is = " + look_up(state, current_read));
              System.out.println("We have a buffered character = \""+current_char+"\"");
                buffered = true;
                 
                print_discovery(state, current_read);
                 
                state = 0;
                token_under_construction = "";
            }
            else if((next_state(state, current_read)==-1)&&(action(state,current_read)!=2)){
            	System.out.println("There was an error");
            	buffered = false;
                                 
                state = 0;
                token_under_construction = "";
            }
             
//          System.out.println(current_read);
        }
 
        buffer.close();
        System.out.println("done scanning");
 
    }
     
    private int next_state(int new_state,int new_char){
    	return initFullStateTable(new_state, new_char);
    }
     
    private int action(int new_state,int new_char){
    	return initFullActionTable(new_state, new_char);
    }
     
    private int look_up(int new_state,int new_char){
    	return initFullLookupTable(new_state, new_char);
    }
     
    private void print_discovery(int new_state,int new_char){
    	
    	switch(look_up(new_state, new_char)){
        case 1:
            System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 2: 
            System.out.println("TOKEN DISCOVERED is COMMENT " + token_under_construction);
            resultString += "TOKEN DISCOVERED is <font color=green>COMMENT</font> " + token_under_construction + "<br>\n";
            break;
        case 3:
            System.out.println("TOKEN DISCOVERED is COMPOUND OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPOUND OP " + token_under_construction + "<br>\n";
            break;
        case 4:
            System.out.println("TOKEN DISCOVERED is COMMENT " + token_under_construction);
            resultString += "TOKEN DISCOVERED is <font color=green>COMMENT</font> " + token_under_construction + "<br>\n";
            break;
        case 5:
            System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 6:
            System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 7:
            System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 8:
            System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 9:
            System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 10:
            System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 11:
            System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 12:
            System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 13:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 14:
        	System.out.println("TOKEN DISCOVERED is SCIENTIFIC NOTATION " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SCIENTIFIC NOTATION " + token_under_construction + "<br>\n";
            break;
        case 15:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 16:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 17:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 18:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 19:
        	System.out.println("TOKEN DISCOVERED is SCIENTIFIC NOTATION " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SCIENTIFIC NOTATION " + token_under_construction + "<br>\n";
            break;
        case 20:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 21:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 22:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 23:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 24:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 25:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED REAL " + token_under_construction + "<br>\n";
            break;
        case 26:
        	System.out.println("TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction);
            resultString += "TOKEN DISCOVERED is UNSIGNED INTEGER " + token_under_construction + "<br>\n";
            break;
        case 27:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 28:
        	System.out.println("TOKEN DISCOVERED is CURRENCY " + token_under_construction);
            resultString += "TOKEN DISCOVERED is CURRENCY " + token_under_construction + "<br>\n";
            break;
        case 29:
        	System.out.println("TOKEN DISCOVERED is CURRENCY " + token_under_construction);
            resultString += "TOKEN DISCOVERED is CURRENCY " + token_under_construction + "<br>\n";
            break;
        case 30:
        	System.out.println("TOKEN DISCOVERED is CURRENCY " + token_under_construction);
            resultString += "TOKEN DISCOVERED is CURRENCY " + token_under_construction + "<br>\n";
            break;
        case 31:
        case 32:
        case 33:
        case 34:
        	System.out.println("TOKEN DISCOVERED is IDENTIFIER " + token_under_construction + "<br>\n");
            resultString += "TOKEN DISCOVERED is IDENTIFIER " + token_under_construction;
            if(reservedWordStore.containsKey(token_under_construction)){
            	resultString+=" and is " + "<font color=purple>RESERVED</font>" + "<br>\n";
            }else if(userDefinedWords.containsKey(token_under_construction)){
            	int count = userDefinedWords.get(token_under_construction);
            	userDefinedWords.put(token_under_construction, count+1);
            	resultString+=", is <font color=blue>USER_DEFINED</font> and has appeared " + count + " times" + "<br>\n";
            }else{
            	userDefinedWords.put(token_under_construction, 1);
            	resultString+=" and is a <font color=red>new USER-DEFINED IDENTIFIER</font>" + "<br>\n";
            }
            break;
        case 35:
        	System.out.println("TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction);
            resultString += "TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction + "<br>\n";
            break;
        case 36:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 37:
        	System.out.println("TOKEN DISCOVERED is COMMENT " + token_under_construction);
            resultString += "TOKEN DISCOVERED is <font color=green>COMMENT</font> " + token_under_construction + "<br>\n";
            break;
        case 38:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 39:
        	System.out.println("TOKEN DISCOVERED is STRING LITERAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is STRING LITERAL " + token_under_construction + "<br>\n";
            break;
        case 40:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 41:
        	System.out.println("TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction);
            resultString += "TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction + "<br>\n";
            break;
        case 42:
        	System.out.println("TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction);
            resultString += "TOKEN DISCOVERED is LIBRARY TOKEN " + token_under_construction + "<br>\n";
            break;
        case 43:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 44:
        	System.out.println("TOKEN DISCOVERED is NEGATIVE INTEGER" + token_under_construction);
            resultString += "TOKEN DISCOVERED is NEGATIVE INTEGER " + token_under_construction + "<br>\n";
            break;
        case 45:
        	System.out.println("TOKEN DISCOVERED is FILE TOKEN" + token_under_construction);
            resultString += "TOKEN DISCOVERED is FILE TOKEN " + token_under_construction + "<br>\n";
            break;
        case 46:
        	System.out.println("TOKEN DISCOVERED is NEGATIVE INTEGER" + token_under_construction);
            resultString += "TOKEN DISCOVERED is NEGATIVE INTEGER " + token_under_construction + "<br>\n";
            break;
        case 47:
        	System.out.println("TOKEN DISCOVERED is NEGATIVE INTEGER" + token_under_construction);
            resultString += "TOKEN DISCOVERED is NEGATIVE INTEGER " + token_under_construction + "<br>\n";
            break;
        case 48:
        	System.out.println("TOKEN DISCOVERED is NEGATIVE REAL" + token_under_construction);
            resultString += "TOKEN DISCOVERED is NEGATIVE REAL " + token_under_construction + "<br>\n";
            break;
        case 49:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 50:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 51:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 52:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 53:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 54:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 55:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 56:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 57:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 58:
        	System.out.println("TOKEN DISCOVERED is NEGATIVE REAL " + token_under_construction);
            resultString += "TOKEN DISCOVERED is NEGATIVE REAL " + token_under_construction + "<br>\n";
            break;
        case 59:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP &lt;" + token_under_construction + "<br>\n";
            break;
        case 60:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP &lt;&lt;" + token_under_construction + "<br>\n";
            break;
        case 61:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP " + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 62:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP &lt;&gt;" + token_under_construction + "<br>\n";
            break;
        case 63:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 64:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 65:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 66:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 67:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 68:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 69:
        	System.out.println("TOKEN DISCOVERED is COMPLEX OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is COMPLEX OP " + token_under_construction + "<br>\n";
            break;
        case 70:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 71:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 72:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 73:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 74:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 75:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 76:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 77:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 78:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 79:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 80:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 81:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 82:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 83:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        case 84:
        	System.out.println("TOKEN DISCOVERED is SIMPLE OP" + token_under_construction);
            resultString += "TOKEN DISCOVERED is SIMPLE OP " + token_under_construction + "<br>\n";
            break;
        default:
            System.out.println("error");
    }
//      System.out.println("state = " + state + " current_read");
    }
     
    public void initUI(){
    	//Reserved words
    	File reservedWords = new File("reservedWords.txt");
    	try {
			Scanner scanner = new Scanner(reservedWords);
			while(scanner.hasNext()){
				String next = scanner.next();
				reservedWordStore.put(next, 0);
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
        JFrame frame = new JFrame();
        final JPanel panel = new JPanel();
        
        final JTextArea txtArea = new JTextArea(40,25);
        JScrollPane scroll = new JScrollPane(txtArea);
         
        final JTextArea resultArea = new JTextArea(40,25);
        JScrollPane scroll2 = new JScrollPane(resultArea);
        
        final JTextPane resultPane = new JTextPane();
        resultPane.setSize(40,25);
        resultPane.setContentType("text/html");
        JScrollPane scroll4 = new JScrollPane(resultPane);
        scroll4.getViewport().setPreferredSize(new Dimension(300,650));
        
        final JTextArea identifierTableArea = new JTextArea(40,25);
        JScrollPane scroll3 = new JScrollPane(identifierTableArea);
         
        frame.add(panel);
         
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        String userhome = System.getProperty("user.home");
        final JFileChooser fc = new JFileChooser(userhome + "\\workspace\\Full Scanner");
     
        JButton scanBtn = new JButton("Open file to scan...");
        scanBtn.addActionListener(new ActionListener() {
             
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                int returnVal = fc.showOpenDialog(panel);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    file = fc.getSelectedFile();
                    System.out.println(file);
                     
                    try {
                        Scanner scanner = new Scanner(file);
                        String appendString = "";
                         
                        while(scanner.hasNext()){
                            String oneString = scanner.nextLine();
                            txtArea.append(oneString + "\n");
                        }
                         
                        try {
                            read_characters(file.toString());
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                         
                        resultArea.append(resultString);
                        resultPane.setText(resultString);
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                    //Print ID table
                    Map<String,Integer> map = new TreeMap<String,Integer>(userDefinedWords);
                    identifierTableArea.append("Number of Occurences " + "\t" +
                    							"Symbol" + "\n\n");
                    Iterator iter = 
                    		map.entrySet().iterator();
                   while(iter.hasNext()){
                	   Map.Entry pair = 
                			   (Map.Entry)iter.next();
                	   identifierTableArea.append(
                			   pair.getValue() + "\t\t" + pair.getKey() + "\n");
                   }
                    
                }
            }
            
        });
         
        panel.add(scanBtn);
        panel.add(scroll);
//        panel.add(scroll2);
        panel.add(scroll4);
        panel.add(scroll3);
         
        frame.setSize(1400, 800);
        frame.setVisible(true);
        
        
    }
    
	private int initFullStateTable(int new_state,int new_char){
	       return state_table[new_state][new_char];
	}
	
	private int initFullActionTable(int new_state,int new_char){
        return action_table[new_state][new_char];
	}
	
	private int initFullLookupTable(int new_state,int new_char){
		return look_up_table[new_state][new_char];
	}
}