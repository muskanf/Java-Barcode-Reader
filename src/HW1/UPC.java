package HW1;
import java.util.Arrays; //i used this for testing purposes earlier
// This is the starting version of the UPC-A scanner
//   that needs to be filled in for the homework

//"""
//Description of program: Project 1 - UPC Code
//Filename: Project 1 - Muskan Fatima
//Author: Muskan Fatima
//Date: SEP 2023
//Course: JAVA
//Assignment: Project 1
//Collaborators: I used help from Stack overflow, I worked with my friend Elizabeth, and I also took help from our TA. 
//
//"""



public class UPC { 
	//--------------------------------------------
	// Scan in the bit pattern from the image
	// Takes the filename of the image
	// Returns an in array of the 95 scanned bits
	//--------------------------------------------
    public static int[] scanImage(String filename) {
        DUImage image = new DUImage(filename);
        int[] scanPattern = new int[95];  // Initialize an array to store the scanned pattern
        
        // Define constants for barcode properties
        int startX = 5;   // Start scanning at pixel 5
        int pixelWidth = 2;  // Each bar (or space) is 2 pixels wide
        
        // Loop through the barcode, scanning left to right
        for (int i = 0; i < 95; i++) {
            int x = startX + i * pixelWidth;  // Calculate the X coordinate for the current bit and scan whatever is in the middle
            int y = image.getHeight() / 2;   
            int redValue = image.getRed(x, y);  // Get the red component of the color
            
            // Determine whether the pixel is black (1) or white (0) based on the red component
            if (redValue < 125) {
                scanPattern[i] = 1;  // this will be a black pixel upc
            } else {
                scanPattern[i] = 0;  //This one will be considered a white pixel
            }
        }
        
        return scanPattern;
    }

	//--------------------------------------------
	// Finds the matching digit for the given pattern
	// This is a helper method for decodeScan
	// Takes the full 95 scanned pattern as well as
	//   a starting location in that pattern where we
	//   want to look
	// Also takes in a boolean to indicate if this is a
	//   left or right pattern
	// Returns an int indicating which digit matches
	//   Any pattern that doesn't match anything will be -1
	//--------------------------------------------
	
	//I added a helper method to invert the pattern
	
	public static int[] invertPattern(int[] pattern) {
	    for (int i = 0; i < pattern.length; i++) {
	        pattern[i] = pattern[i] == 0 ? 1 : 0; // im using a short form of the if else statment. if it is 0 then make it 1 else 0. I learned this through stack overflow and wikipedia: https://en.wikipedia.org/wiki/Ternary_conditional_operator
	    }
	    return pattern;
	}

	public static int matchPattern(int[] scanPattern, int startIndex, boolean left) {
		//put in one array
		//start index tells you where the pattern starts from in the array
		//start at 0 end at 7
		int[][] digitPat = {{0,0,0,1,1,0,1}, //0
				            {0,0,1,1,0,0,1},//1	
				            {0,0,1,0,0,1,1},//2
				            {0,1,1,1,1,0,1},//3
				            {0,1,0,0,0,1,1},//4
				            {0,1,1,0,0,0,1},//5
				            {0,1,0,1,1,1,1},//6
				            {0,1,1,1,0,1,1},//7
				            {0,1,1,0,1,1,1},//8
				            {0,0,0,1,0,1,1}};//9
		
		
		
		
		
	    int[] pattern = new int[7];
	    for (int i = 0; i < 7; i++) {
	        pattern[i] = scanPattern[startIndex + i];
	    }
	    // Check if startIndex + i is within the array bounds
	    for (int i = 0; i < 7 && startIndex + i < scanPattern.length; i++) {
	        pattern[i] = scanPattern[startIndex + i];
	    }
	    // Invert the pattern if the first bit is 1
	    if (!left && pattern[0] == 1) {
	        pattern = invertPattern(pattern);
	    }
	    
	    for (int i = 0; i < digitPat.length; i++) {
	        int[] currentPattern = digitPat[i];

	        boolean match = true;
	        for (int j = 0; j < 7; j++) {
	            if (pattern[j] != currentPattern[j]) {
	                match = false;
	                break;
	            }
	        }

	        if (match) {
	            return i; // Return the digit index if there is a match
	        }
	    }

	    // Return -1 if no pattern is found
	    return -1;
	}
	
	//--------------------------------------------
	// Performs a full scan decode that turns all 95 bits
	//   into 12 digits
	// Takes the full 95 bit scanned pattern
	// Returns an int array of 12 digits
	//   If any digit scanned incorrectly it is returned as a -1
	// If the start, middle, or end patterns are incorrect
	//   it provides an error and exits
	//--------------------------------------------
	public static int[] decodeScan(int[] scanPattern) {
	    if (scanPattern.length != 95) {
	        System.out.println("Error: Invalid scan pattern length.");
	        System.exit(1);
	    }

	    int[] decodedDigits = new int[12];
	    for (int i = 0; i < 6; i++) { // Decode the left side
	        int start = 3 + i * 7; // Skip the first 3 bits for start code and then for each digit, skip 7 bits
	        decodedDigits[i] = matchPattern(scanPattern, start, true);
	    }
	    for (int i = 0; i < 6; i++) { // Decode the right side
	        int start = 50 + i * 7; // Skip the first 50 bits for start code, left side, and middle guard pattern
	        decodedDigits[i+6] = matchPattern(scanPattern, start, false);
	    }

	    return decodedDigits;
	}
//
//	//--------------------------------------------
//	// Do the checksum of the digits here
//	// All digits are assumed to be in range 0..9
//	// Returns true if check digit is correct and false otherwise
//	//--------------------------------------------
	public static boolean verifyCode(int[] digits) {
	    int sum = 0;
	    for (int i = 0; i < digits.length - 1; i++) {
	        if (i % 2 == 0) {
	            sum += digits[i] * 3;
	        } else {
	            sum += digits[i];
	        }
	    }
	    int checkDigit = sum % 10;
	    if (checkDigit != 0) {
	        checkDigit = 10 - checkDigit;
	    }
	    return checkDigit == digits[digits.length - 1];
	}
	
	//--------------------------------------------
	// The main method scans the image, decodes it,
	//   and then validates it
	//--------------------------------------------	
public static void main(String[] args) {
	        // file name to process.
	        // Note: change this to other files for testing
	        String barcodeFileName = "barcodeUpsideDown.png";
//
//	        // optionally get file name from command-line args
	        if(args.length == 1){
		    barcodeFileName = args[0];
		}
		
//		// scanPattern is an array of 95 ints (0..1)
		int[] scanPattern = scanImage(barcodeFileName);
//
//		// Display the bit pattern scanned from the image
		System.out.println("Original scan");
		for (int i=0; i<scanPattern.length; i++) {
			System.out.print(scanPattern[i]);
		}
		System.out.println(""); // the \n
				
		
//		// digits is an array of 12 ints (0..9)
		int[] digits = decodeScan(scanPattern);
//		
//		// YOUR CODE HERE TO HANDLE UPSIDE-DOWN SCANS	
		
	    // Check if the first digit is -1
	    if (digits[0] == -1) {
	        // If it is, reverse the scanPattern array
	        for (int i = 0; i < scanPattern.length / 2; i++) {
	            int temp = scanPattern[i];
	            scanPattern[i] = scanPattern[scanPattern.length - 1 - i];
	            scanPattern[scanPattern.length - 1 - i] = temp;
	        }
	        // Decode the scan again
	        digits = decodeScan(scanPattern);
	    }
		
		
//		// Display the digits and check for scan errors
		boolean scanError = false;
		System.out.println("Digits");
		for (int i=0; i<12; i++) {
			System.out.print(digits[i] + " ");
			if (digits[i] == -1) {
				scanError = true;
			}
		}
		System.out.println("");
				
		if (scanError) {
			System.out.println("Scan error");
			
		} else { // Scanned in correctly - look at checksum
		
			if (verifyCode(digits)) {
				System.out.println("Passed Checksum");
			} else {
				System.out.println("Failed Checksum");
			}
		}
	}
}

//I was just using this to test the matchpattern method
	
//public static void main(String[] args) {
//    int[] scanPattern = {1,0,1,0,0,1,1,0,0,1,0,0,1,0,0,1,1,0,1,1,1,1,0,1,0,1,0,0,0,1,1,0,1,1,0,0,0,1,0,1,0,1,1,1,1,0,1,0,1,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1,1,1,0,1,0,0,1,1,1,0,0,1,1,0,0,1,1,0,1,1,0,1,1,0,0,1,1,0,1};
//    int startIndex = 0;
//    boolean left = true;
//
//    int match = matchPattern(scanPattern, startIndex, left);
//    System.out.println("Matched digit: " + match);
//}